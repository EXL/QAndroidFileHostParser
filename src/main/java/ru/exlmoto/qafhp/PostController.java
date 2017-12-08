package ru.exlmoto.qafhp;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

/**
 * Created by exlmo on 08.12.2017.
 */

public class PostController {
    @FXML
    private TextArea textAreaReq = null;

    @FXML
    private TextArea textAreaAns = null;

    @FXML
    private Label labelStatusBar = null;

    @FXML
    private TextField textFieldMethod = null;

    @FXML
    private Spinner spinnerTimeout = null;

    @FXML
    private VBox rootPost = null;

    private boolean curlCompressed = false;
    private int connectionTimeout = 0;
    private String requestMethod = null;

    private Map<String, String> parsedArgs = new HashMap<>();
    private List<String> arguments = new ArrayList<>();

    private ExecutorService exec = Executors.newSingleThreadExecutor(r -> {
        Thread t = new Thread(r);
        t.setDaemon(true);
        return t;
    });

    @FXML
    private void initialize() {
        Platform.runLater(() -> textAreaReq.requestFocus());
        textAreaReq.setWrapText(true);
        textAreaAns.setWrapText(true);
        textAreaReq.setText(PageTemplate.curlReq);
        spinnerTimeout.getEditor().setText("" + PageTemplate.conTimeout);
    }

    @FXML
    private void buttonSendReq() {
        String curl = textAreaReq.getText().trim();
        if (curl.isEmpty()) {
            labelStatusBar.setText("Error: request is empty.");
            return;
        }
        disableAllElements(true);
        try {
            connectionTimeout = Integer.valueOf(spinnerTimeout.getEditor().getText());
            requestMethod = textFieldMethod.getText();
            curlCompressed = curl.contains("--compressed");
            fillArgs(curl);
            exec.submit(createReqTask());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void buttonClearReq() {
        textAreaReq.clear();
    }

    @FXML
    private void buttonClearAns() {
        textAreaAns.clear();
    }

    private void fillArgs(String curl) {
        arguments.clear();
        parsedArgs.clear();
        // https://stackoverflow.com/a/1473198
        Pattern p = Pattern.compile("\'([^\']*)\'");
        Matcher m = p.matcher(curl);
        while (m.find()) {
            arguments.add(m.group(1));
        }
        int b = arguments.size() - 1;
        for (int i = 1; i < b; ++i) { // Drop first and last elements
            String handle = arguments.get(i);
            String[] strs = handle.split(":");
            String first = strs[0].trim();
            parsedArgs.put(first, handle.replace(first + ":", "").trim());
        }
    }

    private String sendPost(String url, String data, Map<String, String> properties) throws Exception {
        URL obj = new URL(url);
        try {
            HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
            con.setRequestMethod(requestMethod);
            for (Map.Entry<String, String> entry : properties.entrySet()) {
                con.setRequestProperty(entry.getKey(), entry.getValue());
            }
            con.setConnectTimeout(connectionTimeout * 100);
            con.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(data);
            wr.flush();
            wr.close();
            int responseCode = con.getResponseCode();
            Platform.runLater(() -> textAreaAns.appendText("Response code: " + responseCode + "\n"));
            if (curlCompressed) {
                InputStream ungzippedResponse = new GZIPInputStream(con.getInputStream());
                Reader reader = new InputStreamReader(ungzippedResponse, "UTF-8");
                Writer writer = new StringWriter();
                char[] buffer = new char[10240];
                for (int length; (length = reader.read(buffer)) > 0; ) {
                    writer.write(buffer, 0, length);
                }
                reader.close();
                ungzippedResponse.close();
                return writer.toString();
            } else {
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                return response.toString();
            }
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    private Task<Void> createReqTask() {
        return new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                Platform.runLater(() -> labelStatusBar.setText("Working..."));
                String serverAnswer = sendPost(arguments.get(0), arguments.get(arguments.size() - 1), parsedArgs);
                Platform.runLater(() -> {
                    textAreaAns.appendText(serverAnswer + "\n\n\n");
                    disableAllElements(false);
                    labelStatusBar.setText("Done!");
                });
                return null;
            }
        };
    }

    private void disableAllElements(boolean disable) {
        rootPost.setDisable(disable);
    }
}

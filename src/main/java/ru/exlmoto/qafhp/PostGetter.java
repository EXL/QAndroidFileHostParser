package ru.exlmoto.qafhp;

import javafx.concurrent.Task;

import javax.net.ssl.HttpsURLConnection;

import java.io.*;
import java.net.URL;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.zip.GZIPInputStream;

// https://stackoverflow.com/a/26611622
public class PostGetter {
    private AtomicInteger taskCount = new AtomicInteger(0);

    private GuiController guiController = null;
    private List<String> fids = null;

    private ExecutorService exec = Executors.newSingleThreadExecutor(r -> {
        Thread t = new Thread(r);
        t.setDaemon(true); // allows app to exit if tasks are running
        return t;
    });

    public PostGetter(GuiController gui, List<String> fids) {
        this.guiController = gui;
        this.fids = fids;
    }

    private Task<Void> createTask() {
        final int taskNumber = taskCount.incrementAndGet();
        return new Task<Void>() {
            @Override
            public Void call() throws Exception {
                for (int count=0; count<2; count++) {
                    Thread.sleep(PageTemplate.postDelay);
                    sendPost("889964283620770242");
                }
                return null;
            }
        };
    }

    // HTTP POST request
    private void sendPost(String fid) throws Exception {
        URL obj = new URL(PageTemplate.curlUrl);

        HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Cookie", PageTemplate.curlCookie);
        con.setRequestProperty("Origin", PageTemplate.justUrl);
        con.setRequestProperty("Accept-Encoding", PageTemplate.curlAe);
        con.setRequestProperty("Accept-Language", PageTemplate.curlAl);
        con.setRequestProperty("Content-Type", PageTemplate.curlContentType);
        con.setRequestProperty("X-Mod-Sbb-Ctype", PageTemplate.curlXmod);
        con.setRequestProperty("Accept", PageTemplate.curlA);
        con.setRequestProperty("Referer", PageTemplate.justUrl + "?fid=" + fid);
        con.setRequestProperty("Authority", PageTemplate.curlAuthority);
        con.setRequestProperty("X-Requested-With", PageTemplate.curlXreq);
        con.setRequestProperty("User-Agent", PageTemplate.curlUa);
        con.setDoOutput(true);

        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(PageTemplate.curlData + fid);
        wr.flush();
        wr.close();

        InputStream ungzippedResponse = new GZIPInputStream(con.getInputStream());

        Reader reader = new InputStreamReader(ungzippedResponse, "UTF-8");
        Writer writer = new StringWriter();

        String body = null;
        char[] buffer = new char[10240];
        for (int length = 0; (length = reader.read(buffer)) > 0;) {
            writer.write(buffer, 0, length);
        }
        body = writer.toString();
        reader.close();
        ungzippedResponse.close();

        //print result
        System.out.println(body);
    }

    public void startWork() {
        Task<Void> task = createTask();
        exec.submit(task);
    }
}

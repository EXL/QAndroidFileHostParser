package ru.exlmoto.qafhp;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.zip.GZIPInputStream;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker.State;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;

import javax.net.ssl.HttpsURLConnection;

public class PageWalker {
    private WebView webView = null;
    private WebEngine webEngine = null;
    private GuiController guiController = null;

    private ChangeListener<State> initialListener = null;
    private ChangeListener<State> pageLinksListener = null;

    private ArrayList<FlashClass> flashClassArray = null;

    private void getDirectLinksWithPost(List<String> fids) {
        LinkedList<String> fidsStack = new LinkedList<>(fids);

    }

    // https://stackoverflow.com/a/27496335
    private void loadPagesConsecutively(List<String> pages) {
        LinkedList<String> pageStack = new LinkedList<>(pages);
        pageLinksListener = new ChangeListener<State>() {
            @Override
            public void changed(ObservableValue<? extends State> obs, State oldState, State newState) {
                if (newState == State.SUCCEEDED ) {
                    tryGetFileLinks();
                    if (pageStack.isEmpty()) {
                        webEngine.getLoadWorker().stateProperty().removeListener(this);
                        guiController.toLog("Getting SW Links done!");
                        List<String> fids = new ArrayList<>();
                        for (FlashClass aFlashClassArray : flashClassArray) {
                            fids.add(aFlashClassArray.getFid());
                        }
                        getDirectLinksWithPost(fids);
                    } else {
                        guiController.goToUrl(pageStack.pop());
                    }
                }
            }
        };
        webEngine.getLoadWorker().stateProperty().addListener(pageLinksListener);
        guiController.goToUrl(pageStack.pop());
    }

    public PageWalker(WebView webView, WebEngine webEngine, GuiController guiController) {
        this.webView = webView;
        this.webEngine = webEngine;
        this.guiController = guiController;

        flashClassArray = new ArrayList<>();

        initialListener = new ChangeListener<State>() {
            @Override
            public void changed(ObservableValue<? extends State> observable, State oldValue, State newValue) {
                if (newValue == State.SUCCEEDED) {
                    PageTemplate.pageCount = tryGetPageCount();
                    if (tryGetPageCount() != -1) {
                        guiController.toLog("Get num of pages... " + PageTemplate.pageCount + ".");
                        List<String> links = new ArrayList<>();
                        int count = 0;
                        for (int i = PageTemplate.pageStart; i <= PageTemplate.pageStop; i++, count++) {
                            String link = PageTemplate.startUrl + "&page=" + i;
                            links.add(link);
                        }
                        guiController.toLog("Generating links... " + count);
                        webEngine.getLoadWorker().stateProperty().removeListener(this);
                        loadPagesConsecutively(links);
                    } else {
                        guiController.toLog("Get num of pages... fail!");
                    }
                }
            }
        };
    }

    private void tryGetFileLinks() {
        try {
            guiController.toLog(PageTemplate.pageStart + "-- Page");
            String linksArrayDirty = (String) webEngine.executeScript(PageTemplate.scriptGetLinks);
            String[] links = linksArrayDirty.split("\\|");
            for (int i = 0; i < links.length; ++i) {
                String[] sw = links[i].split(";");
                flashClassArray.add(new FlashClass(i+1, sw[1], sw[0]));
            }
            for (FlashClass flash : flashClassArray) {
                if (PageTemplate.settingMirrors) {
                    //tryGetItemMirrors(flash.getFid());
                }
                guiController.toReport(flash.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int tryGetPageCount() {
        try {
            String countOfFiles = (String) webEngine.executeScript(PageTemplate.scriptGetCountOfFiles);
            int filesCount = Integer.valueOf(countOfFiles);
            if (filesCount % PageTemplate.pageItems == 0) {
                return filesCount / PageTemplate.pageItems;
            } else {
                return (filesCount / PageTemplate.pageItems) + 1;
            }
        } catch (Exception e) {
            return -1;
        }
    }

    public void startWork() {
        //guiController.goToUrl(PageTemplate.startUrl);
        //webEngine.getLoadWorker().stateProperty().addListener(initialListener);
        try {
            sendPost();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // HTTP POST request
    private void sendPost() throws Exception {
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
        con.setRequestProperty("Referer", "https://androidfilehost.com/?fid=889964283620770242");
        con.setRequestProperty("Authority", PageTemplate.curlAuthority);
        con.setRequestProperty("X-Requested-With", PageTemplate.curlXreq);
        con.setRequestProperty("User-Agent", PageTemplate.curlUa);

        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(PageTemplate.curlData + "889964283620770242");
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

        //print result
        System.out.println(body);
    }
}

// $url = "/libs/otf/mirrors.otf.php"
// $fid = "962021903579496369"
// $.post($url, { "submit" : "submit" , "action" : "getdownloadmirrors" , "fid" : $fid },
// ---
// $.post("/libs/otf/mirrors.otf.php", { "submit" : "submit" , "action" : "getdownloadmirrors" , "fid" : 962021903579496369 },
// function($response) { console.log($response) });

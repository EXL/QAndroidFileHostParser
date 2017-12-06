package ru.exlmoto.qafhp;

import org.json.JSONArray;
import org.json.JSONObject;

import javafx.concurrent.Task;

import javax.net.ssl.HttpsURLConnection;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
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
    private PageWalker pageWalker = null;

    private ExecutorService exec = Executors.newSingleThreadExecutor(r -> {
        Thread t = new Thread(r);
        t.setDaemon(true);
        return t;
    });

    public PostGetter(GuiController gui, List<String> fids, PageWalker pageWalker) {
        this.guiController = gui;
        this.fids = fids;
        this.pageWalker = pageWalker;
    }

    private Task<Void> createTask() {
        final int taskNumber = taskCount.incrementAndGet();
        return new Task<Void>() {
            @Override
            public Void call() throws Exception {
                guiController.toLog("=== Start Part 2");
                for (int i = 0; i < fids.size(); i++) {
                    Thread.sleep(PageTemplate.postDelay * 100);
                    String fid = fids.get(i);
                    if (sendPost(fid, i)) {
                        guiController.toLog("Good " + (i+1) + ": " + fid);
                    } else {
                        guiController.toLog("Fail " + (i+1) + ": " + fid);
                    }
                    if (!PageTemplate.settingMd5) {
                        guiController.toReport(pageWalker.getFlashesArray().get(i).toString());
                    }
                }
                guiController.toLog("=== End Part 2");
                if (PageTemplate.settingMd5) {
                    guiController.toLog("Post Direct Links done!\nGet MD5 Hashes... " + fids.size() + ".");
                    pageWalker.getMd5Hashes();
                }
                return null;
            }
        };
    }

    // HTTP POST request
    private boolean sendPost(String fid, int num) throws Exception {
        URL obj = new URL(PageTemplate.curlUrl);
        try {
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
            con.setConnectTimeout(PageTemplate.conTimeout * 100);
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
            for (int length = 0; (length = reader.read(buffer)) > 0; ) {
                writer.write(buffer, 0, length);
            }
            body = writer.toString();
            reader.close();
            ungzippedResponse.close();

            JSONObject jsonObject = new JSONObject(body);
            JSONArray jsonArray = jsonObject.getJSONArray("MIRRORS");
            List<String> directLinks = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                directLinks.add(jsonArray.getJSONObject(i).getString("url"));
            }
            pageWalker.getFlashesArray().get(num).setDirectLinks(directLinks);

            return true;
        } catch (Exception e) {
            List<String> error = new ArrayList<>();
            error.add("undefined");
            pageWalker.getFlashesArray().get(num).setDirectLinks(error);
            guiController.toLog(e.toString());
            return false;
        }
    }

    public void startWork() {
        Task<Void> task = createTask();
        exec.submit(task);
    }
}

//{
//    "STATUS": "1",
//    "CODE": "200",
//    "MESSAGE": "success, #winning",
//    "AUTO_START": "FALSE",
//    "MIRRORS": [{
//    "mid": "90",
//    "name": "Quebec, Canada #1",
//    "abbrev": "QC1",
//    "address": "http:\/\/qc1.androidfilehost.com",
//    "path": "",
//    "mirror_status": "1",
//    "selectable": "1",
//    "archive": "1",
//    "weight": "5",
//    "temporary": "0",
//    "type": "1",
//    "url": "http:\/\/qc1.androidfilehost.com\/dl\/0YTR1wT3uLVyKto-mMqiZw\/1512744007\/23991606952606298\/CFC-fastboot_falcon_verizon_user_4.4.2_KXB20.9-1.10-1.20_22_release-keys.xml.zip"
//    }]
//}
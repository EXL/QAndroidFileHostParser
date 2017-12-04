package ru.exlmoto.qafhp;

import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.URL;

public class PageWalker {

    private String startUrl = "https://www.androidfilehost.com/?w=search&s=.xml.zip&type=files";

    private WebView webView = null;
    private WebEngine webEngine = null;

    public PageWalker(WebView webView, WebEngine webEngine) {
        this.webView = webView;
        this.webEngine = webEngine;
    }

    public void startWalk() {
        webEngine.load(startUrl);
    }
}

// $url = "/libs/otf/mirrors.otf.php"
// $fid = "962021903579496369"
// $.post($url, { "submit" : "submit" , "action" : "getdownloadmirrors" , "fid" : $fid },
// function($response) { }

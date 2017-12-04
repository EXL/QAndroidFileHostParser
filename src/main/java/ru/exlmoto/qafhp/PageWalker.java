package ru.exlmoto.qafhp;

import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

public class PageWalker {
    private WebView webView = null;
    private WebEngine webEngine = null;

    public PageWalker(WebView webView, WebEngine webEngine) {
        this.webView = webView;
        this.webEngine = webEngine;
    }

    public void startWalk() {
        webEngine.load(PageTemplate.startUrl);
    }
}

// $url = "/libs/otf/mirrors.otf.php"
// $fid = "962021903579496369"
// $.post($url, { "submit" : "submit" , "action" : "getdownloadmirrors" , "fid" : $fid },
// function($response) { }

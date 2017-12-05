package ru.exlmoto.qafhp;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.concurrent.Worker.State;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import netscape.javascript.JSException;
import netscape.javascript.JSObject;

public class PageWalker {
    private WebView webView = null;
    private WebEngine webEngine = null;
    private GuiController guiController = null;

    public class JavaBridge {
        public void log(String text) {
            System.out.println(text);
        }
    }

    enum WorkerState { PAGE_C, PAGE_S,  ITEM }

    private WorkerState workerState = WorkerState.PAGE_C;

    public PageWalker(WebView webView, WebEngine webEngine, GuiController guiController) {
        this.webView = webView;
        this.webEngine = webEngine;
        this.guiController = guiController;

        this.webEngine.getLoadWorker().stateProperty().addListener(new ChangeListener<Worker.State>() {
            @Override
            public void changed(ObservableValue<?extends State> observableValue, State oldState, State state) {
                switch (state) {
                    case SUCCEEDED: {
                        switch (workerState) {
                            case PAGE_C: {
                                int pageCount = tryGetPageCount();
                                if (pageCount != -1) {
                                    guiController.toLog("Page Count... " + pageCount + ".");
                                    workerState = WorkerState.ITEM;
                                    webEngine.load("https://androidfilehost.com/?fid=889964283620770242");
                                } else {
                                    guiController.toLog("Page Count... fail!");
                                }
                                break;
                            }
                            case ITEM: {
                                tryGetItemMirrors();
                                break;
                            }
                        }
                        break;
                    }
                }
            }
        });
    }

    private void tryGetItemMirrors() {
        try {
            JSObject window = (JSObject) webEngine.executeScript("window");
            JavaBridge bridge = new JavaBridge();
            window.setMember("java", bridge);
            webEngine.executeScript("console.log = function(message)\n" +
                    "{\n" +
                    "    java.log(message);\n" +
                    "};");
            JSObject postJQuery = (JSObject) webEngine.executeScript(PageTemplate.scriptJQueryPost);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int tryGetPageCount() {
        try {
            String countOfFiles = (String) webEngine.executeScript(PageTemplate.scriptGetCountOfFiles);
            if (Integer.valueOf(countOfFiles) % PageTemplate.pageItems == 0) {
                return Integer.valueOf(countOfFiles) / PageTemplate.pageItems;
            } else {
                return (Integer.valueOf(countOfFiles) / PageTemplate.pageItems) + 1;
            }
        } catch (Exception e) {
            return -1;
        }
    }

    public void startWork() {
        webEngine.load(PageTemplate.startUrl);
    }
}

// $url = "/libs/otf/mirrors.otf.php"
// $fid = "962021903579496369"
// $.post($url, { "submit" : "submit" , "action" : "getdownloadmirrors" , "fid" : $fid },
// ---
// $.post("/libs/otf/mirrors.otf.php", { "submit" : "submit" , "action" : "getdownloadmirrors" , "fid" : 962021903579496369 },
// function($response) { console.log($response) });

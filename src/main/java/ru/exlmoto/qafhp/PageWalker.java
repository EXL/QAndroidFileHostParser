package ru.exlmoto.qafhp;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.concurrent.Worker.State;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;

public class PageWalker {
    private WebView webView = null;
    private WebEngine webEngine = null;
    private GuiController guiController = null;

    public class JavaBridge {
        private PageWalker pageWalker = null;

        public void log(String text) {
            pageWalker.guiController.toReport(text);
        }

        public JavaBridge(PageWalker pageWalker) {
            this.pageWalker = pageWalker;
        }
    }

    enum WorkerState { PAGE_C, PAGE_S,  ITEM }

    public WorkerState workerState = WorkerState.PAGE_C;

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
                                PageTemplate.pageCount = tryGetPageCount();
                                if (PageTemplate.pageCount != -1) {
                                    guiController.toLog("Page #" + PageTemplate.pageStart + " fully loaded.");
                                    PageTemplate.pageStart += 1;
                                    tryGetFileLinks();
                                    guiController.goToUrl(PageTemplate.startUrl + PageTemplate.pageStart);
                                    if (PageTemplate.pageStart > PageTemplate.pageStop) {
                                        workerState = WorkerState.ITEM;
                                    }
                                } else {
                                    guiController.toLog("Page Count... fail!");
                                }
                                break;
                            }
                            case ITEM: {
                                guiController.toLog("Move to Items!");
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

    private void tryGetFileLinks() {
        try {
            JSObject linksArrayDirty = (JSObject) webEngine.executeScript(PageTemplate.scriptGetLinks);
            guiController.toReport(linksArrayDirty.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void tryGetItemMirrors() {
        try {
            JSObject window = (JSObject) webEngine.executeScript("window");
            JavaBridge bridge = new JavaBridge(this);
            window.setMember("java", bridge);
            webEngine.executeScript("console.log = function(message)\n" +
                    "{\n" +
                    "    java.log(message);\n" +
                    "};");
            webEngine.executeScript(PageTemplate.scriptJQueryPost);
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
        guiController.goToUrl(PageTemplate.startUrl + "1");
    }
}

// $url = "/libs/otf/mirrors.otf.php"
// $fid = "962021903579496369"
// $.post($url, { "submit" : "submit" , "action" : "getdownloadmirrors" , "fid" : $fid },
// ---
// $.post("/libs/otf/mirrors.otf.php", { "submit" : "submit" , "action" : "getdownloadmirrors" , "fid" : 962021903579496369 },
// function($response) { console.log($response) });

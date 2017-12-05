package ru.exlmoto.qafhp;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker.State;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

public class PageWalker {
    private WebView webView = null;
    private WebEngine webEngine = null;
    private GuiController guiController = null;
    private PostGetter postGetter = null;

    private ChangeListener<State> initialListener = null;
    private ChangeListener<State> pageLinksListener = null;

    private List<Flashes> flashesArray = null;

    private void getDirectLinksWithPost(List<String> fids) {
        postGetter = new PostGetter(guiController, fids);
        postGetter.startWork();
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
                        for (Flashes aFlashesArray : flashesArray) {
                            fids.add(aFlashesArray.getFid());
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

        flashesArray = new ArrayList<>();

        initialListener = new ChangeListener<State>() {
            @Override
            public void changed(ObservableValue<? extends State> observable, State oldValue, State newValue) {
                if (newValue == State.SUCCEEDED) {
                    PageTemplate.pageCount = tryGetPageCount();
                    if (tryGetPageCount() != -1) {
                        guiController.toLog("Get num of pages... " + PageTemplate.pageCount + ".");
                        List<String> links = new ArrayList<>();
                        for (int i = PageTemplate.pageStart; i <= PageTemplate.pageStop; i++, PageTemplate.pageCountAux++) {
                            String link = PageTemplate.startUrl + "&page=" + i;
                            links.add(link);
                        }
                        guiController.toLog("Generating links... " + PageTemplate.pageCountAux);
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
            guiController.toLog("Page #" + PageTemplate.pageCountAux + " crawled!");
            PageTemplate.pageCountAux += 1;
            String linksArrayDirty = (String) webEngine.executeScript(PageTemplate.scriptGetLinks);
            String[] links = linksArrayDirty.split("\\|");
            for (int i = 0; i < links.length; ++i) {
                String[] sw = links[i].split(";");
                flashesArray.add(new Flashes(i+1, sw[1], sw[0]));
            }
            for (Flashes flash : flashesArray) {
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
        guiController.goToUrl(PageTemplate.startUrl);
        webEngine.getLoadWorker().stateProperty().addListener(initialListener);
//        postGetter = new PostGetter(guiController, null);
//        postGetter.startWork();
    }
}

// $url = "/libs/otf/mirrors.otf.php"
// $fid = "962021903579496369"
// $.post($url, { "submit" : "submit" , "action" : "getdownloadmirrors" , "fid" : $fid },
// ---
// $.post("/libs/otf/mirrors.otf.php", { "submit" : "submit" , "action" : "getdownloadmirrors" , "fid" : 962021903579496369 },
// function($response) { console.log($response) });

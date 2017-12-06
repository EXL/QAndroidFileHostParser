package ru.exlmoto.qafhp;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.concurrent.Worker.State;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

public class PageWalker {
    private WebView webView = null;
    private WebEngine webEngine = null;
    private GuiController guiController = null;
    private PostGetter postGetter = null;

    private ChangeListener<State> initialListener = null;

    private List<Flashes> flashesArray = null;

    enum PartStates { PartLink, PartMd5 }

    private void getDirectLinksWithPost(List<String> fids) {
        postGetter = new PostGetter(guiController, fids, this);
        postGetter.startWork();
    }

    private List<String> getFids() {
        List<String> fids = new ArrayList<>();
        for (Flashes aFlashesArray : flashesArray) {
            fids.add(aFlashesArray.getFid());
        }
        return fids;
    }

    // https://stackoverflow.com/a/27496335
    private void loadPagesConsecutively(List<String> pages, PartStates state) {
        LinkedList<String> pageStack = new LinkedList<>(pages);
        ChangeListener<State> pageLinksListener = new ChangeListener<State>() {
            @Override
            public void changed(ObservableValue<? extends State> obs, State oldState, State newState) {
                if (newState == State.SUCCEEDED) {
                    switch (state) {
                        case PartLink: {
                            tryGetFileLinks();
                            if (pageStack.isEmpty()) {
                                webEngine.getLoadWorker().stateProperty().removeListener(this);
                                guiController.toLog("=== End LINKS");
                                guiController.toLog("Getting SW Links done!");
                                List<String> fids = getFids();
                                if (PageTemplate.settingMd5) {
                                    guiController.toLog("Get additional... " + fids.size() + ".");
                                    guiController.toLog("=== Start MD5");
                                    List<String> links = new ArrayList<>();
                                    PageTemplate.fidsAux = fids.size();
                                    for (Flashes aFlashesArray : flashesArray) {
                                        links.add(aFlashesArray.getUrl());
                                    }
                                    loadPagesConsecutively(links, PartStates.PartMd5);
                                } else {
                                    guiController.toLog("Get Post Direct links... " + fids.size() + ".");
                                    getDirectLinksWithPost(fids);
                                }
                            } else {
                                guiController.goToUrl(pageStack.pop());
                            }
                            break;
                        }
                        case PartMd5: {
                            if (tryGetAdditionalInfo(pageStack.size())) {
                                if (pageStack.isEmpty()) {
                                    webEngine.getLoadWorker().stateProperty().removeListener(this);
                                    guiController.toLog("=== End MD5");
                                    List<String> fids = getFids();
                                    guiController.toLog("Get Post Direct links... " + fids.size() + ".");
                                    getDirectLinksWithPost(fids);
                                } else {
                                    guiController.goToUrl(pageStack.pop());
                                }
                            } else {
                                guiController.toLog("Get additional... fail!");
                            }
                            break;
                        }
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
                        guiController.toLog("Generating links... " + PageTemplate.pageCountAux + ".");
                        guiController.toLog("=== Start LINKS");
                        PageTemplate.pageCountAux = PageTemplate.pageStart;
                        webEngine.getLoadWorker().stateProperty().removeListener(this);
                        loadPagesConsecutively(links, PartStates.PartLink);
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

    private boolean tryGetAdditionalInfo(int size) {
        int i = PageTemplate.fidsAux - size - 1;
        try {
            String additionalArray = (String) webEngine.executeScript(PageTemplate.scriptGetAdditional);
            String[] additional = additionalArray.split(";");
            flashesArray.get(i).setSize(additional[0]);
            flashesArray.get(i).setMd5(additional[1]);
            flashesArray.get(i).setDate(additional[2]);
            guiController.toLog("Addit. " + (i+1) + " " + additionalArray);
            return true;
        } catch (Exception e) {
            guiController.toLog(e.toString());
            return false;
        }
    }

    public List<Flashes> getFlashesArray() {
        return flashesArray;
    }

    public WebEngine getWebEngine() {
        return webEngine;
    }

    public void startWork() {
        webEngine.getLoadWorker().stateProperty().addListener(initialListener);
    }
}

// $url = "/libs/otf/mirrors.otf.php"
// $fid = "962021903579496369"
// $.post($url, { "submit" : "submit" , "action" : "getdownloadmirrors" , "fid" : $fid },
// ---
// $.post("/libs/otf/mirrors.otf.php", { "submit" : "submit" , "action" : "getdownloadmirrors" , "fid" : 962021903579496369 },
// function($response) { console.log($response) });

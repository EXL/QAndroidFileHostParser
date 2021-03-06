package ru.exlmoto.qafhp;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker.State;
import javafx.scene.web.WebEngine;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class PageWalker {
    private WebEngine webEngine;
    private GuiController guiController;

    private ChangeListener<State> initialListener;

    private List<Flashes> flashesArray;
    private List<String> cookiesArray;

    PageWalker(WebEngine webEngine, GuiController guiController) {
        this.webEngine = webEngine;
        this.guiController = guiController;

        flashesArray = new ArrayList<>();
        cookiesArray = new ArrayList<>();

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

    private void getDirectLinksWithPost(List<String> fids) {
        PostGetter postGetter = new PostGetter(guiController, fids, cookiesArray, this);
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

    private void tryGetFileLinks() {
        try {
            guiController.toLog("Page #" + PageTemplate.pageCountAux + " crawled!");
            PageTemplate.pageCountAux += 1;
            String linksArrayDirty = (String) webEngine.executeScript(PageTemplate.scriptGetLinks);
            String[] links = linksArrayDirty.split("\\|");
            for (String link : links) {
                String[] sw = link.split(";");
                flashesArray.add(new Flashes(sw[1], sw[0]));
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
            String cookie = (String) webEngine.executeScript(PageTemplate.scriptGetCookie);
            String[] additional = additionalArray.split(";");
            flashesArray.get(i).setSize(additional[0]);
            flashesArray.get(i).setMd5(additional[1]);
            flashesArray.get(i).setDate(additional[2]);
            cookiesArray.add(cookie);
            guiController.toLog("Addit. " + (i + 1) + " " + additionalArray +
                    "\nCookie: " + cookie);
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

    enum PartStates {PartLink, PartMd5}
}

// $url = "/libs/otf/mirrors.otf.php"
// $fid = "962021903579496369"
// $.post($url, { "submit" : "submit" , "action" : "getdownloadmirrors" , "fid" : $fid },
// ---
// $.post("/libs/otf/mirrors.otf.php", { "submit" : "submit" , "action" : "getdownloadmirrors" , "fid" : 962021903579496369 },
// function($response) { console.log($response) });

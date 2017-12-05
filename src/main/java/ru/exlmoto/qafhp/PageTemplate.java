package ru.exlmoto.qafhp;

public class PageTemplate {
    public static String testUrl = "https://androidfilehost.com/?fid=889964283620770242";
    public static String startUrl = "https://www.androidfilehost.com/?w=search&s=.xml.zip&type=files";

    public static int pageItems = 15;
    public static int pageCount = 0;
    public static int pageStop = 10;

    public static boolean settingMd5 = true;
    public static boolean settingMirrors = true;

    public static String scriptGetCountOfFiles = "document.getElementsByClassName(\"search-result-stats\")[0].innerHTML.replace(/<[^>]*>/g, \"\").replace(\"&nbsp;\", \"\").replace(\"Files :\", \"\").trim();";
    public static String scriptJQueryPost = "$.post(\"/libs/otf/mirrors.otf.php\", { \"submit\" : \"submit\" , \"action\" : \"getdownloadmirrors\" , \"fid\" : \"962021903579496369\" }, function(response) { console.log(response) });";
}

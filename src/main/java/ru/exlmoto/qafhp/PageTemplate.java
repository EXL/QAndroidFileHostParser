package ru.exlmoto.qafhp;

public class PageTemplate {
    public static String testUrl = "https://androidfilehost.com/?fid=889964283620770242";
    public static String startUrl = "https://www.androidfilehost.com/?w=search&s=.xml.zip&type=files&page=";

    public static int pageItems = 15;
    public static int pageCount = 0;
    public static int pageStart = 1;
    public static int pageStop = 10;

    public static boolean settingMd5 = true;
    public static boolean settingMirrors = true;

    public static String scriptGetCountOfFiles = "document.getElementsByClassName(\"search-result-stats\")[0].innerHTML.replace(/<[^>]*>/g, \"\").replace(\"&nbsp;\", \"\").replace(\"Files :\", \"\").trim();";
    public static String scriptJQueryPost = "$.post(\"/libs/otf/mirrors.otf.php\", { \"submit\" : \"submit\" , \"action\" : \"getdownloadmirrors\" , \"fid\" : \"962021903579496369\" }, function(response) { console.log(response) });";
    public static String scriptGetLinks = "(function() { var a = []; var b = document.getElementsByClassName(\"file-name\"); for (var i = 0; i < b.length; ++i) { var c = b[i].innerHTML.trim(); var d = c.length; var e = \"https://androidfilehost.com\" + c.slice(13, d.length).replace('\">', \":\").slice(0, -9) + \"\\n\"; a.push(e); } return a; })();";
}

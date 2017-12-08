package ru.exlmoto.qafhp;

public class PageTemplate {
    public static String justUrl = "https://androidfilehost.com/";
    public static String startUrl = "https://www.androidfilehost.com/?w=search&s=.xml.zip&type=files";
    public static String startUrlAux = "https://www.androidfilehost.com/?w=search&s=.xml.zip&type=files";

    public static int pageItems = 15;
    public static int pageCount = 0;
    public static int pageStart = 1;
    public static int pageStop = 2;
    public static int postDelay = 25;
    public static int conTimeout = 70;
    public static int pageCountAux = 0;
    public static int fidsAux = 0;

    public static boolean settingMd5 = true;

    public static String scriptGetCountOfFiles = "document.getElementsByClassName(\"search-result-stats\")[0].innerHTML.replace(/<[^>]*>/g, \"\").replace(\"&nbsp;\", \"\").replace(\"Files :\", \"\").trim();";
    public static String scriptGetLinks = "(function() { var a = \"\"; var b = document.getElementsByClassName(\"file-name\"); for (var i = 0; i < b.length; ++i) { var c = b[i].innerHTML.trim(); var d = c.length; var e = \"https://androidfilehost.com\" + c.slice(13, d.length).replace('\">', \";\").slice(0, -9) + \"\\n\"; a += e + \"|\"; } return a; })();";
    public static String scriptGetAdditional = "document.getElementsByClassName(\"file-attr-value\")[1].innerHTML.replace(/<[^>]*>/g, \"\").slice(0, -4) + \";\" + document.getElementsByClassName(\"file-attr-value\")[2].innerHTML.replace(/<[^>]*>/g, \"\").slice(0, -3) + \";\" + document.getElementsByClassName(\"file-attr-value\")[3].innerHTML.replace(/<[^>]*>/g, \"\").slice(0, -11);";
    public static String scriptGetCookie = "document.cookie";

    public static String curlUrl = "https://androidfilehost.com/libs/otf/mirrors.otf.php";
    public static String curlCookie = "afh=f11126e55637d5284173083cacaf34c7; i_72623ev9=75; SPSI=fa8d9c12161eca8d89238ffc2f253f7f; sbtsck=jav; _gat=1; spcsrf=16066cfd6bca89bcfbfc5b1d45922b45; PRLST=os; UTGv2=h4d9b133bb87689ecb9bcbe2fc8da3689475; _ga=GA1.2.1352175642.1512412104; _gid=GA1.2.1885976942.1512412104; adOtr=9dacf2I11; _awl=2.54.3-8c0464aa55acf2fd333f2a3c26d23032-412d3539393742453442363342353145353139423131383231382d31-6763652d6575726f70652d7765737431";
    public static String curlAe = "gzip, deflate, br";
    public static String curlAl = "en-US,en;q=0.9,ru;q=0.8";
    public static String curlContentType = "application/x-www-form-urlencoded; charset=UTF-8";
    public static String curlXmod = "xhr";
    public static String curlA = "*/*";
    public static String curlAuthority = "androidfilehost.com";
    public static String curlXreq = "XMLHttpRequest";
    public static String curlUa = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/62.0.3202.97 Safari/537.36 Vivaldi/1.94.1008.34";
    public static String curlData = "submit=submit&action=getdownloadmirrors&fid=";
    public static String curlReq = "curl 'https://androidfilehost.com/libs/otf/mirrors.otf.php' -H 'cookie: afh=bc06f77f53f9864b545bb42210a6a487; SPSI=7c2790ffcc4cfe8ad48d3f8abd1be634; sbtsck=jav; _gat=1; i_72623ev9=75; spcsrf=359fff32ae1a6f8e092f94ad27b2ed1c; PRLST=ha; UTGv2=h4400b6819a2ea4e4d511b62dd91403bdb96; _ga=GA1.2.946306473.1512399207; _gid=GA1.2.186282965.1512635741; adOtr=97c07fScf; _awl=2.52.3-44c2f8c04d96a54a70e33e790a314321-412d3539393742453442363342353145353139423131383231382d31-6763652d6575726f70652d7765737431' -H 'origin: https://androidfilehost.com' -H 'accept-encoding: gzip, deflate, br' -H 'accept-language: en-US,en;q=0.9,ru;q=0.8' -H 'user-agent: Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/62.0.3202.97 Safari/537.36 Vivaldi/1.94.1008.34' -H 'content-type: application/x-www-form-urlencoded; charset=UTF-8' -H 'x-mod-sbb-ctype: xhr' -H 'accept: */*' -H 'referer: https://androidfilehost.com/?fid=673791459329059783' -H 'authority: androidfilehost.com' -H 'x-requested-with: XMLHttpRequest' --data 'submit=submit&action=getdownloadmirrors&fid=673791459329059783' --compressed";
}

// curl 'https://androidfilehost.com/libs/otf/mirrors.otf.php'
// -H 'cookie: afh=f11126e55637d5284173083cacaf34c7; i_726237dc4cf26d0531d5; sbtsck=jav; _gat=1; PRLST=RH; UTGv2=h4d9b133bb87689ecb9bcbe2fc8da3689475; _ga=GA1.2.1352175642.1512412104; _gid=GA1.2.1885976942.1512412104; spcsrf=0f23506163eed9ed16c05598c155cf1c; adOtr=1K0U701dd; _awl=2.54.3-8c0464aa55acf2fd333f2a3c26d23032-412d3539393742453442363342353145353139423131383231382d31-6763652d6575726f70652d7765737431'
// -H 'origin: https://androidfilehost.com'
// -H 'accept-encoding: gzip, deflate, br'
// -H 'accept-language: en-US,en;q=0.9,ru;q=0.8'
// -H 'user-agent: Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/62.0.3202.97 Safari/537.36 Vivaldi/1.94.1008.34'
// -H 'content-type: application/x-www-form-urlencoded; charset=UTF-8'
// -H 'x-mod-sbb-ctype: xhr'
// -H 'accept: */*'
// -H 'referer: https://androidfilehost.com/?fid=889964283620770242'
// -H 'authority: androidfilehost.com'
// -H 'x-requested-with: XMLHttpRequest'
// --data 'submit=submit&action=getdownloadmirrors&fid=889964283620770242'
// --compressed
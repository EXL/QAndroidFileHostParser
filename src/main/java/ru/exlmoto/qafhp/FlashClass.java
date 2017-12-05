package ru.exlmoto.qafhp;

import java.math.BigDecimal;
import java.math.BigInteger;

public class FlashClass {
    private int number = 0;
    private String name = "undefined";
    private String url = "undefined";
    private String md5 = "undefined";
    private String directLinks = "undefined";

    public FlashClass(int number, String name, String url, String md5, String directLinks) {
        this.number = number;
        this.name = name;
        this.url = url;
        this.md5 = md5;
        this.directLinks = directLinks;
    }

    public FlashClass(int number, String name, String url) {
        this.number = number;
        this.name = name;
        this.url = url;
    }

    public String getFid() {
        try {
            String fid = url.replace(PageTemplate.justUrl + "?fid=", "");
            BigInteger bigInteger = new BigInteger(fid);
            return fid;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public String getDirectLinks() {
        return directLinks;
    }

    public void setDirectLinks(String directLinks) {
        this.directLinks = directLinks;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "Name: " + name +
                "Link: " + url + "\n" +
                "MD5: " + md5 + "\n" +
                "Direct Links:\n" + directLinks + "\n\n";
    }
}

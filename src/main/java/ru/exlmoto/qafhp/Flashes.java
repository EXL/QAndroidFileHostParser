package ru.exlmoto.qafhp;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class Flashes {
    private String name;
    private String url;
    private String md5 = "undefined";
    private String date = "undefined";
    private String size = "undefined";
    private List<String> directLinks;

    Flashes(String name, String url) {
        this.name = name;
        this.url = url;
        directLinks = new ArrayList<>();
        directLinks.add("undefined");
    }

    public String getFid() {
        try {
            String fid = url.replace(PageTemplate.justUrl + "?fid=", "");
            new BigInteger(fid);
            return fid;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public void setDirectLinks(List<String> directLinks) {
        this.directLinks.clear();
        this.directLinks = directLinks;
    }

    public String getUrl() {
        return url;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setSize(String size) {
        this.size = size;
    }

    @Override
    public String toString() {
        String reportString = "Name: " + name +
                "Link: " + url + "\n" +
                "MD5: " + md5 + "\n" +
                "Date: " + date + "\n" +
                "Size: " + size + "\n" +
                "Direct Links:\n";
        StringBuilder sb = new StringBuilder();
        for (String directLink : directLinks) {
            sb.append(directLink).append("\n");
        }
        reportString += sb.toString() + "\n\n";
        return reportString;
    }
}

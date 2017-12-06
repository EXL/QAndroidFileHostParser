package ru.exlmoto.qafhp;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class Flashes {
    private int number = 0;
    private String name = "undefined";
    private String url = "undefined";
    private String md5 = "undefined";
    private String date = "undefined";
    private String size = "undefined";
    private List<String> directLinks = null;

    public Flashes(int number, String name, String url) {
        this.number = number;
        this.name = name;
        this.url = url;
        directLinks = new ArrayList<>();
        directLinks.add("undefined");
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

    public List<String> getDirectLinks() {
        return directLinks;
    }

    public void setDirectLinks(List<String> directLinks) {
        this.directLinks.clear();
        this.directLinks = directLinks;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getSize() {
        return size;
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
        reportString += sb.toString() + "\n";
        return reportString;
    }
}

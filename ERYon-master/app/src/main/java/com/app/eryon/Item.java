package com.app.eryon;

public class Item {
    private String postid;
    private String name;
    private String img_url;
    private String detail_link;
    private String day;
    private String text;
    private String good;
    private String jot;
    private String dat;
    private String part;


    public Item(String postid,String name, String url, String link, String day, String text,String good, String jot, String dat,String part){
        this.postid = postid;
        this.name = name;
        this.img_url = url;
        this.detail_link = link;
        this.day = day;
        this.text = text;
        this.good = good;
        this.jot = jot;
        this.dat = dat;
        this.part=part;
    }

    public String getPostid() {
        return postid;
    }

    public String getName() {
        return name;
    }

    public String getImg_url() {
        return img_url;
    }

    public String getDetail_link() {
        return detail_link;
    }

    public String getDay() {
        return day;
    }

    public String getText() {
        return text;
    }

    public String getGood() {
        return good;
    }

    public String getJot() {
        return jot;
    }
    public String getDat() {
        return dat;
    }
    public String getPart() {
        return part;
    }

}

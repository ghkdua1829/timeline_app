package com.app.eryon;

public class dat {
    private String datimg;
    private String datid;
    private String dattext;
    private String datcreated;


    public dat(String datimg, String datid, String dattext, String datcreated){
        this.datimg = datimg;
        this.datid = datid;
        this.dattext = dattext;
        this.datcreated = datcreated;
    }


    public String getDatimg() {
        return datimg;
    }

    public String getDatid() {
        return datid;
    }

    public String getDattext() {
        return dattext;
    }

    public String getDatcreated() {
        return datcreated;
    }

}

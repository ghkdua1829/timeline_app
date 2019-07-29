package com.app.eryon;

public class alarm {
    private String alarmcontent;
    private String sendname;
    private String username;

    public alarm(String alarmcontent,String sendname,String username){
        this.alarmcontent=alarmcontent;
        this.sendname=sendname;
        this.username=username;
    }
    public String getAlarmcontent() {
        return alarmcontent;
    }
    public String getSendname() {
        return sendname;
    }
    public String getUsername() {
        return username;
    }

}

package com.app.eryon;

public class Friend {
    private String friendname;
    private String friendprofile;

    public Friend(String friendname,String friendprofile){
        this.friendname=friendname;
        this.friendprofile=friendprofile;
    }
    public String getFriendname() {
        return friendname;
    }
    public String getFriendprofile() {
        return friendprofile;
    }

}

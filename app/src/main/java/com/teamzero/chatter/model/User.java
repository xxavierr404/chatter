package com.teamzero.chatter.model;

import android.media.Image;

public class User {
    private String nickname;
    private String bio;

    public User(String nickname, String bio) {
        this.nickname = nickname;
        this.bio = bio;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

}

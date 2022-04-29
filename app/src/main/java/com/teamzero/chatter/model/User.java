package com.teamzero.chatter.model;

import android.media.Image;

import java.util.Set;

public class User {
    private String nickname;
    private String bio;
    private Set<Integer> chatIDs;

    public User(String nickname, String bio) {
        this.nickname = nickname;
        this.bio = bio;
    }

    public User(String nickname, String bio, Set<Integer> chatIDs) {
        this.nickname = nickname;
        this.bio = bio;
        this.chatIDs = chatIDs;
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

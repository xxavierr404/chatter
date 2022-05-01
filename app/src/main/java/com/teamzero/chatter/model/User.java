package com.teamzero.chatter.model;

import android.media.Image;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class User {
    private String nickname;
    private String bio;
    private List<String> chatIDs;

    public User(String nickname, String bio) {
        this.nickname = nickname;
        this.bio = bio;
        chatIDs = new ArrayList<String>();
    }

    public User(String nickname, String bio, ArrayList<String> chatIDs) {
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

    public List<String> getChatIDs() {
        return chatIDs;
    }

    public void addChat(String chatID){
        chatIDs.add(chatID);
    }

    public void removeChat(String chatID){
        chatIDs.remove(chatID);
    }
}

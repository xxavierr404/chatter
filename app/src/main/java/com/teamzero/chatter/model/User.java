package com.teamzero.chatter.model;

import com.google.firebase.database.Exclude;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class User {
    private String id;
    private String nickname;
    private String bio;
    private Map<String, Boolean> chatIDs;

    public User(){
        chatIDs = new HashMap<>();
    }

    public User(String id, String nickname, String bio) {
        this.id = id;
        this.nickname = nickname;
        this.bio = bio;
        chatIDs = new HashMap<>();
    }

    public User(String id, String nickname, String bio, Map<String, Boolean> chatIDs) {
        this.id = id;
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

    public Map<String, Boolean> getChatIDs() {
        return chatIDs;
    }

    public void setChatIDs(Map<String, Boolean> chatIDs){
        this.chatIDs = chatIDs;
    }

    public void addChat(String chatID){
        chatIDs.put(chatID, true);
    }

    public void removeChat(String chatID){
        chatIDs.remove(chatID);
    }
    
    public String getId(){
        return this.id;
    }

    public void setId(String id){
        this.id = id;
    }
}

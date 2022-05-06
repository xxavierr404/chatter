package com.teamzero.chatter.model;

import android.media.Image;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class User {
    private String nickname;
    private String bio;
    private List<String> chatIDs;

    public User(){
        chatIDs = new ArrayList<>();
    }

    public User(String nickname, String bio) {
        this.nickname = nickname;
        this.bio = bio;
        chatIDs = new ArrayList<>();
    }

    public User(String nickname, String bio, List<String> chatIDs) {
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

    public void setChatIDs(List<String> chatIDs){
        this.chatIDs = chatIDs;
    }

    public void addChat(String chatID){
        chatIDs.add(chatID);
    }

    public void removeChat(String chatID){
        chatIDs.remove(chatID);
    }
}

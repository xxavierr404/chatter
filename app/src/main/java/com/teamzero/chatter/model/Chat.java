package com.teamzero.chatter.model;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Chat {
    private String id;
    private String name;
    private String adminUID;
    private List<String> members = new ArrayList<>();
    private List<String> authorized = new ArrayList<>();
    private List<String> tags = new ArrayList<>();
    private List<String> messageIDs = new ArrayList<>();

    public Chat(){}

    public Chat(String id, String creatorID){
        this.id = id;
        this.adminUID = creatorID;
        this.name = "Unknown space";
    }

    public Chat(String adminUID, List<String> members, String name,
                List<String> authorized, List<String> tags, List<String> messageIDs){
        this.adminUID = adminUID;
        this.members = members;
        this.name = name;
        this.authorized = authorized;
        this.tags = tags;
        this.messageIDs = messageIDs;
    }

    public String getAdminUID() {
        return adminUID;
    }

    public List<String> getMembers() {
        return members;
    }

    public List<String> getAuthorized() {
        return authorized;
    }

    public List<String> getMessageIDs() {
        return messageIDs;
    }

    public List<String> getTags() {
        return tags;
    }

    public void addAuthorized(String newModerator){
        authorized.add(newModerator);
    }

    public void removeAuthorized(String notModeratorAnymore){
        authorized.remove(notModeratorAnymore);
    }

    public void addMember(String newMember){
        members.add(newMember);
    }

    public void kickMember(String wasMember){
        members.remove(wasMember);
    }

    public void addMessage(String newMessageID){
        messageIDs.add(newMessageID);
    }

    public void removeMessage(String removedMessage){
        messageIDs.remove(removedMessage);
    }

    public void addTag(String newTag){
        tags.add(newTag);
    }

    public void setTags(List<String> tags){
        this.tags = tags;
    }

    public void removeTag(String removedTag){
        tags.remove(removedTag);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }
}

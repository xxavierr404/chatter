package com.teamzero.chatter.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Chat {
    private String id;
    private String name;
    private String adminUID;
    private Map<String, Boolean> members = new HashMap<>();
    private Map<String, Boolean> authorized = new HashMap<>();
    private Map<String, Boolean> tags = new HashMap<>();
    private Map<String, Boolean> messageIDs = new HashMap<>();

    public Chat(){}

    public Chat(String id, String creatorID){
        this.id = id;
        this.adminUID = creatorID;
        this.name = "Unknown space";
    }

    public Chat(String adminUID, Map<String, Boolean> members, String name,
                Map<String, Boolean> authorized, Map<String, Boolean> tags, Map<String, Boolean> messageIDs){
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

    public Map<String, Boolean> getMembers() {
        return members;
    }

    public Map<String, Boolean> getAuthorized() {
        return authorized;
    }

    public Map<String, Boolean> getMessageIDs() {
        return messageIDs;
    }

    public Map<String, Boolean> getTags() {
        return tags;
    }

    public void addAuthorized(String newModerator){
        authorized.put(newModerator, true);
    }

    public void removeAuthorized(String notModeratorAnymore){
        authorized.remove(notModeratorAnymore);
    }

    public void addMember(String newMember){
        members.put(newMember, true);
    }

    public void kickMember(String wasMember){
        members.remove(wasMember);
    }

    public void addMessage(String newMessageID){
        messageIDs.put(newMessageID, true);
    }

    public void removeMessage(String removedMessage){
        messageIDs.remove(removedMessage);
    }

    public void addTag(String newTag){
        tags.put(newTag, true);
    }

    public void setTags(Map<String, Boolean> tags){
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

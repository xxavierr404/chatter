package com.teamzero.chatter.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Chat {
    private String id;
    private String name;
    private String adminUID;
    private List<String> members = new ArrayList<>();
    private List<String> authorized = new ArrayList<>();
    private List<String> tags = new ArrayList<>();
    private Map<String, Boolean> messageIDs = new HashMap<>();

    public Chat(){}

    public Chat(String id, String creatorID){
        this.id = id;
        this.adminUID = creatorID;
        this.name = "Unknown space";
    }

    public Chat(String adminUID, List<String> members, String name,
                List<String> authorized, List<String> tags, Map<String, Boolean> messageIDs){
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

    public Map<String, Boolean> getMessageIDs() {
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
        messageIDs.put(newMessageID, true);
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

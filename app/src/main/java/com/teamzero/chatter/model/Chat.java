package com.teamzero.chatter.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Chat {
    private String id;
    private String name;
    private String adminUID;
    private String desc;
    private Map<String, Object> members = new HashMap<>();
    private Map<String, Object> authorized = new HashMap<>();
    private Map<String, Object> tags = new HashMap<>();
    private Map<String, Object> messageIDs = new HashMap<>();

    public Chat(){}

    public Chat(String id, String creatorID){
        this.id = id;
        this.adminUID = creatorID;
        this.name = "Voidspace";
        this.desc = "This place was just discovered.";
    }

    public Chat(String adminUID, Map<String, Object> members, String name,
                Map<String, Object> authorized, Map<String, Object> tags, Map<String, Object> messageIDs){
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

    public Map<String, Object> getMembers() {
        return members;
    }

    public Map<String, Object> getAuthorized() {
        return authorized;
    }

    public Map<String, Object> getMessageIDs() {
        return messageIDs;
    }

    public Map<String, Object> getTags() {
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

    public void setTags(Map<String, Object> tags){
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

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}

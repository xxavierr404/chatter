package com.teamzero.chatter.model;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Chat {
    private String name;
    private final String adminUID;
    private List<String> members;
    private List<String> authorized;
    private List<String> tags;
    private List<String> messageIDs;

    public Chat(String creator){
        adminUID = creator;
        name = "Middlespace";
        members = new ArrayList<String>();
        authorized = new ArrayList<String>();
        tags = new ArrayList<String>();
        messageIDs = new ArrayList<String>();
    }

    public Chat(String adminUID, ArrayList<String> members, String name,
                ArrayList<String> authorized, ArrayList<String> tags, ArrayList<String> messageIDs){
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
}

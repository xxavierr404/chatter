package com.teamzero.chatter.model;

import java.util.ArrayList;
import java.util.Set;

public class Chat {
    private int id;
    private String adminUID;
    private Set<String> members;
    private Set<String> authorized;
    private Set<String> tags;
    private ArrayList<Integer> messageIDs;

    public Chat(int id, String creator){
        this.id = id;
        adminUID = creator;
    }

    public Chat(int id, String adminUID, Set<String> members,
                Set<String> authorized, Set<String> tags, ArrayList<Integer> messageIDs){
        this.id = id;
        this.adminUID = adminUID;
        this.members = members;
        this.authorized = authorized;
        this.tags = tags;
        this.messageIDs = messageIDs;
    }

    public int getId() {
        return id;
    }

    public String getAdminUID() {
        return adminUID;
    }

    public Set<String> getMembers() {
        return members;
    }

    public Set<String> getAuthorized() {
        return authorized;
    }

    public ArrayList<Integer> getMessageIDs() {
        return messageIDs;
    }

    public Set<String> getTags() {
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

    public void addMessage(int newMessageID){
        messageIDs.add(newMessageID);
    }

    public void removeMessage(int removedMessage){
        messageIDs.remove(removedMessage);
    }

    public void addTag(String newTag){
        tags.add(newTag);
    }

    public void removeTag(String removedTag){
        tags.remove(removedTag);
    }
}

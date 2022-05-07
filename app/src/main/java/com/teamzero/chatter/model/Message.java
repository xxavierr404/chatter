package com.teamzero.chatter.model;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.ServerValue;

import java.util.Map;

public class Message {
    private String id;
    private String text;
    private String senderUID;
    private Long timestamp;
    private String destinationUID;
    private boolean read;

    public Message(String id, String text, String senderUID, Map<String, String> timestamp, String dest) {
        this.id = id;
        this.text = text;
        this.senderUID = senderUID;
        this.destinationUID = dest;
        this.read = false;
    }

    public Message(){}

    public String getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getSenderUID() {
        return senderUID;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead() {
        this.read = true;
    }

    public Map<String, String> getTimestamp() {
        return ServerValue.TIMESTAMP;
    }

    @Exclude
    public Long getTimestampLong() {
        return timestamp;
    }

    public String getDestinationUID() {
        return destinationUID;
    }

    public void setDestinationUID(String destinationUID) {
        this.destinationUID = destinationUID;
    }
}

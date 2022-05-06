package com.teamzero.chatter.model;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Message {
    private String id;
    private String text;
    private String senderUID;
    private Long timestamp;
    private boolean read;

    public Message(String id, String text, String senderUID, Long timestamp) {
        this.id = id;
        this.text = text;
        this.senderUID = senderUID;
        this.timestamp = timestamp;
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

    public Long getTimestamp() {
        return timestamp;
    }

}

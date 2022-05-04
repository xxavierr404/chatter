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
    private boolean read;

    public Message(String id){
        String[] messageData = new String[3];
        FirebaseDatabase.getInstance().getReference("messages")
                .child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messageData[0] = snapshot.child("id").getValue().toString();
                messageData[1] = snapshot.child("senderUID").getValue().toString();
                messageData[2] = snapshot.child("text").getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("MessageERR", error.getDetails());
            }
        });

        id = messageData[0];
        senderUID = messageData[1];
        text = messageData[2];
        read = false;
    }

    public Message(String id, String text, String senderUID) {
        this.id = id;
        this.text = text;
        this.senderUID = senderUID;
        this.read = false;
    }

    public Message(){
        this.read = false;
    }

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
}

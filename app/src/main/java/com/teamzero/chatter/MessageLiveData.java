package com.teamzero.chatter;

import androidx.lifecycle.MutableLiveData;

import com.teamzero.chatter.model.Message;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessageLiveData extends MutableLiveData<Map<String, Message>> {

    Map<String, Message> data = new HashMap<>();

    public void add(Message msg){
        data.put(msg.getId(), msg);
        setValue(data);
    }

    public void remove(Message msg){
        data.remove(msg.getId());
        setValue(data);
    }

    public Message getMessage(String id) {
        return data.get(id);
    }
}

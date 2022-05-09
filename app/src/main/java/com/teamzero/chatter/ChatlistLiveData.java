package com.teamzero.chatter;

import androidx.lifecycle.MutableLiveData;

import com.teamzero.chatter.model.Chat;

import java.util.ArrayList;
import java.util.List;

public class ChatlistLiveData extends MutableLiveData<List<Chat>> {
    private final List<Chat> data = new ArrayList<>();

    public void add(Chat chat){
        data.add(chat);
        setValue(data);
    }

    public void remove(Chat chat){
        data.remove(chat);
        setValue(data);
    }

    public void update(Chat chat){
        for(Chat c: data){
            if(c.getId().equals(chat.getId())) {
                data.remove(c);
                data.add(chat);
                setValue(data);
                return;
            }
        }
    }

}

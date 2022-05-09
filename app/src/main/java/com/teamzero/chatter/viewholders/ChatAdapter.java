package com.teamzero.chatter.viewholders;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.teamzero.chatter.R;
import com.teamzero.chatter.Utils;
import com.teamzero.chatter.model.Chat;
import com.teamzero.chatter.model.Message;
import com.teamzero.chatter.ui.fragments.main.ChatlogFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatInfoHolder> {

    private List<Chat> chatList;
    private Context ctx;

    public ChatAdapter(Context ctx){
        this.chatList = new ArrayList<>();
        this.ctx = ctx;
    }

    public void addChat(Chat chat){
        chatList.add(chat);
    }

    public void updateChat(Chat chat){
        for(Chat c: chatList){
            if(c.getId().equals(chat.getId())) {
                c = chat;
                return;
            }
        }
    }

    public void removeChat(Chat chat){
        chatList.remove(chat);
    }

    @NonNull
    @Override
    public ChatInfoHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ChatInfoHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_chat_tab, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ChatInfoHolder holder, int position) {

        Chat chat = chatList.get(position);
        HashMap<String, ChatlogFragment> chatFragments = new HashMap<>();

        holder.lastMessage.setText(R.string.no_messages_yet);
        holder.chatName.setText(chat.getName());
        // TODO: 04.05.2022 Установка изображения
/*        if(chat.getImageURL() != null){
            holder.chatImage.setImageResource();
        }*/

        Utils.getDatabase().getReference("chats").child(chat.getId())
                .child("messageIDs").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Utils.getDatabase().getReference("messages").child(snapshot.getKey())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                String text = snapshot.getValue(Message.class).getText();
                                if(text.length() > 50){
                                    text = text.substring(0, 47) + "...";
                                }
                                holder.lastMessage.setText(text);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!chatFragments.containsKey(chat.getId())) {
                    Log.i("ChatINF", "Created new chatlog window");
                    chatFragments.put(chat.getId(), new ChatlogFragment(chat.getId()));
                }
                ((AppCompatActivity) ctx).getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frame, chatFragments.get(chat.getId()))
                        .addToBackStack("chatWindow").commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    class ChatInfoHolder extends RecyclerView.ViewHolder{

        TextView chatName;
        TextView lastMessage;
        ImageView chatImage;
        ImageView unreadIndicator;
        ConstraintLayout layout;

        public ChatInfoHolder(@NonNull View itemView) {
            super(itemView);

            chatName = itemView.findViewById(R.id.chat_name);
            lastMessage = itemView.findViewById(R.id.last_message);
            chatImage = itemView.findViewById(R.id.chat_image);
            unreadIndicator = itemView.findViewById(R.id.unread_indicator);
            layout = itemView.findViewById(R.id.chatTab);
        }
    }
}

package com.teamzero.chatter.viewholders;

import android.content.Context;
import android.net.Uri;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.teamzero.chatter.R;
import com.teamzero.chatter.Utils;
import com.teamzero.chatter.model.Chat;
import com.teamzero.chatter.model.Message;

import java.util.ArrayList;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatInfoHolder> {

    private List<Chat> chatList;
    private Context ctx;

    public ChatAdapter(Context ctx, List<Chat> chatList){
        this.ctx = ctx;
        this.chatList = chatList;
    }

    public ChatAdapter(Context ctx){
        this.ctx = ctx;
        this.chatList = new ArrayList<>();
    }

    public void addChat(Chat chat){
        chatList.add(chat);
    }

    @NonNull
    @Override
    public ChatInfoHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ChatInfoHolder(LayoutInflater.from(ctx).inflate(R.layout.layout_chat_tab, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ChatInfoHolder holder, int position) {
        Chat chat = chatList.get(position);

        holder.chatName.setText(chat.getName());
        // TODO: 04.05.2022 Установка изображения
/*        if(chat.getImageURL() != null){
            holder.chatImage.setImageResource();
        }*/
        if(!chat.getMessageIDs().isEmpty()) {
            String lastMessageID = chat.getMessageIDs().get(chat.getMessageIDs().size() - 1);
            Utils.getDatabase().getReference("messages").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    holder.lastMessage.setText(snapshot.getValue(Message.class).getText());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        } else {
            holder.lastMessage.setText(R.string.no_messages_yet);
        }
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

        public ChatInfoHolder(@NonNull View itemView) {
            super(itemView);

            chatName = itemView.findViewById(R.id.chat_name);
            lastMessage = itemView.findViewById(R.id.last_message);
            chatImage = itemView.findViewById(R.id.chat_image);
            unreadIndicator = itemView.findViewById(R.id.unread_indicator);
        }
    }

}

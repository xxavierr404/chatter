package com.teamzero.chatter.viewholders;

import android.content.Context;
import android.net.Uri;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.teamzero.chatter.MainActivity;
import com.teamzero.chatter.R;
import com.teamzero.chatter.Utils;
import com.teamzero.chatter.model.Chat;
import com.teamzero.chatter.model.Message;

import java.util.ArrayList;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatInfoHolder> {

    private List<Chat> chatList;
    private ItemClickListener listener;


    public ChatAdapter(ItemClickListener listener){
        this.chatList = new ArrayList<>();
        this.listener = listener;
    }

    public void addChat(Chat chat){
        for(Chat c: chatList){
            if(c.getId().equals(chat.getId())) return;
        }
        chatList.add(chat);
    }

    @NonNull
    @Override
    public ChatInfoHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ChatInfoHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_chat_tab, parent, false));
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
            Utils.getDatabase().getReference("messages").child(lastMessageID)
        .addValueEventListener(new ValueEventListener() {
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
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItemClick(chatList.get(position));
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

    public interface ItemClickListener{
        public void onItemClick(Chat chat);
    }
}

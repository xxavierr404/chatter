package com.teamzero.chatter.viewholders;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.teamzero.chatter.R;
import com.teamzero.chatter.Utils;
import com.teamzero.chatter.model.Message;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder>{

    private List<Message> messages;

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message message = messages.get(position);

        holder.message.setText(message.getText());
        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) holder.message.getLayoutParams();
        if(message.getSenderUID().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
            holder.author.setVisibility(View.GONE);
            params.horizontalBias = 1f;
            holder.message.setLayoutParams(params);
        } else {
            params.horizontalBias = 0f;
            holder.message.setLayoutParams(params);
            Utils.getDatabase().getReference("users").child(message.getSenderUID()).child("username")
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            holder.author.setText(snapshot.getValue().toString());
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            holder.author.setText("???");
                        }
                    });
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    class MessageViewHolder extends RecyclerView.ViewHolder{

        TextView author;
        TextView message;
        ConstraintLayout layout;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            author = itemView.findViewById(R.id.author);
            message = itemView.findViewById(R.id.message);
            layout = itemView.findViewById(R.id.messageLayout);
        }
    }
}

package com.teamzero.chatter.ui.fragments.main;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.teamzero.chatter.R;
import com.teamzero.chatter.Utils;
import com.teamzero.chatter.databinding.FragmentChatlogBinding;
import com.teamzero.chatter.model.Message;
import com.teamzero.chatter.viewholders.ChatAdapter;
import com.teamzero.chatter.viewholders.MessageAdapter;

import java.util.ArrayList;
import java.util.List;

public class ChatlogFragment extends Fragment {

    private FragmentChatlogBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private String chatID;

    public ChatlogFragment(String chatID){
        this.chatID = chatID;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        mDatabase = Utils.getDatabase();
        binding = FragmentChatlogBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImageButton backButton = binding.closeChatButton;
        ImageButton editButton = binding.editChatButton;
        ImageButton sendButton = binding.send;
        ImageButton uploadButton = binding.upload;
        TextView chatName = binding.chatTitle;
        RecyclerView mainWindow = binding.chatHistoryView;
        EditText messageField = binding.messageField;

        MessageAdapter messageAdapter = new MessageAdapter();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());

        mainWindow.setAdapter(messageAdapter);
        mainWindow.setLayoutManager(layoutManager);

        mDatabase.getReference("chats").child(chatID).child("name").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatName.setText(snapshot.getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("DatabaseERR", error.getMessage());
            }
        });

        mDatabase.getReference("chats").child(chatID).child("messageIDs").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot snap: snapshot.getChildren()){
                    String messageID = snap.getValue().toString();
                    mDatabase.getReference("messages").child(messageID).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            messageAdapter.addMessage(snapshot.getValue(Message.class));
                            mainWindow.setAdapter(messageAdapter);
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                        });
                    }
                }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        backButton.setOnClickListener((v)->{
            getActivity().getSupportFragmentManager().popBackStack();
        });

        sendButton.setOnClickListener((v) -> {
            String msg = messageField.getText().toString().trim();
            if(msg.length() == 0) return;
            DatabaseReference ref = mDatabase.getReference("messages").push();
            String key = ref.getKey();
            Message newMessage = new Message(key, msg, mAuth.getCurrentUser().getUid(), ServerValue.TIMESTAMP, chatID);
            ref.setValue(newMessage)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                mDatabase.getReference("chats").child(chatID)
                                        .child("messageIDs")
                                        .child(key).push();
                            } else {
                                Log.e("MessagesERR", task.getException().getMessage());
                                Toast.makeText(getContext(), R.string.check_connection, Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        });
    }
}
package com.teamzero.chatter.ui.fragments.chats;

import android.media.MediaPlayer;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.ObjectKey;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.teamzero.chatter.R;
import com.teamzero.chatter.utils.Utils;
import com.teamzero.chatter.databinding.FragmentChatlogBinding;
import com.teamzero.chatter.model.Chat;
import com.teamzero.chatter.model.Message;
import com.teamzero.chatter.adapters.MessageAdapter;

public class ChatlogFragment extends Fragment {

    private FragmentChatlogBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private String chatID;
    private ChatlogFragment self = this;

    public ChatlogFragment(){}

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
        // TODO: 17.05.2022 Отправка вложений
        ImageButton uploadButton = binding.upload;
        ImageView chatImage = binding.chatImage;
        TextView chatName = binding.chatTitle;
        TextView emptyChatNotice = binding.emptyChatNotice;
        TextView members = binding.membersCount;
        RecyclerView mainWindow = binding.chatHistoryView;
        EditText messageField = binding.messageField;
        final MediaPlayer mp = MediaPlayer.create(getContext(), R.raw.message);

        MessageAdapter messageAdapter = new MessageAdapter(getContext());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());

        mainWindow.setAdapter(messageAdapter);
        mainWindow.setLayoutManager(layoutManager);
        mainWindow.setItemViewCacheSize(128);

        Glide.with(this)
                .load(FirebaseStorage.getInstance().getReference("chat_pics")
                        .child(chatID).child("avatar.jpg"))
                .placeholder(R.drawable.astronaut)
                .signature(new ObjectKey(System.currentTimeMillis()))
                .into(chatImage);

        mDatabase.getReference("chats").child(chatID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatName.setText(snapshot.child("name").getValue(String.class));
                final int[] onlineCounter = {0};
                for(DataSnapshot dss: snapshot.child("members").getChildren()){
                    mDatabase.getReference("users").child(dss.getKey()).child("connections").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snap) {
                            if (snap.hasChildren()) onlineCounter[0]++;
                            members.setText(String.format(getString(R.string.online_counter), snapshot.child("members").getChildrenCount(), onlineCounter[0]));
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("DatabaseERR", error.getMessage());
            }
        });

        mDatabase.getReference("chats").child(chatID).child("messageIDs").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String messageID = snapshot.getKey();
                mDatabase.getReference("messages").child(messageID).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        emptyChatNotice.setVisibility(View.GONE);
                        messageAdapter.addMessage(snapshot.getValue(Message.class));
                        mainWindow.setAdapter(messageAdapter);
                        mainWindow.scrollToPosition(messageAdapter.getItemCount()-1);
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

        backButton.setOnClickListener((v)->{
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(R.anim.slide_in_vertical, R.anim.slide_out_vertical)
                    .hide(this).commit();
        });

        editButton.setOnClickListener((v)->{
            mDatabase.getReference("chats").child(chatID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    getActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out)
                            .add(R.id.frame, new ChatOptionsFragment(snapshot.getValue(Chat.class), self))
                            .addToBackStack("chatOptions").commit();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
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
                                        .child(key).setValue(true);
                                messageField.setText("");
                                mp.start();
                            } else {
                                Log.e("MessagesERR", task.getException().getMessage());
                                Toast.makeText(getContext(), R.string.check_connection, Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        });

        binding.toolbar.setOnClickListener((v)->{
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out)
                    .add(R.id.frame, new MemberListFragment(chatID))
                    .addToBackStack("members")
                    .commit();
        });
    }
}
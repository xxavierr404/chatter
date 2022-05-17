package com.teamzero.chatter.ui.fragments.main;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.teamzero.chatter.R;
import com.teamzero.chatter.utils.Utils;
import com.teamzero.chatter.databinding.FragmentChatsBinding;
import com.teamzero.chatter.model.Chat;
import com.teamzero.chatter.viewholders.ChatAdapter;

import java.util.ArrayList;
import java.util.List;

public class ChatsFragment extends Fragment{

    private View root;
    private FragmentChatsBinding binding;
    private FirebaseDatabase mDatabase;
    private FirebaseAuth mAuth;
    private ChatAdapter adapter;
    private List<String> chatIDs = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
            binding = FragmentChatsBinding.inflate(inflater, container, false);
            root = binding.getRoot();
            return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAuth = FirebaseAuth.getInstance();

        if(mAuth.getCurrentUser() == null) return;

        mDatabase = Utils.getDatabase();
        adapter = new ChatAdapter(getContext());

        ProgressBar loading = binding.progressBar2;
        RecyclerView chatList = binding.chatRecycler;
        TextView noChatsNotice = binding.textChats;

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, true);
        DividerItemDecoration decoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);

        chatList.addItemDecoration(decoration);
        chatList.setLayoutManager(layoutManager);
        chatList.setAdapter(adapter);
        chatList.setItemAnimator(new DefaultItemAnimator());
        chatList.setItemViewCacheSize(32);

        DatabaseReference chatRef = mDatabase.getReference("chats");

/*        mDatabase.getReference("users").child(mAuth.getCurrentUser().getUid())
                .child("chatIDs").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                chatRef.child(snapshot.getKey()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(noChats){
                            noChats = false;
                            noChatsNotice.setVisibility(View.GONE);
                        }
                        adapter.addChat(snapshot.getValue(Chat.class));
                        synchronized (adapter) {
                            adapter.notify();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                chatRef.child(snapshot.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        adapter.updateChat(snapshot.getValue(Chat.class));
                        synchronized (adapter) {
                            adapter.notify();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                chatRef.child(snapshot.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        adapter.removeChat(snapshot.getValue(Chat.class));
                        synchronized (adapter) {
                            adapter.notify();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });*/

        mDatabase.getReference("users").child(mAuth.getCurrentUser().getUid())
                .child("chatIDs").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.getChildrenCount() == 0) {
                    chatList.setVisibility(View.GONE);
                    noChatsNotice.setVisibility(View.VISIBLE);
                    loading.setVisibility(View.GONE);
                } else {
                    chatIDs.clear();
                    adapter.clear();
                    chatList.setVisibility(View.VISIBLE);
                    noChatsNotice.setVisibility(View.GONE);
                    for(DataSnapshot snap: snapshot.getChildren()){
                        chatIDs.add(snap.getKey());
                    }
                    for(String chatID: chatIDs){
                        mDatabase.getReference("chats").child(chatID).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot)
                            {
                                if(snapshot.getValue(Chat.class) == null) return;
                                adapter.addChat(snapshot.getValue(Chat.class));
                                chatList.setAdapter(adapter);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(getContext(), R.string.error, Toast.LENGTH_SHORT).show();
                                Log.e("ChatERR", error.getMessage());
                            }
                        });
                    }
                    loading.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
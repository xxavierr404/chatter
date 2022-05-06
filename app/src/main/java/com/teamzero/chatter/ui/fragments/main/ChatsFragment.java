package com.teamzero.chatter.ui.fragments.main;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.teamzero.chatter.R;
import com.teamzero.chatter.Utils;
import com.teamzero.chatter.databinding.FragmentChatsBinding;
import com.teamzero.chatter.model.Chat;
import com.teamzero.chatter.viewholders.ChatAdapter;

import java.util.ArrayList;
import java.util.List;

public class ChatsFragment extends Fragment implements ChatAdapter.ItemClickListener {

    private FragmentChatsBinding binding;
    private FirebaseDatabase mDatabase;
    private FirebaseAuth mAuth;
    private ChatAdapter adapter;
    List<String> chatIDs = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentChatsBinding.inflate(inflater, container, false);
        mDatabase = Utils.getDatabase();
        mAuth = FirebaseAuth.getInstance();
        chatIDs = new ArrayList<>();
        adapter = new ChatAdapter(this);
        View root = binding.getRoot();
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if(mAuth.getCurrentUser() == null) return;

        ProgressBar loading = binding.progressBar2;
        RecyclerView chatList = binding.chatRecycler;
        TextView noChatsNotice = binding.textChats;

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        DividerItemDecoration decoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);

        chatList.addItemDecoration(decoration);
        chatList.setLayoutManager(layoutManager);
        chatList.setAdapter(adapter);

        loading.setVisibility(View.VISIBLE);
        mDatabase.getReference("users").child(mAuth.getCurrentUser().getUid())
                .child("chatIDs").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.getChildrenCount() == 0) {
                    chatList.setVisibility(View.GONE);
                    loading.setVisibility(View.GONE);
                    noChatsNotice.setVisibility(View.VISIBLE);
                } else {
                    chatList.setVisibility(View.VISIBLE);
                    noChatsNotice.setVisibility(View.GONE);
                    for(DataSnapshot snap: snapshot.getChildren()){
                        chatIDs.add(snap.getValue().toString());
                    }
                    for(String chatID: chatIDs){
                        mDatabase.getReference("chats").child(chatID).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
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

    @Override
    public void onItemClick(Chat chat) {
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame, new ChatlogFragment(chat.getId())).addToBackStack("chatWindow").commit();
    }
}
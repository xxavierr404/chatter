package com.teamzero.chatter.ui.fragments.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.teamzero.chatter.databinding.FragmentChatsBinding;
import com.teamzero.chatter.model.Chat;
import com.teamzero.chatter.viewholders.ChatAdapter;

public class ChatsFragment extends Fragment {

    private FragmentChatsBinding binding;
    private FirebaseDatabase mDatabase;
    private FirebaseAuth mAuth;
    private ChatAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentChatsBinding.inflate(inflater, container, false);
        mDatabase = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        adapter = new ChatAdapter(getContext());
        View root = binding.getRoot();
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ProgressBar loading = binding.progressBar2;
        RecyclerView chatList = binding.chatRecycler;
        TextView noChatsNotice = binding.textChats;

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
                        adapter.addChat(snap.getValue(Chat.class));
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
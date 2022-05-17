package com.teamzero.chatter.ui.fragments.messaging;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
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
import com.teamzero.chatter.Utils;
import com.teamzero.chatter.databinding.FragmentChatsBinding;
import com.teamzero.chatter.databinding.FragmentMemberlistBinding;
import com.teamzero.chatter.model.Chat;
import com.teamzero.chatter.model.User;
import com.teamzero.chatter.viewholders.ChatAdapter;
import com.teamzero.chatter.viewholders.MembersAdapter;

import java.util.ArrayList;
import java.util.List;

public class MemberListFragment extends Fragment{

    private View root;
    private FragmentMemberlistBinding binding;
    private FirebaseDatabase mDatabase;
    private FirebaseAuth mAuth;
    private MembersAdapter adapter;
    private String chatID;

    public MemberListFragment(String chatID){
        this.chatID = chatID;
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
            binding = FragmentMemberlistBinding.inflate(inflater, container, false);
            root = binding.getRoot();
            return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAuth = FirebaseAuth.getInstance();

        if(mAuth.getCurrentUser() == null) return;

        mDatabase = Utils.getDatabase();
        adapter = new MembersAdapter(getContext());

        RecyclerView memberList = binding.memberList;
        ImageButton closeButton = binding.closeMemberListButton;

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, true);
        DividerItemDecoration decoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);

        memberList.addItemDecoration(decoration);
        memberList.setLayoutManager(layoutManager);
        memberList.setAdapter(adapter);
        memberList.setItemViewCacheSize(32);

        mDatabase.getReference("chats").child(chatID).
                child("members").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dss: snapshot.getChildren()){
                    mDatabase.getReference("users").child(dss.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            adapter.addMember(snapshot.getValue(User.class));
                            memberList.setAdapter(adapter);
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

        closeButton.setOnClickListener((v)->{
            getActivity().getSupportFragmentManager().popBackStack("members", 1);
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
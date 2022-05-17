package com.teamzero.chatter.ui.fragments.chats;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.teamzero.chatter.utils.Role;
import com.teamzero.chatter.utils.Utils;
import com.teamzero.chatter.databinding.FragmentMemberlistBinding;
import com.teamzero.chatter.model.User;
import com.teamzero.chatter.viewholders.MembersAdapter;

import java.util.Map;

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
        adapter = new MembersAdapter(getContext(), chatID);

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

        mDatabase.getReference("chats").child(chatID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Map<Boolean, String> moderators = (Map)snapshot.child("authorized").getValue();
                if(snapshot.child("adminUID").getValue(String.class).equals(mAuth.getCurrentUser().getUid())){
                    adapter.setRole(Role.ADMIN);
                } else if (moderators != null && moderators.containsKey(mAuth.getCurrentUser().getUid())){
                    adapter.setRole(Role.MODERATOR);
                } else {
                    adapter.setRole(Role.MEMBER);
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
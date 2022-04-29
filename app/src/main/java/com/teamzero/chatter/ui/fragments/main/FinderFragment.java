package com.teamzero.chatter.ui.fragments.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.teamzero.chatter.databinding.FragmentFinderBinding;
import com.teamzero.chatter.model.Chat;

public class FinderFragment extends Fragment {

    private FragmentFinderBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentFinderBinding.inflate(inflater, container, false);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        View root = binding.getRoot();
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final Button createChatButton = binding.createChatButton;
        final Button findChatButton = binding.findChatButton;
        final EditText tags = binding.editTags;
        final EditText newChatName = binding.editChatName;

/*        createChatButton.setOnClickListener((v)->{
            Chat chat = new Chat(mDatabase.getReference("chats")., mAuth.getCurrentUser().getUid());
        });*/
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
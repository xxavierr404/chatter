package com.teamzero.chatter.ui.fragments.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.teamzero.chatter.MainActivity;
import com.teamzero.chatter.R;
import com.teamzero.chatter.databinding.FragmentFinderBinding;
import com.teamzero.chatter.model.Chat;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

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

        createChatButton.setOnClickListener((v)->{
            String key = mDatabase.getReference("chats").push().getKey();
            Chat chat = new Chat(key, mAuth.getCurrentUser().getUid());
            chat.addMember(mAuth.getCurrentUser().getUid());
            if(!tags.getText().toString().trim().isEmpty()){
                chat.setTags(Arrays.asList(tags.getText().toString().trim().split(",")));
            }
            MainActivity.getUserInfo().addChat(key);
            mDatabase.getReference("chats").child(key)
                    .setValue(chat).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        mDatabase.getReference("users").child(mAuth.getCurrentUser().getUid())
                                .setValue(MainActivity.getUserInfo()).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()) {
                                    Toast.makeText(getContext(), R.string.chat_created, Toast.LENGTH_LONG).show();
                                    // TODO: 02.05.2022 Переход на фрагмент с новым чатом
                                } else {
                                    Toast.makeText(getContext(), R.string.error, Toast.LENGTH_SHORT).show();
                                    mDatabase.getReference("chats").child(key).removeValue();
                                }
                            }
                        });
                    } else {
                        Toast.makeText(getContext(), R.string.error, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
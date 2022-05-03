package com.teamzero.chatter.ui.fragments.main;

import android.os.Bundle;
import android.util.Log;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.teamzero.chatter.MainActivity;
import com.teamzero.chatter.R;
import com.teamzero.chatter.databinding.FragmentFinderBinding;
import com.teamzero.chatter.model.Chat;
import com.teamzero.chatter.model.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
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

/*        if(!tags.getText().toString().trim().isEmpty()){
            chat.setTags(Arrays.asList(tags.getText().toString().trim().split(",")));
        }*/

        createChatButton.setOnClickListener((v)-> {
            String key = mDatabase.getReference("chats").push().getKey();
            Chat chat = new Chat(mAuth.getCurrentUser().getUid());
            chat.addMember(mAuth.getCurrentUser().getUid());
            String name = newChatName.getText().toString().trim();
            if(!name.isEmpty()) chat.setName(name);
            User user = new User();
            mDatabase.getReference("users").child(mAuth.getCurrentUser().getUid())
                    .child("chatIDs").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for(DataSnapshot dss: snapshot.getChildren()){
                        user.addChat(dss.getValue().toString());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            user.addChat(key);
            mDatabase.getReference("chats").child(key)
                    .setValue(chat).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        mDatabase.getReference("users").child(mAuth.getCurrentUser().getUid())
                                .child("chatIDs").setValue(user.getChatIDs()).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()) {
                                    Toast.makeText(getContext(), R.string.chat_created, Toast.LENGTH_LONG).show();
                                    // TODO: 02.05.2022 Переход на фрагмент с новым чатом
                                } else {
                                    Toast.makeText(getContext(), R.string.error, Toast.LENGTH_SHORT).show();
                                    Log.e("UserERR", task.getException().getMessage());
                                    mDatabase.getReference("chats").child(key).removeValue();
                                }
                            }
                        });
                    } else {
                        Toast.makeText(getContext(), R.string.error, Toast.LENGTH_SHORT).show();
                        Log.e("ChatERR", task.getException().getMessage());
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
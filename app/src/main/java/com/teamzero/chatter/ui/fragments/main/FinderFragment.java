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
import com.teamzero.chatter.Utils;
import com.teamzero.chatter.databinding.FragmentFinderBinding;
import com.teamzero.chatter.model.Chat;
import com.teamzero.chatter.model.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
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
        mDatabase = Utils.getDatabase();
        View root = binding.getRoot();
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final Button createChatButton = binding.createChatButton;
        final Button findChatButton = binding.findChatButton;
        final EditText tagsForExisting = binding.editTags;
        final EditText tagsForNew = binding.editTagsForNew;
        final EditText newChatName = binding.editChatName;

        createChatButton.setOnClickListener((v)-> {
            String key = mDatabase.getReference("chats").push().getKey();
            Chat chat = new Chat(key, mAuth.getCurrentUser().getUid());
            chat.addMember(mAuth.getCurrentUser().getUid());
            String name = newChatName.getText().toString().trim();
            String tags = tagsForNew.getText().toString().trim();
            if(!name.isEmpty()) chat.setName(name);
            if(!tags.isEmpty()){
                List<String> tagsList = new ArrayList<String>(Arrays.asList(tags.replaceAll("\\s+","").split(",")));
                tagsList.removeAll(Collections.singletonList(""));
                for(String tag: tagsList){
                    chat.addTag(tag);
                }
            }
            mDatabase.getReference("chats").child(key)
                    .setValue(chat).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        mDatabase.getReference("users").child(mAuth.getCurrentUser().getUid())
                                .child("chatIDs").child(key).setValue(true).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()) {
                                    tagsForNew.setText("");
                                    newChatName.setText("");
                                    Toast.makeText(getContext(), R.string.chat_created, Toast.LENGTH_LONG).show();
                                    getActivity().getSupportFragmentManager().beginTransaction()
                                            .replace(R.id.frame, new ChatlogFragment(key))
                                            .addToBackStack("chatWindow").commit();
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
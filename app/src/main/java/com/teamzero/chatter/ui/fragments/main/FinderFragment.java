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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import java.util.Map;
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

        findChatButton.setOnClickListener((v) -> {
            String tags = tagsForExisting.getText().toString().trim();
            tagsForExisting.setText("");
            HashSet<String> tagsSet = new HashSet<>(Arrays.asList(tags.replaceAll("\\s+","").split(",")));
            tagsSet.removeAll(Collections.singletonList(""));
            mDatabase.getReference("chats").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    ArrayList<String> candidates = new ArrayList<>();
                    for(DataSnapshot snap: snapshot.getChildren()){
                        if((tagsSet.size() == 0 || (snap.child("tags").exists() && ((Map)snap.child("tags").getValue()).keySet().containsAll(tagsSet)))
                            && !((Map)snap.child("members").getValue()).containsKey(mAuth.getCurrentUser().getUid())){
                            candidates.add(snap.child("id").getValue(String.class));
                        }
                    }
                    if(candidates.size() == 0){
                        Toast.makeText(getContext(), R.string.chat_not_found, Toast.LENGTH_LONG).show();
                        return;
                    }
                    Collections.shuffle(candidates);
                    mDatabase.getReference("chats").child(candidates.get(0))
                            .child("members").child(mAuth.getCurrentUser().getUid())
                            .setValue(true).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            mDatabase.getReference().child("users")
                                    .child(mAuth.getCurrentUser().getUid())
                                    .child("chatIDs")
                                    .child(candidates.get(0))
                                    .setValue(true).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(getContext(), R.string.welcome_new_chat, Toast.LENGTH_SHORT).show();
                                    getActivity().getSupportFragmentManager().beginTransaction()
                                            .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                                            .replace(R.id.frame, new ChatlogFragment(candidates.get(0)))
                                            .addToBackStack("chatWindow").commit();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getContext(), R.string.error, Toast.LENGTH_SHORT).show();
                                    Log.e("FinderERR", e.getMessage());
                                }
                            });
                        }
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getContext(), R.string.error, Toast.LENGTH_SHORT).show();
                    Log.e("FinderERR", error.getMessage());
                }
            });
        });

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
package com.teamzero.chatter.ui.fragments.main;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.ObjectKey;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.teamzero.chatter.R;
import com.teamzero.chatter.Utils;
import com.teamzero.chatter.databinding.FragmentProfileBinding;
import com.teamzero.chatter.model.Chat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.StringJoiner;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private FirebaseStorage mStorage;
    private Uri newPicUri;
    private String id;
    private boolean isChat = false;
    ActivityResultLauncher<Intent> activityResultLauncher;

    public ProfileFragment(String id, boolean isChat){
        this.id = id;
        this.isChat = isChat;
    }

    public ProfileFragment(){}

    public void setId(String id){
        this.id = id;
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();
                            if (data != null && data.getData() != null) {
                                newPicUri = data.getData();
                                Glide.with(getContext()).load(newPicUri).into(binding.profilePicture);
                            }
                        }
                    }
                });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAuth = FirebaseAuth.getInstance();

        if(mAuth.getCurrentUser() == null) return;

        mDatabase = Utils.getDatabase();
        mStorage = FirebaseStorage.getInstance();

        EditText nicknameField = binding.nickname;
        EditText bioField = binding.bioField;
        EditText tags = binding.editTagsInChatWindow;
        ImageView profilePicture = binding.profilePicture;
        ImageView indicator = binding.presenceIndicator;
        ImageButton backButton = binding.backButton;
        ProgressBar loading = binding.progressBar;

        backButton.setOnClickListener((v)->{
            getActivity().getSupportFragmentManager().popBackStack();
        });

        binding.logoutButton.setVisibility(View.GONE);
        binding.logoutButton.setClickable(false);

        indicator.setVisibility(View.GONE);
        tags.setVisibility(View.GONE);

        // TODO: 16.05.2022 Упростить ужасное повторение кода, следующее далее

        if(!isChat) {

            DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
            loading.setVisibility(View.VISIBLE);
            if(newPicUri == null) {
                Glide.with(getContext())
                        .load(FirebaseStorage.getInstance()
                                .getReference("profile_pics")
                                .child(id)
                                .child("profile.jpg"))
                        .error(R.drawable.astronaut)
                        .signature(new ObjectKey(System.currentTimeMillis()))
                        .into(profilePicture);
            }
            usersRef.child(id)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (mAuth.getCurrentUser() == null) return;
                            String nickname = String.valueOf(snapshot.child("nickname").getValue());
                            String bio = String.valueOf(snapshot.child("bio").getValue());
                            if (snapshot.child("connections").getChildrenCount() > 0) {
                                indicator.setImageResource(android.R.drawable.presence_online);
                            } else indicator.setImageResource(android.R.drawable.presence_offline);
                            nicknameField.setText(nickname);
                            bioField.setText(bio);
                            loading.setVisibility(View.GONE);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(getContext(), R.string.error, Toast.LENGTH_LONG).show();
                            loading.setVisibility(View.GONE);
                            Log.e("DatabaseERR", error.getMessage());
                        }
                    });
        } else {
            tags.setVisibility(View.VISIBLE);
            binding.bioTitle.setText(R.string.chat_description);
            binding.profileTitle.setText(R.string.chat_info_title);
            DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("chats");
            loading.setVisibility(View.VISIBLE);
            Glide.with(getContext())
                    .load(FirebaseStorage.getInstance()
                            .getReference("chat_pics")
                            .child(id)
                            .child("avatar.jpg"))
                    .error(R.drawable.astronaut)
                    .signature(new ObjectKey(System.currentTimeMillis()))
                    .into(profilePicture);
            usersRef.child(id)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @RequiresApi(api = Build.VERSION_CODES.O)
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (mAuth.getCurrentUser() == null) return;
                            String name = String.valueOf(snapshot.child("name").getValue());
                            String desc = String.valueOf(snapshot.child("desc").getValue());
                            List<String> tagsList = new ArrayList<>();
                            for(DataSnapshot snap: snapshot.child("tags").getChildren()){
                                tagsList.add(snap.getKey());
                            }
                            nicknameField.setText(name);
                            bioField.setText(desc);
                            loading.setVisibility(View.GONE);
                            tags.setText(String.join(", ", tagsList));
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(getContext(), R.string.error, Toast.LENGTH_LONG).show();
                            loading.setVisibility(View.GONE);
                            Log.e("DatabaseERR", error.getMessage());
                        }
                    });
        }
        if (!isChat) {
            if (mAuth.getCurrentUser().getUid().equals(id)) {

                backButton.setVisibility(View.GONE);
                binding.logoutButton.setVisibility(View.VISIBLE);
                binding.logoutButton.setClickable(true);

                binding.saveProfileButton.setOnClickListener((v) -> {
                    loading.setVisibility(View.VISIBLE);
                    String nickname = binding.nickname.getText().toString().trim();
                    String bio = binding.bioField.getText().toString().trim();
                    if (nickname.length() == 0) {
                        binding.nickname.setText("Chatman");
                        nickname = "Chatman";
                    }
                    HashMap<String, Object> update = new HashMap<>();
                    update.put("nickname", nickname);
                    update.put("bio", bio);
                    if (newPicUri != null) {
                        mStorage.getReference("profile_pics")
                                .child(id).child("profile.jpg").putFile(newPicUri);
                        newPicUri = null;
                    }
                    mDatabase.getReference("users").child(id)
                            .updateChildren(update).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(getContext(), R.string.save_success, Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getContext(), R.string.save_fail, Toast.LENGTH_LONG).show();
                            }
                            loading.setVisibility(View.GONE);
                        }
                    });
                });

                binding.logoutButton.setOnClickListener((v) -> {
                    logout();
                });

                profilePicture.setOnClickListener((v) -> {
                    Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    activityResultLauncher.launch(pickIntent);
                });
            } else {
                indicator.setVisibility(View.VISIBLE);
                binding.profilePicture.setClickable(false);
                binding.saveProfileButton.setVisibility(View.GONE);
                binding.saveProfileButton.setClickable(false);
                binding.nickname.setEnabled(false);
                binding.bioField.setEnabled(false);
            }
        } else {
            mDatabase.getReference("chats").child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Chat chat = snapshot.getValue(Chat.class);
                    String currentID = mAuth.getCurrentUser().getUid();
                    if (currentID.equals(chat.getAdminUID()) || chat.getAuthorized().containsKey(currentID)) {
                        binding.saveProfileButton.setOnClickListener((v) -> {
                            loading.setVisibility(View.VISIBLE);
                            String nickname = binding.nickname.getText().toString().trim();
                            String bio = binding.bioField.getText().toString().trim();
                            if (nickname.length() == 0) {
                                binding.nickname.setText("Unknown space");
                                nickname = "Unknown space";
                            }
                            HashSet<String> tagsSet = new HashSet<>(Arrays.asList(tags.getText().toString().toLowerCase().trim().replaceAll("\\s+","").split(",")));
                            tagsSet.removeAll(Collections.singletonList(""));
                            HashMap<String, Object> update = new HashMap<>();
                            for(String tag: tagsSet){
                                mDatabase.getReference("chats").child(id).child("tags").child(tag).setValue(true);
                            }
                            update.put("name", nickname);
                            update.put("desc", bio);
                            if (newPicUri != null) {
                                mStorage.getReference("chat_pics")
                                        .child(id).child("avatar.jpg").putFile(newPicUri);
                                newPicUri = null;
                            }
                            mDatabase.getReference("chats").child(id)
                                    .updateChildren(update).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(getContext(), R.string.save_success, Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(getContext(), R.string.save_fail, Toast.LENGTH_LONG).show();
                                    }
                                    loading.setVisibility(View.GONE);
                                }
                            });
                        });

                        profilePicture.setOnClickListener((v) -> {
                            Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            activityResultLauncher.launch(pickIntent);
                        });
                    } else {
                        tags.setEnabled(false);
                        tags.setClickable(false);
                        binding.profilePicture.setClickable(false);
                        binding.saveProfileButton.setVisibility(View.GONE);
                        binding.saveProfileButton.setClickable(false);
                        binding.nickname.setEnabled(false);
                        binding.bioField.setEnabled(false);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }


    private void logout(){
        synchronized (mAuth) {
            Utils.closeConnection();
            mAuth.signOut();
        }
        Toast.makeText(getContext(), R.string.left_chat, Toast.LENGTH_SHORT).show();
        ((AppCompatActivity)getActivity()).getSupportFragmentManager().popBackStack("Initial", FragmentManager.POP_BACK_STACK_INCLUSIVE);
        startActivity(new Intent(getContext(), getActivity().getClass()));
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
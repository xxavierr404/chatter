package com.teamzero.chatter.ui.fragments.main;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;
import com.teamzero.chatter.MainActivity;
import com.teamzero.chatter.R;
import com.teamzero.chatter.Utils;
import com.teamzero.chatter.databinding.FragmentProfileBinding;
import com.teamzero.chatter.model.Message;
import com.teamzero.chatter.model.User;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private FirebaseStorage mStorage;
    private Uri newPicUri;
    ActivityResultLauncher<Intent> activityResultLauncher;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        mStorage = FirebaseStorage.getInstance();
        newPicUri = null;
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
                            }
                        }
                    }
                });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        EditText nicknameField = binding.nickname;
        EditText bioField = binding.bioField;
        ImageView profilePicture = binding.profilePicture;
        ProgressBar loading = binding.progressBar;
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
        loading.setVisibility(View.VISIBLE);
        usersRef.child(mAuth.getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String nickname = String.valueOf(snapshot.child("nickname").getValue());
                        String bio = String.valueOf(snapshot.child("bio").getValue());
                        nicknameField.setText(nickname);
                        bioField.setText(bio);
                        FirebaseStorage.getInstance().getReference("profile_pics").child(mAuth.getCurrentUser().getUid())
                        .child("profile.jpg").getFile(new File(getActivity().getApplicationInfo().dataDir, "profile.jpg"))
                            .addOnCompleteListener(new OnCompleteListener<FileDownloadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<FileDownloadTask.TaskSnapshot> task) {
                                    if (task.isSuccessful()){
                                        profilePicture.setImageURI(Uri.fromFile(new File(getActivity().getApplicationInfo().dataDir, "profile.jpg")));
                                    } else {
                                        Log.e("ProfileERR", task.getException().getMessage());
                                    }
                                }
                            });
                        loading.setVisibility(View.GONE);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getContext(), R.string.error, Toast.LENGTH_LONG).show();
                        loading.setVisibility(View.GONE);
                        mAuth.signOut();
                        startActivity(new Intent(getContext(), MainActivity.class));
                        Log.e("DatabaseERR", error.getMessage());
                    }
                });

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
                FirebaseStorage.getInstance().getReference("profile_pics")
                        .child(mAuth.getCurrentUser().getUid()).child("profile.jpg").putFile(newPicUri);
                newPicUri = null;
            }
            mDatabase.getReference("users").child(mAuth.getCurrentUser().getUid())
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
    }


    private void logout(){
        Utils.closeConnection();
        mAuth.signOut();
        startActivity(new Intent(getContext(), getActivity().getClass()));
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
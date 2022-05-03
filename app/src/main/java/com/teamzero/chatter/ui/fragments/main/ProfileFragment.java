package com.teamzero.chatter.ui.fragments.main;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.teamzero.chatter.MainActivity;
import com.teamzero.chatter.R;
import com.teamzero.chatter.databinding.FragmentProfileBinding;
import com.teamzero.chatter.model.User;

import java.util.HashMap;
import java.util.Map;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        return binding.getRoot();
    }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        EditText nicknameField = binding.nickname;
        EditText bioField = binding.bioField;
        FirebaseDatabase.getInstance().getReference("users").child(mAuth.getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String nickname = String.valueOf(snapshot.child("nickname").getValue());
                        String bio = String.valueOf(snapshot.child("bio").getValue());
                        nicknameField.setText(nickname);
                        bioField.setText(bio);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getContext(), R.string.error, Toast.LENGTH_LONG).show();
                        Log.e("DatabaseERR", error.getMessage());
                    }
                });

        binding.saveProfileButton.setOnClickListener((v) -> {
            String nickname = binding.nickname.getText().toString().trim();
            String bio = binding.bioField.getText().toString().trim();
            if(nickname.length() == 0){
                binding.nickname.setText("Chatman");
                nickname = "Chatman";
            }
            HashMap<String, Object> update = new HashMap<>();
            update.put("nickname", nickname);
            update.put("bio", bio);
            mDatabase.getReference("users").child(mAuth.getCurrentUser().getUid())
                    .updateChildren(update).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(getContext(), R.string.save_success, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), R.string.save_fail, Toast.LENGTH_LONG).show();
                    }
                }
            });
        });

        binding.logoutButton.setOnClickListener((v)->{
            logout();
        });
    }

    private void logout(){
        mAuth.signOut();
        startActivity(new Intent(getContext(), getActivity().getClass()));
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
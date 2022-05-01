package com.teamzero.chatter.ui.fragments.main;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.firebase.database.FirebaseDatabase;
import com.teamzero.chatter.MainActivity;
import com.teamzero.chatter.R;
import com.teamzero.chatter.databinding.FragmentProfileBinding;
import com.teamzero.chatter.model.User;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        View root = binding.getRoot();
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try {
            binding.nickname.setText(MainActivity.getUserInfo().getNickname());
            binding.bioField.setText(MainActivity.getUserInfo().getBio());
            binding.saveProfileButton.setActivated(true);
        } catch(NullPointerException e) {
            Log.e("ProfileERR", e.getMessage());
            binding.nickname.setText("");
            binding.bioField.setText(getString(R.string.error));
            binding.saveProfileButton.setActivated(false);
        }

        binding.saveProfileButton.setOnClickListener((v) -> {
            MainActivity.getUserInfo().setNickname(binding.nickname.getText().toString());
            MainActivity.getUserInfo().setBio(binding.bioField.getText().toString());
            mDatabase.getReference("users").child(mAuth.getCurrentUser().getUid())
                    .setValue(MainActivity.getUserInfo()).addOnCompleteListener(new OnCompleteListener<Void>() {
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
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
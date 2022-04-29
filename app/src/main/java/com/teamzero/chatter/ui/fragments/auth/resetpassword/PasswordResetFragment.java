package com.teamzero.chatter.ui.fragments.auth.resetpassword;

import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.teamzero.chatter.R;
import com.teamzero.chatter.databinding.FragmentPasswordResetBinding;
import com.teamzero.chatter.databinding.FragmentRegisterBinding;
import com.teamzero.chatter.model.User;

public class PasswordResetFragment extends Fragment {

    private FragmentPasswordResetBinding binding;
    private FirebaseAuth mAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        binding = FragmentPasswordResetBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final EditText usernameEditText = binding.username;
        final Button resetButton = binding.resetButton;
        final Button backButton = binding.back;
        final ProgressBar loadingProgressBar = binding.loading;

        resetButton.setOnClickListener((v) -> {

            String username = usernameEditText.getText().toString();

            if(!Patterns.EMAIL_ADDRESS.matcher(username).matches()){
                usernameEditText.setError(getString(R.string.invalid_username));
                usernameEditText.requestFocus();
                return;
            }

            loadingProgressBar.setVisibility(View.VISIBLE);
            reset(username);
            loadingProgressBar.setVisibility(View.GONE);
        });

        backButton.setOnClickListener((v)-> getActivity().getSupportFragmentManager().popBackStack());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void reset(String email){
        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(getContext(), R.string.confirmation_sent, Toast.LENGTH_SHORT).show();
                    getActivity().getSupportFragmentManager().popBackStack();
                } else {
                    Toast.makeText(getContext(), R.string.check_correct_email, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

}
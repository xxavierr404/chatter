package com.teamzero.chatter.ui.fragments.auth.register;

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
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.teamzero.chatter.R;
import com.teamzero.chatter.databinding.FragmentRegisterBinding;
import com.teamzero.chatter.model.User;

public class RegisterFragment extends Fragment {

    private FragmentRegisterBinding binding;
    private FirebaseAuth mAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        binding = FragmentRegisterBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final EditText usernameEditText = binding.username;
        final EditText passwordEditText = binding.password;
        final Button registerButton = binding.signUpButton;
        final Button backButton = binding.back;
        final ProgressBar loadingProgressBar = binding.loading;

        registerButton.setOnClickListener((v) -> {

            String username = usernameEditText.getText().toString();
            String password = passwordEditText.getText().toString();

            if(!Patterns.EMAIL_ADDRESS.matcher(username).matches()){
                usernameEditText.setError(getString(R.string.invalid_username));
                usernameEditText.requestFocus();
                return;
            }

            if(password.trim().length() < 6) {
                passwordEditText.setError(getString(R.string.invalid_password));
                passwordEditText.requestFocus();
                return;
            }

            loadingProgressBar.setVisibility(View.VISIBLE);
            signUp(username, password);
            loadingProgressBar.setVisibility(View.GONE);
        });

        backButton.setOnClickListener((v)-> getActivity().getSupportFragmentManager().popBackStack());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void signUp(String username, String password){
        mAuth.createUserWithEmailAndPassword(username, password).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                Toast.makeText(getContext(), R.string.confirmation_sent, Toast.LENGTH_LONG).show();
                task.getResult().getUser().sendEmailVerification();

                User user = new User("Chatman", getString(R.string.default_bio));
                FirebaseDatabase.getInstance().getReference("users")
                        .child(task.getResult().getUser().getUid()).setValue(user);

                getActivity().getSupportFragmentManager().popBackStack();
            } else {
                Toast.makeText(getContext(), R.string.register_failed, Toast.LENGTH_LONG).show();
            }
        });
    }

}
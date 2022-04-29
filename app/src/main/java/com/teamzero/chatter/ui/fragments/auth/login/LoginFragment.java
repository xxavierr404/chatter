package com.teamzero.chatter.ui.fragments.auth.login;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.teamzero.chatter.R;
import com.teamzero.chatter.databinding.FragmentLoginBinding;
import com.teamzero.chatter.ui.fragments.auth.register.RegisterFragment;

public class LoginFragment extends Fragment {

    private FragmentLoginBinding binding;
    private FirebaseAuth mAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        mAuth = FirebaseAuth.getInstance();
        binding = FragmentLoginBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final EditText usernameEditText = binding.username;
        final EditText passwordEditText = binding.password;
        final Button loginButton = binding.loginPrompt;
        final Button registerButton = binding.signUpPrompt;
        final ProgressBar loadingProgressBar = binding.loading;

        loginButton.setOnClickListener((v) -> {

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
            login(username, password);
            loadingProgressBar.setVisibility(View.GONE);
        });

        registerButton.setOnClickListener((v) -> getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame, new RegisterFragment())
                .addToBackStack("signUp")
                .commit());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void login(String username, String password){
        mAuth.signInWithEmailAndPassword(username, password).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                if(task.getResult().getUser().isEmailVerified()) {
                    Toast.makeText(getContext(), R.string.welcome, Toast.LENGTH_SHORT).show();
                    getActivity().getSupportFragmentManager().beginTransaction().detach(this).commit();
                    ((AppCompatActivity) getActivity()).getSupportActionBar().show();
                } else {
                    Toast.makeText(getContext(), R.string.confirmation_sent, Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getContext(), R.string.login_failed, Toast.LENGTH_SHORT).show();
            }
        });
    }

}
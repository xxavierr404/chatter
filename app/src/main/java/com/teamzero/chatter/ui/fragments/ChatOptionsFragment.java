package com.teamzero.chatter.ui.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.teamzero.chatter.R;
import com.teamzero.chatter.Utils;
import com.teamzero.chatter.databinding.FragmentChatOptionsBinding;
import com.teamzero.chatter.databinding.FragmentChatlogBinding;
import com.teamzero.chatter.model.Chat;
import com.teamzero.chatter.ui.fragments.main.ProfileFragment;

public class ChatOptionsFragment extends Fragment {

    private FragmentChatOptionsBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private Chat chat;

    public ChatOptionsFragment(Chat chat){
        this.chat = chat;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        mDatabase = Utils.getDatabase();
        binding = FragmentChatOptionsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button editChat = binding.editChat;
        Button leaveChat = binding.leaveChat;
        Button deleteChat = binding.deleteChat;
        ImageButton close = binding.closeSettings;
        final MediaPlayer mp = MediaPlayer.create(getContext(), R.raw.chat_deleted);

        close.setOnClickListener((v)->{
            getActivity().getSupportFragmentManager().popBackStack("chatOptions", 1);
        });

        String currentID = mAuth.getCurrentUser().getUid();

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        DialogInterface.OnClickListener deleteClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                switch(i){
                    case DialogInterface.BUTTON_POSITIVE: {
                        getActivity().getSupportFragmentManager().popBackStack("chatWindow", 1);
                        getActivity().getSupportFragmentManager().popBackStack("chatOptions", 1);
                        mDatabase.getReference("chats").child(chat.getId()).removeValue();
                        for (String userID : chat.getMembers().keySet()) {
                            mDatabase.getReference("users").child(userID).child("chatIDs").child(chat.getId()).removeValue();
                        }
                        Toast.makeText(getContext(), R.string.left_chat, Toast.LENGTH_SHORT).show();
                        mp.start();
                        break;
                    }
                    case DialogInterface.BUTTON_NEGATIVE: break;
                }
            }
        };

        DialogInterface.OnClickListener leaveClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                switch(i){
                    case DialogInterface.BUTTON_POSITIVE: {
                        mDatabase.getReference("chats").child(chat.getId()).child("members").child(currentID).removeValue();
                        mDatabase.getReference("users").child(currentID).child("chatIDs").child(chat.getId()).removeValue();
                        getActivity().getSupportFragmentManager().popBackStack("chatWindow", 1);
                        getActivity().getSupportFragmentManager().popBackStack("chatOptions", 1);
                        Toast.makeText(getContext(), R.string.left_chat, Toast.LENGTH_SHORT).show();
                        mp.start();
                        break;
                    }
                    case DialogInterface.BUTTON_NEGATIVE: break;
                }
            }
        };

        leaveChat.setOnClickListener((v) -> {
            builder.setTitle(R.string.alert_are_you_sure).setMessage(R.string.alert_cannot_be_undone)
                    .setPositiveButton(R.string.yes, leaveClickListener)
                    .setNegativeButton(R.string.no, leaveClickListener).show();
        });

        editChat.setOnClickListener((v)->{
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out)
                    .add(R.id.frame, new ProfileFragment(chat.getId(), true))
                    .addToBackStack("chatInfo").commit();
        });

        if(chat.getAdminUID().equals(currentID) || chat.getAuthorized().containsKey(currentID)){

            leaveChat.setEnabled(false);

            deleteChat.setOnClickListener((v) -> {
                builder.setTitle(R.string.alert_are_you_sure).setMessage(R.string.alert_cannot_be_undone)
                        .setPositiveButton(R.string.yes, deleteClickListener)
                        .setNegativeButton(R.string.no, deleteClickListener).show();
            });

        } else {
            deleteChat.setEnabled(false);
        }
    }
}

package com.teamzero.chatter.ui.fragments.chats;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.teamzero.chatter.R;
import com.teamzero.chatter.databinding.FragmentChatOptionsBinding;
import com.teamzero.chatter.databinding.FragmentMemberManageBinding;
import com.teamzero.chatter.model.Chat;
import com.teamzero.chatter.model.User;
import com.teamzero.chatter.ui.fragments.main.ProfileFragment;
import com.teamzero.chatter.utils.Utils;

public class MemberManageFragment extends Fragment {

    private FragmentMemberManageBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private Chat chat;
    private User member;


    public MemberManageFragment(Chat chat, User member){
        this.chat = chat;
        this.member = member;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        mDatabase = Utils.getDatabase();
        binding = FragmentMemberManageBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ConstraintLayout background = binding.backgroundDim;
        TextView title = binding.titleMemberAction;
        Button kickButton = binding.kickButton;
        Button switchRoleButton = binding.switchRoleButton;

        AudioAttributes attributes = new AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .build();
        SoundPool soundPool = new SoundPool.Builder()
                .setAudioAttributes(attributes)
                .setMaxStreams(3)
                .build();

        int kickSoundId = soundPool.load(getContext(), R.raw.kick, 1);
        int promoteSoundId = soundPool.load(getContext(), R.raw.promote, 2);
        int demoteSoundId = soundPool.load(getContext(), R.raw.demote, 3);

        title.setText(String.format(getString(R.string.manage), member.getNickname()));

        background.setOnClickListener((v)->{
            getActivity().getSupportFragmentManager().popBackStack("manageMember", 1);
        });

        String currentID = mAuth.getCurrentUser().getUid();

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        DialogInterface.OnClickListener kickClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                switch(i){
                    case DialogInterface.BUTTON_POSITIVE: {
                        chat.kickMember(member.getId());
                        if(chat.getAuthorized().containsKey(member.getId())) chat.removeAuthorized(member.getId());
                        mDatabase.getReference("chats").child(chat.getId())
                                .child("authorized").child(member.getId()).removeValue();
                        mDatabase.getReference("chats").child(chat.getId())
                                .child("members").child(member.getId()).removeValue();
                        mDatabase.getReference("users").child(member.getId())
                                .child("chatIDs").child(chat.getId()).removeValue();
                        Toast.makeText(getContext(), R.string.member_kicked, Toast.LENGTH_SHORT).show();
                        getActivity().getSupportFragmentManager().popBackStack("manageMember", 1);
                        getActivity().getSupportFragmentManager().popBackStack("members", 1);
                        soundPool.play(kickSoundId, 1, 1, 1, 0, 1);
                        break;
                    }
                    case DialogInterface.BUTTON_NEGATIVE: break;
                }
            }
        };

        if(chat.getAuthorized().containsKey(currentID) || chat.getAdminUID().equals(currentID)) {
            kickButton.setOnClickListener((v) -> {
                builder.setTitle(R.string.alert_are_you_sure).setMessage(R.string.alert_cannot_be_undone)
                        .setPositiveButton(R.string.yes, kickClickListener)
                        .setNegativeButton(R.string.no, kickClickListener).show();
            });
            if(member.getId().equals(chat.getAdminUID())){
                kickButton.setEnabled(false);
                kickButton.setVisibility(View.GONE);
            }
        }

        if(chat.getAdminUID().equals(currentID)){
            switchRoleButton.setEnabled(true);
            if(chat.getAuthorized().containsKey(member.getId())){
                switchRoleButton.setText(R.string.demoteButton);
                switchRoleButton.setOnClickListener((v)->{
                    chat.removeAuthorized(member.getId());
                    mDatabase.getReference("chats")
                            .child(chat.getId())
                            .child("authorized")
                            .child(member.getId())
                            .removeValue()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    getActivity().getSupportFragmentManager().popBackStack("manageMember", 1);
                                }
                            });
                    soundPool.play(demoteSoundId, 1, 1, 1, 0, 1);
                });
            } else {
                switchRoleButton.setOnClickListener((v)->{
                    chat.addAuthorized(member.getId());
                    mDatabase.getReference("chats")
                            .child(chat.getId())
                            .child("authorized")
                            .child(member.getId())
                            .setValue(true)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    getActivity().getSupportFragmentManager().popBackStack("manageMember", 1);
                                }
                            });
                    soundPool.play(promoteSoundId, 1, 1, 1, 0, 1);
                });
            }
        } else {
            switchRoleButton.setEnabled(false);
            switchRoleButton.setVisibility(View.GONE);
        }
    }
}

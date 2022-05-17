package com.teamzero.chatter.viewholders;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.ObjectKey;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.teamzero.chatter.R;
import com.teamzero.chatter.Role;
import com.teamzero.chatter.Utils;
import com.teamzero.chatter.model.User;
import com.teamzero.chatter.ui.fragments.main.ProfileFragment;

import java.util.ArrayList;
import java.util.List;

public class MembersAdapter extends RecyclerView.Adapter<MembersAdapter.MemberInfoHolder> {

    private List<User> members;
    private Context ctx;
    private Role role = Role.MEMBER;

    public void setRole(Role role){
        this.role = role;
    }

    public MembersAdapter(Context ctx){
        this.members = new ArrayList<>();
        this.ctx = ctx;
    }

    public void addMember(User member){
        members.add(member);
    }

    public void updateMember(User member){
        for(User m: members){
            if(m.getId().equals(member.getId())) {
                m = member;
                return;
            }
        }
    }

    public void removeMember(User member){
        members.remove(member);
    }

    public void clear(){
        members.clear();
    }

    @NonNull
    @Override
    public MemberInfoHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MemberInfoHolder(LayoutInflater.from(ctx).inflate(R.layout.layout_member,parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MemberInfoHolder holder, int position) {
        User member = members.get(position);

        Glide.with(ctx)
                .load(FirebaseStorage.getInstance().getReference("profile_pics")
                .child(member.getId())
                .child("profile.jpg"))
                .error(R.drawable.astronaut)
                .signature(new ObjectKey(System.currentTimeMillis()))
                .into(holder.memberAvatar);

        Utils.getDatabase().getReference("users").child(member.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                holder.memberName.setText(snapshot.child("nickname").getValue(String.class));
                if(snapshot.child("connections").getChildrenCount() > 0){
                    holder.presence.setImageResource(android.R.drawable.presence_online);
                } else {
                    holder.presence.setImageResource(android.R.drawable.presence_offline);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        switch(role){
            case MEMBER:{
                holder.itemView.setOnClickListener((v)->{
                    ((AppCompatActivity)ctx).getSupportFragmentManager()
                        .beginTransaction()
                        .add(R.id.frame, new ProfileFragment(member.getId(), false))
                        .addToBackStack("profile").commit();
                });
                break;
            }
            default:
                holder.itemView.setOnClickListener((v)->{});
        }

    }

    @Override
    public int getItemCount() {
        return members.size();
    }

    public class MemberInfoHolder extends RecyclerView.ViewHolder {
        TextView memberName;
        ImageView memberAvatar;
        ImageView presence;

        public MemberInfoHolder(@NonNull View itemView) {
            super(itemView);
            memberName = itemView.findViewById(R.id.memberNameField);
            memberAvatar = itemView.findViewById(R.id.memberAvatar);
            presence = itemView.findViewById(R.id.memberPresence);
        }
    }
}

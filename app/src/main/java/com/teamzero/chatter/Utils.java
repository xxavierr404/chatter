package com.teamzero.chatter;

import android.content.Context;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.module.AppGlideModule;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;

import java.io.InputStream;
import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;

@GlideModule
public class Utils extends AppGlideModule {

    private static FirebaseDatabase mDatabase;
    private static String connectionKey;

    public static FirebaseDatabase getDatabase(){
        if(mDatabase == null){
            mDatabase = FirebaseDatabase.getInstance();
            mDatabase.setPersistenceEnabled(true);
            DatabaseReference ref = mDatabase.getReference("users").child(FirebaseAuth.getInstance()
                    .getCurrentUser().getUid()).child("connections");
            connectionKey = ref.push().getKey();
            ref.child(connectionKey).setValue(true);
            ref.child(connectionKey).onDisconnect().removeValue();
        }
        return mDatabase;
    }

    public static String getTimeDate(long timestamp) {
        try {
            DateFormat dateFormat = DateFormat.getDateTimeInstance();
            Date netDate = (new Date(timestamp));
            return dateFormat.format(netDate);
        } catch (Exception e) {
            return "date";
        }
    }

    public static void closeConnection(){
        mDatabase.getReference("users").child(FirebaseAuth.getInstance()
                .getCurrentUser().getUid()).child("connections").child(connectionKey)
        .removeValue();
    }

    @Override
    public void registerComponents(@NonNull Context context, @NonNull Glide glide, @NonNull Registry registry) {
        registry.append(StorageReference.class, InputStream.class,
                new FirebaseImageLoader.Factory());
    }
}

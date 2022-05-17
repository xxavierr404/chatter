package com.teamzero.chatter.utils;

import android.content.Context;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.module.AppGlideModule;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
    private static boolean connected;

    public static FirebaseDatabase getDatabase(){
        if(mDatabase == null || !connected){
            mDatabase = FirebaseDatabase.getInstance();
            DatabaseReference ref = mDatabase.getReference("users").child(FirebaseAuth.getInstance()
                    .getCurrentUser().getUid()).child("connections");
            connectionKey = ref.push().getKey();
            ref.child(connectionKey).setValue(true);
            connected = true;
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
        mDatabase.getReference(connectionKey).onDisconnect().cancel();
        mDatabase.getReference("users").child(FirebaseAuth.getInstance()
                .getCurrentUser().getUid()).child("connections").child(connectionKey)
                .removeValue();
        connected = false;
    }

    @Override
    public void registerComponents(@NonNull Context context, @NonNull Glide glide, @NonNull Registry registry) {
        registry.append(StorageReference.class, InputStream.class,
                new FirebaseImageLoader.Factory());
    }
}

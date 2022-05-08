package com.teamzero.chatter;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;

public class Utils {

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

}

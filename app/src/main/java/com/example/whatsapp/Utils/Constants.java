package com.example.whatsapp.Utils;

import com.google.firebase.auth.FirebaseAuth;

public class Constants {

    public static final String USERS = "Users";
    public static final String GROUPS = "Groups";
    public static final String CONTACTS = "Contacts";

    public static String getUID() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

}

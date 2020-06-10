package com.example.whatsapp.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.example.whatsapp.R;

public class ChatActivity extends AppCompatActivity {

    private String messageUserID , messageUserName;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        toolbar = findViewById(R.id.chat_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(messageUserName);

        messageUserID = getIntent().getExtras().get("userID").toString();
        messageUserName = getIntent().getExtras().get("userName").toString();
//        Toast.makeText(this, messageUserID + "\n" + messageUserName, Toast.LENGTH_LONG).show();

    }
}

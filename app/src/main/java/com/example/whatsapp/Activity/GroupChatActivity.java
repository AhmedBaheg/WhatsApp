package com.example.whatsapp.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.whatsapp.Fragment.GroupsFragment;
import com.example.whatsapp.Model.Group.GroupMessageInfo;
import com.example.whatsapp.R;
import com.example.whatsapp.Utils.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class GroupChatActivity extends AppCompatActivity {

    private Toolbar mtoToolbar;
    private ScrollView scrollView;
    private TextView tv_Group_Chat_Text_Display;
    private EditText ed_Message_Group_Chat;
    private FloatingActionButton btn_Send_Message_Group_Chat;

    public String currentUserName;

    // Firebase
    private DatabaseReference userRef, groupNameRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);

        userRef = FirebaseDatabase.getInstance().getReference(Constants.USERS);
        groupNameRef = FirebaseDatabase.getInstance().getReference(Constants.GROUPS).child(GroupsFragment.positionItem);

        initializeView();

        getUserInfo();

        btn_Send_Message_Group_Chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveMessageInfo();
                ed_Message_Group_Chat.setText("");
                scrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        groupNameRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.exists()){
                    displayGroupMessages(dataSnapshot);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.exists()){
                    displayGroupMessages(dataSnapshot);
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void initializeView() {

        mtoToolbar = findViewById(R.id.group_chat_bar);
        setSupportActionBar(mtoToolbar);
        getSupportActionBar().setTitle(GroupsFragment.positionItem);

        scrollView = findViewById(R.id.scroll);
        tv_Group_Chat_Text_Display = findViewById(R.id.tv_group_chat_text_display);
        ed_Message_Group_Chat = findViewById(R.id.ed_message_group_chat);
        btn_Send_Message_Group_Chat = findViewById(R.id.btn_send_message_group_chat);

    }

    private void getUserInfo() {

        userRef.child(Constants.getUID()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    currentUserName = dataSnapshot.child("name").getValue().toString();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void saveMessageInfo() {

        final String message = ed_Message_Group_Chat.getText().toString();
        String messageKey = groupNameRef.push().getKey();
        if (message.isEmpty()) {
            Toast.makeText(this, "Please Write The Message...", Toast.LENGTH_LONG).show();
        } else {

            Calendar calendar = Calendar.getInstance();
            String currentDate = new SimpleDateFormat("MMM dd ,yyyy", Locale.getDefault()).format(calendar.getTime());
            String currentTime = new SimpleDateFormat("hh:mm aa", Locale.getDefault()).format(calendar.getTime());

            GroupMessageInfo messageInfo = new GroupMessageInfo(currentUserName, currentDate, currentTime, message);
            groupNameRef.child(messageKey).setValue(messageInfo).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(GroupChatActivity.this, message, Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }

    }

    @SuppressLint("SetTextI18n")
    private void displayGroupMessages(DataSnapshot dataSnapshot) {

        GroupMessageInfo info = dataSnapshot.getValue(GroupMessageInfo.class);
//        String chatName = info.getName();
//        String chatMessage = info.getMessage();
//        String chatDate = info.getDate();
//        String chatTime = info.getTime();
        tv_Group_Chat_Text_Display.append(info.getName() + "\n" + info.getMessage() + "\n"
                + info.getTime() + "\n" + info.getDate() + "\n\n");

        scrollView.fullScroll(ScrollView.FOCUS_DOWN);

    }

}

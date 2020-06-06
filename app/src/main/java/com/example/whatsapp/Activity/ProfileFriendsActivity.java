package com.example.whatsapp.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.whatsapp.Model.User;
import com.example.whatsapp.R;
import com.example.whatsapp.Utils.Constants;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFriendsActivity extends AppCompatActivity {

    private CircleImageView image_Profile_User_Find_Friends;
    private TextView tv_Name_Profile_Find_Friends , tv_Status;
    private ImageButton  btn_Add_Friend , btn_Message_Friend;

    private User user;

    // Firebase
    private DatabaseReference rootRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_friends);

        rootRef = FirebaseDatabase.getInstance().getReference(Constants.USERS);

        initializeView();
        displayProfileFriends();

    }

    private void displayProfileFriends(){

        rootRef.child(FindFriendsActivity.key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(User.class);
                tv_Name_Profile_Find_Friends.setText(user.getName());
                tv_Status.setText(user.getStatus());
                Picasso.get()
                        .load(user.getImageUrl())
                        .placeholder(R.drawable.default_pic_user)
                        .error(R.drawable.default_pic_user)
                        .into(image_Profile_User_Find_Friends);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void initializeView() {

        image_Profile_User_Find_Friends = findViewById(R.id.image_profile_user_find_friends);
        tv_Name_Profile_Find_Friends = findViewById(R.id.tv_name_profile_find_friends);
        tv_Status = findViewById(R.id.tv_status);
        btn_Add_Friend = findViewById(R.id.btn_add_friend);
        btn_Message_Friend = findViewById(R.id.btn_message_friend);

    }
}

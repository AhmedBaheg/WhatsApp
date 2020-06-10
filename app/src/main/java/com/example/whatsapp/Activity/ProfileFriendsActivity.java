package com.example.whatsapp.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.whatsapp.Dialog.DialogDeleteFriends;
import com.example.whatsapp.Model.User;
import com.example.whatsapp.R;
import com.example.whatsapp.Utils.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFriendsActivity extends AppCompatActivity {

    private CircleImageView image_Profile_User_Find_Friends;
    private TextView tv_Name_Profile_Find_Friends, tv_Status;
    private ImageButton btn_Add_Friend, btn_Message_Friend;
    private Button btn_Accept, btn_Refuse;

    private User user;

    private String receiverUserID, sendUserID, current_State, name;

    // Firebase
    private DatabaseReference rootRef, requestRef, contactsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_friends);

        rootRef = FirebaseDatabase.getInstance().getReference(Constants.USERS);
        requestRef = FirebaseDatabase.getInstance().getReference("Requests");
        contactsRef = FirebaseDatabase.getInstance().getReference(Constants.CONTACTS);

        initializeView();
        displayProfileFriends();

    }

    private void displayProfileFriends() {

        rootRef.child(FindFriendsActivity.key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(User.class);
                name = user.getName();
                tv_Name_Profile_Find_Friends.setText(name);
                tv_Status.setText(user.getStatus());
                Picasso.get()
                        .load(user.getImageUrl())
                        .placeholder(R.drawable.default_pic_user)
                        .error(R.drawable.default_pic_user)
                        .into(image_Profile_User_Find_Friends);

                manageRequests();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void manageRequests() {

        requestRef.child(sendUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(receiverUserID)) {
                    String request_type = dataSnapshot.child(receiverUserID).child("request_type").getValue().toString();
                    if (request_type.equals("sent")) {
                        current_State = "request_sent";
                        btn_Add_Friend.setImageResource(R.drawable.request_send);
                    } else if (request_type.equals("received")) {
                        btn_Add_Friend.setVisibility(View.INVISIBLE);
                        btn_Message_Friend.setVisibility(View.INVISIBLE);

                        btn_Accept.setVisibility(View.VISIBLE);
                        btn_Refuse.setVisibility(View.VISIBLE);
                        btn_Refuse.setEnabled(true);
                        btn_Refuse.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                cancelRequest();
                            }
                        });
                        btn_Accept.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                acceptRequest();
                            }
                        });
                    }
                } else {

                    contactsRef.child(sendUserID)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.hasChild(receiverUserID)) {
                                        current_State = "friends";
                                        btn_Add_Friend.setImageResource(R.drawable.ic_done);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        if (sendUserID.equals(receiverUserID)) {
            btn_Add_Friend.setVisibility(View.INVISIBLE);
            btn_Message_Friend.setVisibility(View.INVISIBLE);
        } else {

            btn_Add_Friend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    btn_Message_Friend.setEnabled(false);
                    if (current_State.equals("new")) {
                        sendRequest();
                    }
                    if (current_State.equals("request_sent")) {
                        cancelRequest();
                    }
                    if (current_State.equals("friends")) {
                        final DialogDeleteFriends dialog = new DialogDeleteFriends(ProfileFriendsActivity.this);
                        dialog.dialog_Title.setText("UnFriend " + name);
                        dialog.dialog_Body.setText("Are you sure from delete " + name + " from your contact");
                        dialog.btn_Delete.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                removeFriendFromContact();
                                dialog.dismiss();
                            }
                        });
                        dialog.show();
                    }
                }
            });

        }

    }

    private void sendRequest() {

        requestRef.child(sendUserID).child(receiverUserID).child("request_type")
                .setValue("sent").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    requestRef.child(receiverUserID).child(sendUserID).child("request_type")
                            .setValue("received").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                btn_Add_Friend.setEnabled(true);
                                btn_Add_Friend.setImageResource(R.drawable.request_send);
                            }
                        }
                    });
                }
            }
        });

    }

    private void cancelRequest() {

        requestRef.child(sendUserID).child(receiverUserID).removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    requestRef.child(receiverUserID).child(sendUserID).removeValue()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                btn_Add_Friend.setEnabled(true);
                                btn_Add_Friend.setImageResource(R.drawable.ic_add_friend);
                                current_State = "new";

                                btn_Add_Friend.setVisibility(View.VISIBLE);
                                btn_Message_Friend.setVisibility(View.VISIBLE);

                                btn_Accept.setVisibility(View.INVISIBLE);
                                btn_Refuse.setVisibility(View.INVISIBLE);
                                btn_Refuse.setEnabled(false);
                            }
                        }
                    });
                }
            }
        });

    }

    private void acceptRequest() {

        contactsRef.child(sendUserID).child(receiverUserID).child("contact")
                .setValue("saved").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    contactsRef.child(receiverUserID).child(sendUserID).child("contact")
                            .setValue("saved").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                requestRef.child(sendUserID).child(receiverUserID)
                                        .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            requestRef.child(receiverUserID).child(sendUserID)
                                                    .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        btn_Add_Friend.setEnabled(true);
                                                        btn_Add_Friend.setImageResource(R.drawable.ic_done);
                                                        current_State = "new";

                                                        btn_Add_Friend.setVisibility(View.VISIBLE);
                                                        btn_Message_Friend.setVisibility(View.VISIBLE);

                                                        btn_Accept.setVisibility(View.INVISIBLE);
                                                        btn_Refuse.setVisibility(View.INVISIBLE);
                                                        btn_Refuse.setEnabled(false);
                                                    }
                                                }
                                            });
                                        }
                                    }
                                });
                            }
                        }
                    });
                }
            }
        });

    }

    private void removeFriendFromContact() {

        contactsRef.child(sendUserID).child(receiverUserID)
                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    contactsRef.child(receiverUserID).child(sendUserID)
                            .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                btn_Add_Friend.setEnabled(true);
                                btn_Add_Friend.setImageResource(R.drawable.ic_add_friend);
                                current_State = "new";

                                btn_Add_Friend.setVisibility(View.VISIBLE);
                                btn_Message_Friend.setVisibility(View.VISIBLE);

                                btn_Accept.setVisibility(View.INVISIBLE);
                                btn_Refuse.setVisibility(View.INVISIBLE);
                            }
                        }
                    });
                }
            }
        });

    }

    private void initializeView() {

        image_Profile_User_Find_Friends = findViewById(R.id.image_profile_user_find_friends);
        tv_Name_Profile_Find_Friends = findViewById(R.id.tv_name_profile_find_friends);
        tv_Status = findViewById(R.id.tv_status);
        btn_Add_Friend = findViewById(R.id.btn_add_friend);
        btn_Message_Friend = findViewById(R.id.btn_message_friend);
        btn_Accept = findViewById(R.id.btn_accept);
        btn_Refuse = findViewById(R.id.btn_refuse);

        current_State = "new";
        receiverUserID = FindFriendsActivity.key;
        sendUserID = Constants.getUID();

    }
}

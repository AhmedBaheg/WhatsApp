package com.example.whatsapp.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.whatsapp.Adapter.TabsAccessorAdapter;
import com.example.whatsapp.Dialog.DialogCreateNewGroup;
import com.example.whatsapp.R;
import com.example.whatsapp.Utils.Constants;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    Toolbar mToolbar;
    ViewPager myViewPager;
    TabLayout myTabLayout;
    TabsAccessorAdapter myTabsAccessorAdapter;

    // Firebase
    FirebaseUser currentUser;
    FirebaseAuth mAuth;
    DatabaseReference rootRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        rootRef = FirebaseDatabase.getInstance().getReference(Constants.USERS);

        mToolbar = findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("WhatsApp");

        myViewPager = findViewById(R.id.main_tabs_pager);
        myTabsAccessorAdapter = new TabsAccessorAdapter(getSupportFragmentManager(), 4);
        myViewPager.setAdapter(myTabsAccessorAdapter);

        myTabLayout = findViewById(R.id.main_tabs);
        myTabLayout.setupWithViewPager(myViewPager);

    }

    @Override
    protected void onStart() {
        super.onStart();

        if (currentUser == null) {
            sendUserToLoginActivity();
        } else {
            verifyUserExistence();
        }

    }

    private void verifyUserExistence() {

        rootRef.child(Constants.getUID()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if ((dataSnapshot.hasChild("name"))) {
                    Toast.makeText(MainActivity.this, "Welcome", Toast.LENGTH_LONG).show();
                } else {
                    sendUserToSettingsActivity();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void createNewGroup() {

        final DialogCreateNewGroup dialog = new DialogCreateNewGroup(this);
        dialog.show();
        dialog.btn_Create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.addNewGroup();
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.option_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);

        switch (item.getItemId()) {

            case R.id.create_group:
                createNewGroup();
                break;
            case R.id.find_friends:
                sendUserToFindFriendsActivity();
                break;
            case R.id.settings:
                sendUserToSettingsActivity();
                break;
            case R.id.logout:
                mAuth.signOut();
                sendUserToLoginActivity();
                break;
        }

        return true;

    }


    private void sendUserToLoginActivity() {
        Intent login = new Intent(this, LoginActivity.class);
        login.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(login);
        finish();
    }

    private void sendUserToSettingsActivity() {
        Intent settings = new Intent(this, SettingsActivity.class);
        startActivity(settings);
    }

    private void sendUserToFindFriendsActivity() {
        Intent findFriends = new Intent(this, FindFriendsActivity.class);
        startActivity(findFriends);
    }

}

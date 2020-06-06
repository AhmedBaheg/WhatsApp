package com.example.whatsapp.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.whatsapp.Model.Contacts;
import com.example.whatsapp.R;
import com.example.whatsapp.Utils.Constants;
import com.example.whatsapp.ViewHolder.FindFriendsViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class FindFriendsActivity extends AppCompatActivity {

    public static String key;

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    FirebaseRecyclerAdapter<Contacts, FindFriendsViewHolder> adapter;
    FirebaseRecyclerOptions<Contacts> options;
    DatabaseReference rootRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friends);

        rootRef = FirebaseDatabase.getInstance().getReference(Constants.USERS);

        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        toolbar = findViewById(R.id.find_friends_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Find Friends");

    }

    @Override
    protected void onStart() {
        super.onStart();

        options = new FirebaseRecyclerOptions.Builder<Contacts>()
                .setQuery(rootRef , Contacts.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<Contacts, FindFriendsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull FindFriendsViewHolder holder, final int position, @NonNull Contacts contacts) {

                holder.name_Find_Friend.setText(contacts.getName());
                holder.about_Find_Friend.setText(contacts.getStatus());
                Picasso.get()
                        .load(contacts.getImageUrl())
                        .placeholder(R.drawable.default_pic_user)
                        .error(R.drawable.default_pic_user)
                        .into(holder.image_Find_Friend);

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        key = getRef(position).getKey();
                        startActivity(new Intent(FindFriendsActivity.this , ProfileFriendsActivity.class));
                    }
                });

            }

            @NonNull
            @Override
            public FindFriendsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.find_friend_item , parent , false);
                return new FindFriendsViewHolder(view);
            }
        };

        recyclerView.setAdapter(adapter);
        adapter.startListening();

    }

}

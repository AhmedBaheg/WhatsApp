package com.example.whatsapp.Fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.whatsapp.Activity.ChatActivity;
import com.example.whatsapp.Activity.SettingsActivity;
import com.example.whatsapp.Model.Contacts;
import com.example.whatsapp.R;
import com.example.whatsapp.Utils.Constants;
import com.example.whatsapp.ViewHolder.ChatsViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChatsFragment extends Fragment {

    private View view;
    private RecyclerView recyclerView;

    private FirebaseRecyclerOptions<Contacts> options;
    private FirebaseRecyclerAdapter<Contacts , ChatsViewHolder> adapter;

    private DatabaseReference contactsRef , usersRef;

    public ChatsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_chats, container, false);

        contactsRef = FirebaseDatabase.getInstance().getReference(Constants.CONTACTS).child(Constants.getUID());
        usersRef = FirebaseDatabase.getInstance().getReference(Constants.USERS);

        recyclerView = view.findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        options = new FirebaseRecyclerOptions.Builder<Contacts>()
                .setQuery(contactsRef , Contacts.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<Contacts, ChatsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final ChatsViewHolder holder, int position, @NonNull Contacts model) {

                final String usersID = getRef(position).getKey();

                usersRef.child(usersID).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        Contacts contacts = dataSnapshot.getValue(Contacts.class);

                        final String name = contacts.getName();
                        holder.name_Find_Friend.setText(name);
                        holder.about_Find_Friend.setText("Last seen " + "\n" + "Date " + "Time");
                        Picasso.get()
                                .load(contacts.getImageUrl())
                                .placeholder(R.drawable.default_pic_user)
                                .error(R.drawable.default_pic_user)
                                .into(holder.image_Find_Friend);

                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                sendUserToChatsActivity(usersID , name);
                            }
                        });

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }

            @NonNull
            @Override
            public ChatsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                view = LayoutInflater.from(getContext()).inflate(R.layout.find_friend_item , parent , false);
                return new ChatsViewHolder(view);
            }
        };

        recyclerView.setAdapter(adapter);
        adapter.startListening();

    }

    private void sendUserToChatsActivity(String usersID , String name) {
        Intent chats = new Intent(getContext(), ChatActivity.class);
        chats.putExtra("userID"  , usersID);
        chats.putExtra("userName"  , name);
        startActivity(chats);
    }

}

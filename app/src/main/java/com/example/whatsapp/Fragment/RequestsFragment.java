package com.example.whatsapp.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.whatsapp.Model.Contacts;
import com.example.whatsapp.R;
import com.example.whatsapp.Utils.Constants;
import com.example.whatsapp.ViewHolder.RequestsViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

/**
 * A simple {@link Fragment} subclass.
 */
public class RequestsFragment extends Fragment {

    private View view;
    private RecyclerView recyclerView;

    private String senderID;

    private FirebaseRecyclerOptions<Contacts> options;
    private FirebaseRecyclerAdapter<Contacts, RequestsViewHolder> adapter;

    private DatabaseReference requestRef, userRef, contactRef;

    public RequestsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_requests, container, false);

        requestRef = FirebaseDatabase.getInstance().getReference("Requests");
        userRef = FirebaseDatabase.getInstance().getReference(Constants.USERS);
        contactRef = FirebaseDatabase.getInstance().getReference(Constants.CONTACTS);

        senderID = Constants.getUID();

        recyclerView = view.findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        options = new FirebaseRecyclerOptions.Builder<Contacts>()
                .setQuery(requestRef.child(Constants.getUID()), Contacts.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<Contacts, RequestsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final RequestsViewHolder holder, int position, @NonNull Contacts model) {

                final String receiverID = getRef(position).getKey();
                DatabaseReference typeRef = getRef(position).child("request_type");

                typeRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if (dataSnapshot.exists()) {

                            String type = dataSnapshot.getValue().toString();
                            if (type.equals("received")) {


                                userRef.child(receiverID).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                        Contacts contacts = dataSnapshot.getValue(Contacts.class);

                                        holder.name_Find_Friend.setText(contacts.getName());
                                        holder.about_Find_Friend.setText(contacts.getStatus());
                                        Picasso.get()
                                                .load(contacts.getImageUrl())
                                                .placeholder(R.drawable.default_pic_user)
                                                .error(R.drawable.default_pic_user)
                                                .into(holder.image_Find_Friend);

                                        holder.btn_Refuse.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                removeRequest(receiverID);
                                            }
                                        });

                                        holder.btn_Accept.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                acceptRequest(receiverID);
                                            }
                                        });

                                    }


                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                            }



                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }

            @NonNull
            @Override
            public RequestsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(getContext()).inflate(R.layout.request_friend_item, parent, false);
                return new RequestsViewHolder(view);
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();

    }

    private void removeRequest(final String receiverID) {

        requestRef.child(senderID).child(receiverID).removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {

                            requestRef.child(receiverID).child(senderID).removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {

                                            }
                                        }
                                    });

                        }
                    }
                });

    }

    private void acceptRequest(final String receiverID) {

        contactRef.child(senderID).child(receiverID).child("contact")
                .setValue("saved").addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            contactRef.child(receiverID).child(senderID).child("contact")
                                    .setValue("saved").addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                removeRequest(receiverID);
                                            }
                                        }
                                    });
                        }
                    }
                });

    }

}

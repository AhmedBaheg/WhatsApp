package com.example.whatsapp.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.whatsapp.Model.Contacts;
import com.example.whatsapp.R;
import com.example.whatsapp.Utils.Constants;
import com.example.whatsapp.ViewHolder.RequestsViewHolder;
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
public class RequestsFragment extends Fragment {

    private View view;
    private RecyclerView recyclerView;

    private FirebaseRecyclerOptions<Contacts> options;
    private FirebaseRecyclerAdapter<Contacts, RequestsViewHolder> adapter;

    private DatabaseReference requestRef, userRef;

    public RequestsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_requests, container, false);

        requestRef = FirebaseDatabase.getInstance().getReference("Requests").child(Constants.getUID());
        userRef = FirebaseDatabase.getInstance().getReference(Constants.USERS);

        recyclerView = view.findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        options = new FirebaseRecyclerOptions.Builder<Contacts>()
                .setQuery(requestRef, Contacts.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<Contacts, RequestsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final RequestsViewHolder holder, int position, @NonNull Contacts model) {

                final String requestsID = getRef(position).getKey();
                DatabaseReference typeRef = getRef(position).child("request_type").getRef();

                typeRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if (dataSnapshot.exists()) {

                            String type = dataSnapshot.getValue().toString();
                            if (type.equals("received")) {


                                userRef.child(requestsID).addValueEventListener(new ValueEventListener() {
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
}

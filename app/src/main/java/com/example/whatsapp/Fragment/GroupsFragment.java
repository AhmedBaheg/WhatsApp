package com.example.whatsapp.Fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.whatsapp.Activity.GroupChatActivity;
import com.example.whatsapp.R;
import com.example.whatsapp.Utils.Constants;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class GroupsFragment extends Fragment {

    private View view;
    private ArrayList<String> arrayList;
    private ArrayAdapter<String> arrayAdapter;
    private ListView list_View;

    public static String positionItem;
    public static String key;

    // Firebase
    DatabaseReference groupRef;

    public GroupsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_groups, container, false);

        groupRef = FirebaseDatabase.getInstance().getReference(Constants.GROUPS);

        initializeViews();

        RetrieveAndDisplayGroups();

        openGroupChat();

        return view;
    }


    private void initializeViews() {

        list_View = view.findViewById(R.id.list_view);

    }

    /**
     * This method {@link #RetrieveAndDisplayGroups()}
     * to retrieve the unique key groups
     * not the existing value in the key
     */

    private void RetrieveAndDisplayGroups() {

        arrayList = new ArrayList<>();

        groupRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                arrayList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    key = snapshot.getKey();
                    arrayList.add(key);
                }
                arrayAdapter = new ArrayAdapter<>(getContext(), R.layout.group_item, arrayList);
                list_View.setAdapter(arrayAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void openGroupChat() {

        list_View.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                positionItem = parent.getItemAtPosition(position).toString();

                Intent groupChat = new Intent(getContext(), GroupChatActivity.class);
//                groupChat.putExtra("groupName" , currentGroupName);
                startActivity(groupChat);
            }
        });

    }

}

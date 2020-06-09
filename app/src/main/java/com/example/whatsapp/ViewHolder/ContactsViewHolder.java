package com.example.whatsapp.ViewHolder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whatsapp.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class ContactsViewHolder extends RecyclerView.ViewHolder {

    public TextView name_Find_Friend , about_Find_Friend;
    public CircleImageView image_Find_Friend;

    public ContactsViewHolder(@NonNull View itemView) {
        super(itemView);

        name_Find_Friend = itemView.findViewById(R.id.name_find_friend);
        about_Find_Friend = itemView.findViewById(R.id.about_find_friend);
        image_Find_Friend = itemView.findViewById(R.id.image_find_friend);

    }
}

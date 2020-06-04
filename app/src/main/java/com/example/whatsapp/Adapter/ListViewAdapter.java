package com.example.whatsapp.Adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.whatsapp.Model.Group.Group;

import java.util.ArrayList;

public class ListViewAdapter extends ArrayAdapter<Group> {
//
//    private Context context;
//    private int resource;
//    public TextView group_Item;

    public ListViewAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Group> objects) {
        super(context, resource, objects);
//        this.context = context;
//        this.resource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

//        convertView = LayoutInflater.from(context).inflate(R.layout.group_item , parent , false);
//
//        group_Item = convertView.findViewById(R.id.group_item);
//        group_Item.setText(getItem(position).getGroup());
//
        return convertView;

    }
}

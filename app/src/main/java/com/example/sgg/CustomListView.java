package com.example.sgg;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomListView extends BaseAdapter {

    LayoutInflater layoutInflater= null;
    private ArrayList<Save_user> userDataView= null;
    private int count=0;

    public CustomListView(ArrayList<Save_user> userData) {
        userDataView= userData;
        count= userDataView.size();
    }

    @Override
    public int getCount() {
        return count;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int p, View view, ViewGroup viewGroup) {
        if (view == null){
            final Context context= viewGroup.getContext();
            if (layoutInflater == null)
                layoutInflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view= layoutInflater.inflate(R.layout.list_layout, viewGroup, false);
        }
        TextView name= view.findViewById(R.id.name);
        TextView company= view.findViewById(R.id.company);
        TextView position= view.findViewById(R.id.position);

        name.setText(userDataView.get(p).name);
        company.setText(userDataView.get(p).company);
        position.setText(userDataView.get(p).position);

        return view;
    }
}

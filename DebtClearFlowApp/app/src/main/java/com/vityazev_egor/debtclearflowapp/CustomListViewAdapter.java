package com.vityazev_egor.debtclearflowapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.vityazev_egor.debtclearflowapp.Models.ReceptionModel;

public class CustomListViewAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private ReceptionModel[] data;
    private Context context;

    public CustomListViewAdapter(Context context, ReceptionModel[] models){
        inflater = LayoutInflater.from(context);
        this.data = models;
        this.context = context;
    }

    @Override
    public int getCount() {
        return data.length;
    }

    @Override
    public Object getItem(int i) {
        return data[i];
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflater.inflate(R.layout.activity_custom_list_view, null);
        TextView receptionName = view.findViewById(R.id.reception_name);
        TextView recptionDate = view.findViewById(R.id.reception_date);
        Button button = view.findViewById(R.id.go_button);
        receptionName.setText(data[i].name);
        recptionDate.setText(data[i].time);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Shared shared = new Shared(context);
                shared.showSimpleAlert("Test", data[i].id.toString());
            }
        });
        return view;
    }
}

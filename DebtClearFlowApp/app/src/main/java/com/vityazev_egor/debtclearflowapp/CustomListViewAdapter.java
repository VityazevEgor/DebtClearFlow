package com.vityazev_egor.debtclearflowapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import java.util.*;

import com.vityazev_egor.debtclearflowapp.Models.ReceptionModel;

public class CustomListViewAdapter extends BaseAdapter {

    private final LayoutInflater inflater;
    private List<ReceptionModel> data;
    private final Context context;

    public CustomListViewAdapter(Context context, ReceptionModel[] models){
        inflater = LayoutInflater.from(context);
        this.data = new ArrayList<ReceptionModel>(Arrays.asList(models));
        this.context = context;
    }

    public void addModel(ReceptionModel model){
        data.add(model);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int i) {
        return data.get(i);
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
        receptionName.setText(data.get(i).name);
        recptionDate.setText(data.get(i).time);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Shared shared = new Shared(context);
                shared.showSimpleAlert("Test", data.get(i).id.toString());
            }
        });
        return view;
    }
}

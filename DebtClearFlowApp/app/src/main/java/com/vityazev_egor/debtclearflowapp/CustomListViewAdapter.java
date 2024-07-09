package com.vityazev_egor.debtclearflowapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import java.util.*;

import com.vityazev_egor.debtclearflowapp.Models.CustomListModel;

public class CustomListViewAdapter extends BaseAdapter {

    private final LayoutInflater inflater;
    private List<CustomListModel> data;
    private final Context context;

    public CustomListViewAdapter(Context context, CustomListModel[] models){
        inflater = LayoutInflater.from(context);
        this.data = new ArrayList<CustomListModel>(Arrays.asList(models));
        this.context = context;
    }

    public void addModel(CustomListModel model){
        data.add(model);
    }

    public CustomListModel findModelById(int modelId){
        return data.stream().filter(model -> model.id == modelId).findFirst().orElse(null);
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
        return data.get(i).id;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflater.inflate(R.layout.activity_custom_list_view, null);
        TextView receptionName = view.findViewById(R.id.reception_name);
        TextView receptionDate = view.findViewById(R.id.reception_date);
        Button button = view.findViewById(R.id.go_button);
        receptionName.setText(data.get(i).name);
        receptionDate.setText(data.get(i).time);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Shared shared = new Shared(context);
                //shared.showSimpleAlert("Test", data.get(i).id.toString());

                Intent intent = new Intent(context, RepaymentViewActivity.class);
                intent.putExtra("id", data.get(i).id); //для передачи id
                intent.putExtra("name", data.get(i).name);
                context.startActivity(intent);
            }
        });
        return view;
    }
}

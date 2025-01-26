package com.vityazev_egor.debtclearflowapp.CustomListViews;

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
import com.vityazev_egor.debtclearflowapp.QueueViewActivity;
import com.vityazev_egor.debtclearflowapp.R;

public class RetakesListViewAdapter extends BaseAdapter {

    private final LayoutInflater inflater;
    private final List<CustomListModel> data;
    private final Context context;

    public RetakesListViewAdapter(Context context, CustomListModel[] models){
        inflater = LayoutInflater.from(context);
        this.data = new ArrayList<>(Arrays.asList(models));
        this.context = context;
    }

    public void addModel(CustomListModel model){
        data.add(model);
    }

    public Optional<CustomListModel> findModelById(int modelId){
        return data.stream().filter(model -> model.getId() == modelId).findFirst();
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
        return data.get(i).getId();
    }

    public List<CustomListModel> getData() {
        return data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // View recycling
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.activity_retakes_list_view, parent, false);
        }

        // Find views
        TextView receptionName = convertView.findViewById(R.id.reception_name);
        TextView receptionDate = convertView.findViewById(R.id.reception_date);
        TextView receptionRoom = convertView.findViewById(R.id.reception_room);
        Button button = convertView.findViewById(R.id.go_button);

        // Set data
        CustomListModel currentRetake = data.get(position);
        receptionName.setText(currentRetake.getName());
        receptionDate.setText(currentRetake.getTime());
        receptionRoom.setText(currentRetake.getReceptionRoom());

        // Set click listener using final position variable
        final int finalPosition = position;
        button.setOnClickListener(v -> {
            Intent intent = new Intent(context, QueueViewActivity.class);
            intent.putExtra("id", data.get(finalPosition).getId());
            intent.putExtra("name", data.get(finalPosition).getName());
            context.startActivity(intent);
        });

        return convertView;
    }
}

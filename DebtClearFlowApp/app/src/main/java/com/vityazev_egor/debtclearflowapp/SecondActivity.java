package com.vityazev_egor.debtclearflowapp;

import android.os.Bundle;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.vityazev_egor.debtclearflowapp.Models.ReceptionModel;

public class SecondActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_second);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ReceptionModel[] data = new ReceptionModel[]{
                new ReceptionModel(1, "a", "a"),
                new ReceptionModel(2, "b", "b")
        };
        ListView listView = findViewById(R.id.listView);
        listView.setAdapter(new CustomListViewAdapter(this, data));
    }
}
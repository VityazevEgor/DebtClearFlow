package com.vityazev_egor.debtclearflowapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.vityazev_egor.debtclearflowapp.CustomListViews.RetakesListViewAdapter;
import com.vityazev_egor.debtclearflowapp.Models.DebtRepayment;
import com.vityazev_egor.debtclearflowapp.Models.CustomListModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MyRetakesActivity extends AppCompatActivity {

    private final String TAG = "MyRetakesActivity";
    private RetakesListViewAdapter adapter;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    @Override
    protected void onStop() {
        scheduler.shutdown();
        Log.w(TAG, "MyRetakesActivity is not active");
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

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
        setUpUI();
        startDataUpdates();
    }

    private void setUpUI(){
        adapter = new RetakesListViewAdapter(this, new CustomListModel[]{});
        ListView listView = findViewById(R.id.listView);
        listView.setAdapter(adapter);

        Button button = findViewById(R.id.btnLoginAsOtherUser);
        button.setOnClickListener(v -> logOut());
    }

    private void logOut(){
        Shared shared = new Shared(this);
        shared.clearData();
        Intent intent = new Intent(MyRetakesActivity.this, AuthActivity.class);
        startActivity(intent);
        finish();
    }

    private void startDataUpdates() {
        Shared shared = new Shared(this);
        String email = shared.getData("email");
        scheduler.scheduleWithFixedDelay(
                new ReceptionsGetter(email, Shared.serverUrl),
                0,
                5,
                TimeUnit.SECONDS
        );
    }

    public class ReceptionsGetter implements Runnable {
        private final Request request;
        private final APIModule api = new APIModule();

        public ReceptionsGetter(String email, String serverUrl) {
            Map<String, String> jsonBody = Map.of("email", email);
            this.request = api.buildJsonPostRequest(jsonBody, "findReceptions");
        }

        @Override
        public void run() {
            api.sendRequest(request, DebtRepayment[].class).ifPresent(receptionsArray ->{
                List<DebtRepayment> receptions = Arrays.asList(receptionsArray);
                addNewReceptions(receptions);
                removeOldReceptions(receptions);
                runOnUiThread(() -> adapter.notifyDataSetChanged());
            });
        }

        private void addNewReceptions(List<DebtRepayment> receptions){
            receptions.forEach(reception->{
                if (!adapter.findModelById(reception.getId()).isPresent()) {
                    adapter.addModel(new CustomListModel(
                            reception.getId(),
                            reception.getName(),
                            reception.getStarTime().toString(),
                            reception.getCloset()
                    ));
                }
            });
        }

        private void removeOldReceptions(List<DebtRepayment> receptions){
            List<CustomListModel> toRemove = new ArrayList<>();
            for (CustomListModel model : adapter.getData()){
                if (receptions.stream().noneMatch(reception-> Objects.equals(reception.getId(), model.getId()))){
                    toRemove.add(model);
                }
            }
            adapter.getData().removeAll(toRemove);
        }
    }
}
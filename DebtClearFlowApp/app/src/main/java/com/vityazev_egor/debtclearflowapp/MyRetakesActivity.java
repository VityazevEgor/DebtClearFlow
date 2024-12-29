package com.vityazev_egor.debtclearflowapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Button;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.os.HandlerCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.vityazev_egor.debtclearflowapp.Models.DebtRepayment;
import com.vityazev_egor.debtclearflowapp.Models.CustomListModel;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MyRetakesActivity extends AppCompatActivity {

    private final String TAG = "SecondActivity";
    private CustomListViewAdapter adapter;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    @Override
    protected void onStop() {
        scheduler.shutdown();
        Log.w(TAG, "SecondActivity is not active");
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
        adapter = new CustomListViewAdapter(this, new CustomListModel[]{});
        ListView listView = findViewById(R.id.listView);
        listView.setAdapter(adapter);

        Button button = findViewById(R.id.btnLoginAsOtherUser);
        button.setOnClickListener(v -> logOut());
    }

    private void logOut(){
        Shared shared = new Shared(this);
        shared.clearData();
        Intent intent = new Intent(MyRetakesActivity.this, MainActivity.class);
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
        private final OkHttpClient client = new OkHttpClient().newBuilder().build();
        private final Request request;
        private final ObjectMapper mapper = new ObjectMapper();

        public ReceptionsGetter(String email, String serverUrl) {
            Map<String, String> jsonBody = new HashMap<>();
            jsonBody.put("email", email);

            String jsonString;
            try {
                jsonString = mapper.writeValueAsString(jsonBody);
            } catch (JsonProcessingException e) {
                Log.e(TAG, "Error creating JSON", e);
                jsonString = "{}";
            }

            MediaType JSON = MediaType.get("application/json; charset=utf-8");
            RequestBody body = RequestBody.create(jsonString, JSON);

            this.request = new Request.Builder()
                    .url(serverUrl + "findReceptions")
                    .post(body)
                    .addHeader("Content-Type", "application/json")
                    .build();

            mapper.registerModule(new JavaTimeModule());
        }

        @Override
        public void run() {
            try {
                Response response = client.newCall(request).execute();
                assert response.body() != null;
                String responseString = response.body().string();
                Log.i(TAG, responseString);
                DebtRepayment[] receptions = mapper.readValue(responseString, DebtRepayment[].class);
                for (DebtRepayment reception : receptions) {
                    if (adapter.findModelById(reception.getId()) == null) {
                        adapter.addModel(new CustomListModel(
                                reception.getId(),
                                reception.getName(),
                                reception.getStarTime().toString(),
                                reception.getCloset()
                        ));
                    }
                }
                runOnUiThread(() -> adapter.notifyDataSetChanged());
            } catch (Exception e) {
                Log.e(TAG, "I could not get receptions", e);
            }
        }
    }
}
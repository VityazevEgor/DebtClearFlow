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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.vityazev_egor.debtclearflowapp.Models.DebtRepayment;
import com.vityazev_egor.debtclearflowapp.Models.CustomListModel;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SecondActivity extends AppCompatActivity {

    private final String TAG = "SecondActivity";
    private Handler handler;
    private CustomListViewAdapter adapter;

    private static Boolean isActive = false;

    @Override
    protected void onStop() {
        isActive = false;
        Log.w(TAG, "SecondActivity is not active");
        super.onStop();
    }

    @Override
    protected void onPause() {
        isActive = false;
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
        Shared shared = new Shared(this);
        String email = shared.getData("email");

        adapter = new CustomListViewAdapter(this, new CustomListModel[]{});
        ListView listView = findViewById(R.id.listView);
        listView.setAdapter(adapter);

        Button button = findViewById(R.id.btnLoginAsOtherUser);
        button.setOnClickListener(v -> {
            shared.clearData();
            Intent intent = new Intent(SecondActivity.this, MainActivity.class);
            startActivity(intent);
        });

        handler = HandlerCompat.createAsync(Looper.getMainLooper());
        new Thread(new ReceptionsGetter(email, Shared.serverUrl)).start();
        isActive = true;
    }

    public class ReceptionsGetter implements Runnable{
        private final OkHttpClient client = new OkHttpClient().newBuilder().build();

        private final Request request;
        private final ObjectMapper mapper = new ObjectMapper();

        public ReceptionsGetter(String email, String serverUrl){
            RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                    .addFormDataPart("email",email)
                    .build();
            this.request = new Request.Builder()
                    .url(serverUrl + "findReceptions")
                    .method("POST", body)
                    .build();
            mapper.registerModule(new JavaTimeModule());
        }

        @Override
        public void run() {
            while (isActive) {
                try {
                    Response response = client.newCall(request).execute();
                    assert response.body() != null;
                    String responseString = response.body().string();
                    Log.i(TAG, responseString);
                    DebtRepayment[] receptions = mapper.readValue(responseString, DebtRepayment[].class);
                    for (DebtRepayment reception : receptions){
                        if (adapter.findModelById(reception.getId()) == null) {
                            adapter.addModel(new CustomListModel(reception.getId(), reception.getName(), reception.getStarTime().toString()));
                        }
                    }
                    handler.post(() -> adapter.notifyDataSetChanged());
                } catch (Exception e) {
                    Log.e(TAG, "I could not get receptions", e);
                }

                try {
                    Thread.sleep(5000);
                } catch (InterruptedException ignored) {}
            }
        }
    }
}
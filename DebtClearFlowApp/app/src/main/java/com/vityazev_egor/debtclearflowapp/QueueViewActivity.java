package com.vityazev_egor.debtclearflowapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.os.HandlerCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class QueueViewActivity extends AppCompatActivity {

    private TextView queuePositionNumber;

    private final String TAG = "QueueViewActivity";
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    @Override
    protected void onPause() {
        scheduler.shutdown();
        super.onPause();
    }

    @Override
    protected void onStop(){
        scheduler.shutdown();
        super.onStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_repayment_view);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        queuePositionNumber = findViewById(R.id.queuePositionNumber);
        Toolbar toolbar = findViewById(R.id.toolbar);

        Button button = findViewById(R.id.backButton);
        button.setOnClickListener(v -> {
            Intent intent = new Intent(this, MyRetakesActivity.class);
            startActivity(intent);
        });

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            Integer repaymentId = bundle.getInt("id");
            String email = new Shared(this).getData("email");
            if (repaymentId == 0 || email == null) {
                Log.e(TAG, "Can't get repayment id from bundle or email is nulls");
                new Shared(this).showSimpleAlert("Error", "Что-то пошло не так при иницилизации формы");
                return;
            }

            queuePositionNumber.setText(String.valueOf(bundle.getInt("id")));
            toolbar.setTitle(bundle.getString("name"));
            scheduler.scheduleWithFixedDelay(
                    new PositionUpdater(email, repaymentId, Shared.serverUrl),
                    0,
                    4,
                    TimeUnit.SECONDS
            );
        }
    }

    // класс который получает информацию о текущем положении в очереди студента
    private class PositionUpdater implements Runnable {
        private final OkHttpClient client = new OkHttpClient().newBuilder().build();
        private final ObjectMapper mapper = new ObjectMapper();
        private final Request request;

        public PositionUpdater(String email, Integer repaymentId, String serverUrl) {
            Map<String, String> jsonBody = Map.of("email", email, "repaymentId", repaymentId.toString());
            this.request = Shared.buildJsonPostRequest(jsonBody, "findPosition");
        }
        private static class ResponseData{
            private Integer position;
            private String message;
            public ResponseData(){

            }
            public String getMessage() {
                return message;
            }

            public void setMessage(String message) {
                this.message = message;
            }

            public Integer getPosition() {
                return position;
            }

            public void setPosition(Integer position) {
                this.position = position;
            }
        }

        @Override
        public void run() {
            String textResponse;
            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful() || response.body() == null)
                    throw new Exception("Bad Request or body is null");
                textResponse = response.body().string();
                ResponseData data = mapper.readValue(textResponse, ResponseData.class);
                runOnUiThread(()->{
                    queuePositionNumber.setText(String.format("%d", data.getPosition()));
                });
            } catch (IOException e) {
                Log.e(TAG, "Can't send request to API", e);
            } catch (Exception e) {
                Log.e(TAG, Objects.requireNonNull(e.getMessage()));
            }
        }
    }


}
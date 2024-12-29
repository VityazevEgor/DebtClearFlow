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

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class QueueViewActivity extends AppCompatActivity {

    private Handler handler;
    private TextView queuePositionNumber;

    private Boolean isActive = true;
    private final String TAG = "RepaymentViewActivity";

    @Override
    protected void onPause() {
        isActive = false;
        super.onPause();
    }

    @Override
    protected void onStop(){
        isActive = false;
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
        handler = HandlerCompat.createAsync(Looper.getMainLooper());

        queuePositionNumber = findViewById(R.id.queuePositionNumber);
        Toolbar toolbar = findViewById(R.id.toolbar);

        Bundle bundle = getIntent().getExtras();


        Button button = findViewById(R.id.backButton);
        button.setOnClickListener(v -> {
            Intent intent = new Intent(this, MyRetakesActivity.class);
            startActivity(intent);
        });

        if (bundle != null) {
            Integer repaymentId = bundle.getInt("id");
            String email = new Shared(this).getData("email");
            if (repaymentId == null || email == null) {
                Log.e(TAG, "Can't get repayment id from bundle or email is nulls");
                new Shared(this).showSimpleAlert("Error", "Что-то пошло не так при иницилизации формы");
                return;
            }

            queuePositionNumber.setText(String.valueOf(bundle.getInt("id")));
            toolbar.setTitle(bundle.getString("name"));
            new Thread(new PositionUpdater(email, repaymentId, Shared.serverUrl)).start();
        }
    }

    // класс который получает информацию о текущем положении в очереди студента
    private class PositionUpdater implements Runnable {
        private final OkHttpClient client = new OkHttpClient().newBuilder().build();
        private final Request request;

        public PositionUpdater(String email, int repaymentId, String serverUrl) {
            RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                    .addFormDataPart("email", email)
                    .addFormDataPart("repaymentId", String.valueOf(repaymentId))
                    .build();

            request = new Request.Builder()
                    .url(serverUrl + "findPosition")
                    .method("POST", body)
                    .build();
        }

        @Override
        public void run() {
            while (isActive) {
                try{
                    Response response = client.newCall(request).execute();
                    if (!response.isSuccessful() || response.body() == null) throw new Exception("Bad Request or body is null");
                    String textResponse = response.body().string();
                    handler.post(() -> {
                        queuePositionNumber.setText(textResponse);
                    });
                    Thread.sleep(4000);
                } catch (Exception e) {
                    Log.e(TAG, "Can't get current position from server", e);
                }
            }
        }
    }


}
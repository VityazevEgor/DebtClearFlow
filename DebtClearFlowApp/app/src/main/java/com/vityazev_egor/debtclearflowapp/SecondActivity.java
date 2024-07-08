package com.vityazev_egor.debtclearflowapp;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.os.HandlerCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.vityazev_egor.debtclearflowapp.Models.ReceptionModel;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SecondActivity extends AppCompatActivity {

    private Shared shared;
    private String email;
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_second);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        shared = new Shared(this);
        email = shared.getData("email");

        ReceptionModel[] data = new ReceptionModel[]{
                new ReceptionModel(1, "a", "a"),
                new ReceptionModel(2, "b", "b")
        };
        adapter = new CustomListViewAdapter(this, data);
        ListView listView = findViewById(R.id.listView);
        listView.setAdapter(adapter);

        handler = HandlerCompat.createAsync(Looper.getMainLooper());
        new Thread(new ReceptionsGetter(email, shared.serverUrl)).start();
        isActive = true;
    }


    // TODO сделать десерилизацию ответа от сервера и добоавление новых объектов в ListView
    public class ReceptionsGetter implements Runnable{

        private final OkHttpClient client = new OkHttpClient().newBuilder().build();
        private final MediaType mediaType = MediaType.parse("text/plain");

        private final Request request;

        public ReceptionsGetter(String email, String serverUrl){
            RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                    .addFormDataPart("email",email)
                    .build();
            this.request = new Request.Builder()
                    .url(serverUrl + "findReceptions")
                    .method("POST", body)
                    .build();
        }

        @Override
        public void run() {
            while (isActive) {
                try {
                    Response response = client.newCall(request).execute();
                    Log.i(TAG, response.body().string());
                    adapter.addModel(new ReceptionModel(10, "t", "t"));
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

    private void getReceptions(){
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        MediaType mediaType = MediaType.parse("text/plain");
        RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("email",email)
                .build();
        Request request = new Request.Builder()
                .url(shared.serverUrl + "findReceptions")
                .method("POST", body)
                .build();
        try {
            Response response = client.newCall(request).execute();
            Log.d(TAG, response.body().string());
        } catch (IOException e) {
            Log.e(TAG, "I could not get receptions", e);
            shared.showSimpleAlert("Ошибка!","Не удалось получить данные от сервера");
        }
    }
}
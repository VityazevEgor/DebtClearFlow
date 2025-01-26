package com.vityazev_egor.debtclearflowapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.nfc.Tag;
import android.util.Log;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class APIModule {
    private final OkHttpClient client = new OkHttpClient().newBuilder().build();
    private final ObjectMapper mapper = new ObjectMapper();
    private final String baseUrl = "http://192.168.1.69:8080/api/";
    private final String baseImagesUrl = "http://192.168.1.69:8080/images/";
    private final String TAG = "APIModule";

    public APIModule(){
        mapper.registerModule(new JavaTimeModule());
    }

    public Request buildJsonPostRequest(Map<String, String> data, String apiPath){
        ObjectMapper mapper = new ObjectMapper();
        String jsonString;
        try {
            jsonString = mapper.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            Log.e(TAG, "Error creating JSON", e);
            jsonString = "{}";
        }
        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(jsonString, JSON);
        return new Request.Builder()
                .url(baseUrl + apiPath)
                .post(body)
                .addHeader("Content-Type", "application/json")
                .build();
    }

    public <T> Optional<T> sendRequest(Request request, Class<T> toConvert){
        try(Response response = client.newCall(request).execute()){
            if (!response.isSuccessful() || response.body() == null){
                Log.e(TAG, "Response code = " + response.code());
                return Optional.empty();
            }
            return Optional.of(mapper.readValue(response.body().string(), toConvert));
        }
        catch (IOException ex){
            Log.e(TAG, "Error during request", ex);
            return Optional.empty();
        }
    }

    public Optional<Bitmap> getImage(String imageName){
        Request request = new Request.Builder().url(baseImagesUrl + imageName).build();
        try (Response response = client.newCall(request).execute()){
            if (!response.isSuccessful() || response.body() == null){
                Log.e(TAG, "Response code = " + response.code());
                return Optional.empty();
            }
            return Optional.ofNullable(BitmapFactory.decodeStream(response.body().byteStream()));
        }
        catch (IOException e){
            Log.e(TAG, "Can't get image from server", e);
        }
        return Optional.empty();
    }

}

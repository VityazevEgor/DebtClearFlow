package com.vityazev_egor.debtclearflowapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.util.Log;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;

public class Shared {
    private final Context context;
    private final SharedPreferences sharedPreferences;
    public final static String serverUrl = "http://192.168.1.69:8080/api/";

    public Shared(Context context) {
        String APP_NAME = "DebtClearFlow";
        sharedPreferences = context.getSharedPreferences(APP_NAME, Context.MODE_PRIVATE);
        this.context = context;
    }

    public void saveData(String key, String data) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, data);
        editor.apply();
    }

    public void clearData(){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

    public String getData(String key) {
        return sharedPreferences.getString(key, "");
    }

    public void showSimpleAlert(String title, String message){
        new AlertDialog.Builder(context).setTitle(title).setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {}
                }).show();
    }

}

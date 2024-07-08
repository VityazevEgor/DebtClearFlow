package com.vityazev_egor.debtclearflowapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;

public class Shared {
    private final Context context;
    private final SharedPreferences sharedPreferences;
    public final String serverUrl = "http://192.168.1.65:8080/api/";

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

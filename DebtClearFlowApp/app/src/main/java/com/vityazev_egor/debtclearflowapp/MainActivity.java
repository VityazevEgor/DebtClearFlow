package com.vityazev_egor.debtclearflowapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private final String TAG = "MainActivity";
    private Shared shared;

    private EditText input;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        shared = new Shared(this);
        shared.clearData();

        // поиск кнопок и прочее
        Button button = findViewById(R.id.authbutton);
        input = findViewById(R.id.emailinput);
        button.setOnClickListener(new AuthButtonClick());

        //shared.clearData();
        if (!shared.getData("email").isEmpty()){
            Log.i(TAG, "Found saved email. Changing form");
            loadSecondForm();
        }
        else{
            Log.w(TAG, "Didn't found email");
        }
    }

    // класс который обрабатывает событие клика на кнопку
    private class AuthButtonClick implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            // сохраняем информацию о почте
            String inputText = input.getText().toString();
            Log.i(TAG, "User enetered this text - " + inputText);
            if (!inputText.isEmpty() && checkEmail(inputText)) {
                shared.saveData("email", inputText);
                loadSecondForm();
            }
            else{
                shared.showSimpleAlert("Ошибка", "Формат электронной почты не верен!");
            }
        }
    }

    private Boolean checkEmail(String text){
        if (!text.contains("@")){
            return  false;
        }
        return text.substring(text.indexOf("@")).contains(".");
    }

    private void loadSecondForm(){
        Intent intent = new Intent(MainActivity.this, SecondActivity.class);
        startActivity(intent);
    }

}
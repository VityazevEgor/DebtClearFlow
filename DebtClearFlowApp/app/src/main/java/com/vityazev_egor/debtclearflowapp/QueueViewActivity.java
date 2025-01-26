package com.vityazev_egor.debtclearflowapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.vityazev_egor.debtclearflowapp.Models.QStudent;
import com.vityazev_egor.debtclearflowapp.Models.Teacher;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import okhttp3.Request;
import okhttp3.Response;

public class QueueViewActivity extends AppCompatActivity {

    private TextView queuePositionNumber;
    private TextView queuePositionLabel;
    private ImageView teacherPhoto;
    private TextInputEditText teacherNameField;
    private MaterialCardView queueCard;
    private MaterialCardView teacherCard;
    private MaterialCardView successCard;
    private MaterialCardView failureCard;

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
        queuePositionLabel = findViewById(R.id.queuePositionLabel);
        teacherPhoto = findViewById(R.id.teacherPhoto);
        teacherNameField = findViewById(R.id.teacherNameField);
        queueCard = findViewById(R.id.queueCard);
        teacherCard = findViewById(R.id.teacherCard);
        successCard = findViewById(R.id.successCard);
        failureCard = findViewById(R.id.failureCard);
        MaterialToolbar toolbar = findViewById(R.id.toolbar);

        MaterialButton button = findViewById(R.id.backButton);
        button.setOnClickListener(v -> {
            Intent intent = new Intent(this, MyRetakesActivity.class);
            startActivity(intent);
        });

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            Integer repaymentId = bundle.getInt("id");
            String email = new Shared(this).getData("email");
            if (repaymentId == 0 || email == null) {
                Log.e(TAG, "Can't get repayment id from bundle or email is null");
                new Shared(this).showSimpleAlert("Error", "Что-то пошло не так при инициализации формы");
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

    private class PositionUpdater implements Runnable {
        private final APIModule apiModule = new APIModule();
        private final Request getPositionRequest, getQstudentRequest, getTeacherInfoRequest;

        private static class PositionData {
            private Integer position;
            public Integer getPosition() {
                return position;
            }
            public void setPosition(Integer position) {
                this.position = position;
            }
        }

        public PositionUpdater(String email, Integer repaymentId, String serverUrl) {
            Map<String, String> jsonBody = Map.of("email", email, "repaymentId", repaymentId.toString());
            this.getPositionRequest = apiModule.buildJsonPostRequest(jsonBody, "findPosition");
            this.getQstudentRequest = apiModule.buildJsonPostRequest(jsonBody, "getQStudentInfo");
            this.getTeacherInfoRequest = apiModule.buildJsonPostRequest(jsonBody, "getTeacherInfo");
        }

        @Override
        public void run() {
            try {
                PositionData posData = apiModule.sendRequest(getPositionRequest, PositionData.class).orElseThrow(() -> new Exception("Can't get information about current pos"));
                if (posData.getPosition() > 0){
                    runOnUiThread(() -> {
                        queuePositionNumber.setText(String.format("%d", posData.getPosition()));
                        queueCard.setVisibility(View.VISIBLE);
                        teacherCard.setVisibility(View.GONE);
                        queuePositionLabel.setText("Ваша позиция в очереди");
                    });
                    return;
                }
                QStudent qStudent = apiModule.sendRequest(getQstudentRequest, QStudent.class).orElseThrow(() -> new Exception("Can't get qStudent object"));
                Teacher teacher = apiModule.sendRequest(getTeacherInfoRequest, Teacher.class).orElseThrow(() -> new Exception("Can't get information about teacher"));
                Bitmap teacherImage = apiModule.getImage(teacher.getImageName()).orElseThrow(() -> new Exception("Can't get teacher avatar"));

                if (qStudent.getInProcess()){
                    runOnUiThread(() -> {
                        hideMainCards();
                        teacherCard.setVisibility(View.VISIBLE);
                        teacherNameField.setText(teacher.getFullname());
                        teacherPhoto.setImageBitmap(teacherImage);
                    });
                    return;
                }
                if (qStudent.getAccepted()){
                    runOnUiThread(() -> {
                        hideMainCards();
                        successCard.setVisibility(View.VISIBLE);
                    });
                    return;
                }
                if (qStudent.getRejected()){
                    runOnUiThread(() -> {
                        hideMainCards();
                        failureCard.setVisibility(View.VISIBLE);
                    });
                }

            }
            catch (Exception e){
                Log.e(TAG, "Can't update information about position: " + e.getMessage());
            }
        }

        private void hideMainCards(){
            queueCard.setVisibility(View.GONE);
            teacherCard.setVisibility(View.GONE);
            successCard.setVisibility(View.GONE);
            failureCard.setVisibility(View.GONE);
        }
    }
}
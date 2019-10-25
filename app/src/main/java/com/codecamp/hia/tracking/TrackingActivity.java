package com.codecamp.hia.tracking;

import androidx.appcompat.app.AppCompatActivity;
import ticker.views.com.ticker.widgets.circular.timer.callbacks.CircularViewCallback;
import ticker.views.com.ticker.widgets.circular.timer.view.CircularView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.codecamp.hia.tracking.Services.UpdateStatusService;

import com.google.firebase.firestore.DocumentReference;

public class TrackingActivity extends AppCompatActivity {

    private String documentReference;
    CircularView circularViewWithTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking);
        circularViewWithTime = findViewById(R.id.circular_view_with_timer);



        CircularView.OptionsBuilder builderWithTimer =
                new CircularView.OptionsBuilder()
                        .shouldDisplayText(true)
                        .setCounterInSeconds(200)
                        .setCircularViewCallback(new CircularViewCallback() {
                            @Override
                            public void onTimerFinish() {

                                // Will be called if times up of countdown timer
                                Toast.makeText(TrackingActivity.this, "The Customer has arrived ", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onTimerCancelled() {

                                // Will be called if stopTimer is called
                                Toast.makeText(TrackingActivity.this, "CircularCallback: Timer Cancelled ", Toast.LENGTH_SHORT).show();
                            }
                        });
        circularViewWithTime.setOptions(builderWithTimer);
        circularViewWithTime.startTimer();

    }
}

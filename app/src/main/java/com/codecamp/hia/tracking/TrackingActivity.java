package com.codecamp.hia.tracking;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.codecamp.hia.tracking.Services.UpdateStatusService;

import com.google.firebase.firestore.DocumentReference;

public class TrackingActivity extends AppCompatActivity {
    public static final String DOCUMENT_REF = "documentRef";
    private String documentReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking);
        Intent intent = new Intent(this, UpdateStatusService.class);
        startService(intent);
    }
}

package com.codecamp.hia.tracking.Services;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Collection;

import androidx.annotation.Nullable;

public class UpdateStatusService extends Service {
    private FirebaseFirestore mDatabase = FirebaseFirestore.getInstance();
    private Collection progressCollection;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Bundle happyBundle = intent.getExtras();
        String id;
        return START_STICKY;

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}

package com.codecamp.hia.tracking.Services;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;

import com.codecamp.hia.tracking.TrackingActivity;
import com.codecamp.hia.tracking.models.Request;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Collection;

import androidx.annotation.Nullable;

public class UpdateStatusService extends Service {
    private FirebaseFirestore mDatabase = FirebaseFirestore.getInstance();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Bundle happyBundle = intent.getExtras();
        if (happyBundle != null) {
            String id = happyBundle.getString(TrackingActivity.DOCUMENT_REF, "NULL");
            if(!id.equals("NULL")) {
                CollectionReference progressReference = mDatabase.collection(Request.REQUEST_COLLECTION_NAME)
                        .document(id).collection(Request.PROGRESS_COLLECTION_NAME);
                progressReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        
                    }
                });


            }
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}

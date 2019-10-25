package com.codecamp.hia.tracking.Services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.codecamp.hia.tracking.MainActivity;
import com.codecamp.hia.tracking.R;
import com.codecamp.hia.tracking.TrackingActivity;
import com.codecamp.hia.tracking.models.Request;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collection;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class UpdateStatusService extends Service {
    private static final String ERROR_SERVICE_MSG = "error";
    private static final String CHANNEL_ID = "HIA";
    public static final String TAG = "UpdateStatusService";
    private FirebaseFirestore mDatabase = FirebaseFirestore.getInstance();
    Intent intent;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        this.intent = intent;

        final NotificationManager  notificationManager = createNotificationChannel();
        Log.d(TAG, "onStartCommand: Service started");
        Bundle happyBundle = intent.getExtras();
        if (happyBundle != null) {
            String id = happyBundle.getString(MainActivity.DOCUMENT_REF, "NULL");
            Log.d(TAG, "onStartCommand: ID: "+id);
            if (!id.equals("NULL")) {
                final CollectionReference progressReference = mDatabase.collection(Request.REQUEST_COLLECTION_NAME)
                        .document(id).collection(Request.PROGRESS_COLLECTION_NAME);
                Log.d(TAG, "onStartCommand: referance: "+progressReference);
                progressReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable final FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.wtf(ERROR_SERVICE_MSG, e.getMessage());
                        } else {
                            Log.d(TAG, "onEvent: CHANGED");
                            progressReference.orderBy(Request.STATUS_FIELD, Query.Direction.DESCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    try {
                                        Log.d(TAG, "onComplete: size"+task.getResult().getDocuments().size());
                                        if(task.getResult().getDocuments().size()>0) {
                                        DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);

                                            Log.d(TAG, "onComplete: error cause of :"+documentSnapshot.get(Request.STATUS_FIELD));
                                            int progressStatus = Integer.parseInt( documentSnapshot.get(Request.STATUS_FIELD).toString());
                                            Timestamp timestamp = documentSnapshot.getTimestamp(Request.TIMESTAMP_FIELD);
                                            if (progressStatus == 0) {
                                                Log.d(TAG, "onComplete: Start tracking activity");
                                                //TODO - should start trackingActivity if app is open, otherwise should save it in shared preference
                                            }
                                            createNotification(progressStatus, notificationManager);
                                        }

                                    } catch (NumberFormatException | NullPointerException e1) {
                                        Log.wtf(ERROR_SERVICE_MSG, e1);
                                    }
                                }

                            });

                        }
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

    private void createNotification(int code, NotificationManager notificationManager) {
        String notificationMSG = "";
        switch (code) {
            case 1:
                notificationMSG = "plane landed";
                break;
            case 3:
                notificationMSG = "bags collected"; //todo add and modify so that the switch matches all the cases
        }
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(UpdateStatusService.this, CHANNEL_ID);
        notificationBuilder.setContentTitle("Notification"); //todo change this to a suitable title
        notificationBuilder.setContentText(notificationMSG);
        notificationBuilder.setSmallIcon(R.drawable.test_not);

        notificationBuilder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        notificationManager.notify(654, notificationBuilder.build());
    }

    private NotificationManager createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "HIAChannelName";
            String description = "HIAChannelDescription";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
            return notificationManager;
        }
        return null;
    }
}

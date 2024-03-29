package com.codecamp.hia.tracking.Services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
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
import java.util.Random;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import static android.app.Notification.DEFAULT_SOUND;
import static android.app.Notification.DEFAULT_VIBRATE;

public class UpdateStatusService extends Service {
    private static final String ERROR_SERVICE_MSG = "error";
    private static final String CHANNEL_ID = "HIA";
    private static NotificationManager notificationManager;
    public static final String TAG = "UpdateStatusService";
    public static final String TIME = "time";
    public static final String NOTIFICATION_MSG = "notificationMSG";
    private FirebaseFirestore mDatabase = FirebaseFirestore.getInstance();
    public static boolean isRunning = false;
    Intent intent;
    private TrackingActivity trackingActivity;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    long[] vibrations = {250, 250};


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        isRunning = true;
        Intent intentNotifcation = new Intent(this, TrackingActivity.class);
        intentNotifcation.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        final PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intentNotifcation, 0);
        this.intent = intent;
        if(notificationManager ==null) {
            notificationManager = createNotificationChannel();
        }
        Log.d(TAG, "onStartCommand: Service started");
        Bundle happyBundle = intent.getExtras();
        if (happyBundle != null) {
            String id = happyBundle.getString(MainActivity.DOCUMENT_REF, "NULL");
            Log.d(TAG, "onStartCommand: ID: " + id);
            if (!id.equals("NULL")) {
                final CollectionReference progressReference = mDatabase.collection(Request.REQUEST_COLLECTION_NAME)
                        .document(id).collection(Request.PROGRESS_COLLECTION_NAME);
                Log.d(TAG, "onStartCommand: referance: " + progressReference);
                progressReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable final FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.wtf(ERROR_SERVICE_MSG, e.getMessage());
                        } else {
                            Log.d(TAG, "onEvent: CHANGED");
                            progressReference
                                    .orderBy(Request.STATUS_FIELD, Query.Direction.DESCENDING)
                                    .limit(1)
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            try {
                                                Log.d(TAG, "onComplete: size" + task.getResult().getDocuments().size());
                                                if (task.getResult().getDocuments().size() > 0) {
                                                    DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);

                                                    Log.d(TAG, "onComplete: error cause of :" + documentSnapshot.get(Request.STATUS_FIELD));
                                                    int progressStatus = Integer.parseInt(documentSnapshot.get(Request.STATUS_FIELD).toString());
                                                    Timestamp timestamp = documentSnapshot.getTimestamp(Request.TIMESTAMP_FIELD);
                                                    if (progressStatus == 0) {
                                                        Log.d(TAG, "onComplete: Start tracking activity");
                                                        //TODO - should update tracking activity
                                                    }
                                                    createNotification(progressStatus, notificationManager, pendingIntent);
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

    private void createNotification(int code, NotificationManager notificationManager, PendingIntent pendingIntent) {
        String notificationMSG = "";
        int timeInSeconds = 3600;
        switch (code) {
            case 0:
                notificationMSG = "Request Approved ";
                setUI(notificationMSG, getEstimate(timeInSeconds));
                break;
            case 1:
                notificationMSG = "plane landed";
                timeInSeconds = 2000;
                setUI(notificationMSG, getEstimate(timeInSeconds));
                break;
            case 2:
                timeInSeconds = 1500;
                notificationMSG = "Passport control completed";
                setUI(notificationMSG, timeInSeconds);
                break;
            case 3:

                notificationMSG = "Bags loaded on seat belt"; //todo add and modify so that the switch matches all the cases
                setUI(notificationMSG, getEstimate(1000));
                break;
            case 4:
                notificationMSG = "Passenger on way to exit"; //todo add and modify so that the switch matches all the cases
                setUI(notificationMSG, getEstimate(120));
        }
        Bitmap bigPicutre = getBitmap(ContextCompat.getDrawable(getApplicationContext(), R.mipmap.ic_background_air));

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                .setContentTitle("HIA Arrivals Notification")
                .setSmallIcon(R.drawable.test_not)
                .setOnlyAlertOnce(false)
                .setContentText(notificationMSG)
                .setVibrate(vibrations)
                .setContentIntent(pendingIntent)
                .setFullScreenIntent(pendingIntent, true)
                .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
                .setDefaults(Notification.DEFAULT_ALL)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(false);

//                .setStyle(new NotificationCompat.BigTextStyle());

        notificationManager.notify(new Random().nextInt(500)+100, notificationBuilder.build());
        Log.wtf(ERROR_SERVICE_MSG, "notified");
    }
    //        notificationBuilder.setLargeIcon(bigPicutre);
//        notificationBuilder; //todo change this to a suitable title

//
//        notificationBuilder.setVibrate(vibrations);
//
////        Bitmap icon = BitmapFactory.decodeResource(this.getResources(),
////                R.drawable.test_not);
//        notificationBuilder.setContentIntent(pendingIntent);
//        notificationBuilder.setFullScreenIntent(pendingIntent, true);
//        notificationBuilder.setDefaults(Notification.DEFAULT_ALL);
//        notificationBuilder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
////        notificationBuilder.setStyle(new NotificationCompat.BigPictureStyle().bigPicture(bigPicutre));
//        notificationBuilder.setAutoCancel(true);

    private void setUI(String notificationMSG, int randomNumber) {
        Intent intent = new Intent();
        intent.setAction("com.codecamp.hia.tracking.STATUS_CHANGED");
        intent.putExtra(TIME,randomNumber);
        intent.putExtra(NOTIFICATION_MSG,notificationMSG);
        sendBroadcast(intent);
        Log.wtf(TAG,"broadcast sent");
    }

    private NotificationManager createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "HIAChannelName";
            String description = "HIAChannelDescription";
            int importance = NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setShowBadge(true);
            channel.setVibrationPattern(vibrations);
//            channel.setBypassDnd(true);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);

            notificationManager.createNotificationChannel(channel);
            return notificationManager;
        }
        return null;
    }

    private int getEstimate(int timeInSeconds) {
        return new Random().nextInt(timeInSeconds + 100) + timeInSeconds - 100;

    }

    private Bitmap getBitmap(Drawable vectorDrawable) {
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        vectorDrawable.draw(canvas);
        return bitmap;
    }
}

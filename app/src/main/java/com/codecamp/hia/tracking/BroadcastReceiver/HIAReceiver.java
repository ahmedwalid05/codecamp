package com.codecamp.hia.tracking.BroadcastReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.codecamp.hia.tracking.Services.UpdateStatusService;
import com.codecamp.hia.tracking.TrackingActivity;

public class HIAReceiver extends BroadcastReceiver {

    public static final String TAG = "HIAReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.wtf(TAG, "received");
        TrackingActivity trackingActivity = (TrackingActivity) context;
        String notificationMSG = intent.getStringExtra(UpdateStatusService.NOTIFICATION_MSG);
        int time = intent.getIntExtra(UpdateStatusService.TIME, -1);
        if (time != -1) {
            trackingActivity.setStatusUpdate(notificationMSG, time);
        }


    }
}

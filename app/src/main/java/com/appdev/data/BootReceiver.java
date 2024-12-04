package com.appdev.data;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver {
    private static final String TAG = "DEBUG_BootReciver";
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            Log.d(TAG,"Boot recived");
            DataTrack dataTrack = new DataTrack(context);
            dataTrack.handleReboot(); // Handle data on reboot
        }
    }
}

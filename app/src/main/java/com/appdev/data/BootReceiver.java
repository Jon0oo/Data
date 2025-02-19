package com.appdev.data;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver {
    private static final String TAG = "DEBUG_BootReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            Log.d(TAG, "Boot completed broadcast received.");
            DataTrack dataTrack = new DataTrack(context);
            dataTrack.handleReboot(); // Incorporate data usage from before reboot into monthly offset
        }
    }
}

package com.appdev.data;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            DataTrack dataTrack = new DataTrack(context);
            dataTrack.handleReboot(); // Handle data on reboot
        }
    }
}

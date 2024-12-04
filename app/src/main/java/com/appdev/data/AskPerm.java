package com.appdev.data;

import android.app.Activity;
import android.app.AppOpsManager;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class AskPerm {
    private static final String TAG = "DEBUG_AskPerm";
    private Context context;
    private PermissionDialogListener listener;

    public AskPerm(Context context) {
        this.context = context;
    }

    public boolean checkPermission() {
        return isUsageStatsPermissionGranted() && isBatteryOptimizationIgnored();
    }


    private boolean isUsageStatsPermissionGranted() {
        AppOpsManager appOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                android.os.Process.myUid(), context.getPackageName());
        return mode == AppOpsManager.MODE_ALLOWED;
    }

    private boolean isBatteryOptimizationIgnored() {
        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Log.d(TAG, String.valueOf(powerManager.isIgnoringBatteryOptimizations(context.getPackageName())));
            return powerManager.isIgnoringBatteryOptimizations(context.getPackageName());
        }
        return true; // Battery optimization check isn't required for versions below Android M
    }

    public void showCustomDialog(PermissionDialogListener listener) {
        this.listener = listener;

        // Check if both permissions are granted
        if (!isUsageStatsPermissionGranted()) {
            showUsageStatsDialog(); // Prompt for data usage permission
        } else if (!isBatteryOptimizationIgnored()) {
            showBatteryOptimizationDialog(); // Prompt to disable battery optimization
        } else {
            if (listener != null) listener.onPermissionGranted(); // Both permissions are granted
        }
    }


    private void showUsageStatsDialog() {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.custom_permission_dialog);
        dialog.setCancelable(false);

        TextView messageTextView = dialog.findViewById(R.id.dialog_message);
        Button goToSettingsButton = dialog.findViewById(R.id.btn_go_to_settings);
        Button declineButton = dialog.findViewById(R.id.btn_decline);

        goToSettingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                context.startActivity(intent);
                dialog.dismiss();
                if (listener != null) listener.onPermissionDenied(); // Notify listener
            }
        });

        declineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Die App funktioniert ohne diese Berechtigung nicht.", Toast.LENGTH_SHORT).show();
                dialog.dismiss();

                // Close the app
                if (context instanceof Activity) {
                    ((Activity) context).finish(); // Finish the current activity
                }
            }
        });

        dialog.show();
    }

    private void showBatteryOptimizationDialog() {
        Dialog dialog2 = new Dialog(context);
        dialog2.setContentView(R.layout.custom_battery_optimisation_dialog);
        dialog2.setCancelable(false);

        TextView messageTextView = dialog2.findViewById(R.id.dialog_message);


        Button goToSettingsButton2 = dialog2.findViewById(R.id.btn_go_to_settingsB);
        Button declineButton2 = dialog2.findViewById(R.id.btn_declineB);

        goToSettingsButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
                context.startActivity(intent);
                dialog2.dismiss();
                if (listener != null) listener.onPermissionDenied(); // Notify listener
            }
        });

        declineButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Die App funktioniert ohne diese Berechtigung nicht.", Toast.LENGTH_SHORT).show();
                dialog2.dismiss();

                // Close the app
                if (context instanceof Activity) {
                    ((Activity) context).finish(); // Finish the current activity
                }
            }
        });

        dialog2.show();
    }

    public interface PermissionDialogListener {
        void onPermissionGranted();
        void onPermissionDenied();
    }
}

package com.appdev.data;

import android.app.Activity;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.app.Dialog;
import android.widget.TextView;
import android.widget.Toast;

public class AskPerm {

    private Context context;
    private PermissionDialogListener listener;

    public AskPerm(Context context) {
        this.context = context;
    }

    public boolean checkPermission() {
        return isUsageStatsPermissionGranted();
    }

    private boolean isUsageStatsPermissionGranted() {
        AppOpsManager appOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                android.os.Process.myUid(), context.getPackageName());
        return mode == AppOpsManager.MODE_ALLOWED;
    }

    public void showCustomDialog(PermissionDialogListener listener) {
        this.listener = listener; // Save the listener
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

    public interface PermissionDialogListener {
        void onPermissionGranted();
        void onPermissionDenied();
    }
}
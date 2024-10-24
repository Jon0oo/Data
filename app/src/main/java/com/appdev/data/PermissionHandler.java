package com.appdev.data;

import android.app.Activity;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class PermissionHandler {
    private static final int REQUEST_CODE_READ_PHONE_STATE = 1002;
    private static final int REQUEST_CODE_NOTIFICATION = 1003;
    private static final String TAG = "PermissionHandler";

    private Context context;
    private boolean isFirstPermissionHandled = false;

    public PermissionHandler(Context context) {
        this.context = context;
    }

    public void requestPermissionsSequentially() {
        requestPhoneStatePermission();
    }

    private void requestPhoneStatePermission() {
        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) context,
                    new String[]{android.Manifest.permission.READ_PHONE_STATE},
                    REQUEST_CODE_READ_PHONE_STATE);
        } else {
            isFirstPermissionHandled = true;
            requestNotificationPermission(); // Request next permission if already granted
        }
    }

    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Check for Android 13 or higher
            if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions((Activity) context,
                        new String[]{android.Manifest.permission.POST_NOTIFICATIONS},
                        REQUEST_CODE_NOTIFICATION);
            }
        }
    }

    public boolean isPermissionGranted(String permission) {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
    }

    private boolean hasUsageStatsPermission() {
        AppOpsManager appOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, android.os.Process.myUid(), context.getPackageName());
        return mode == AppOpsManager.MODE_ALLOWED;
    }

    public void handlePermissionResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_READ_PHONE_STATE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    isFirstPermissionHandled = true; // First permission granted
                    requestNotificationPermission(); // Now request the notification permission
                } else {
                    Toast.makeText(context, "Please allow this permission, else the app won't function", Toast.LENGTH_SHORT).show();
                    // Optionally, inform the user why the permission is important
                }
                break;

            case REQUEST_CODE_NOTIFICATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted
                } else {
                    Toast.makeText(context, "Notification permission denied", Toast.LENGTH_SHORT).show();
                    // Optionally, inform the user why the permission is important
                }
                break;

            // Handle other permission results as needed
        }
    }
}

package com.example.productiveapp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "ProductiveApp";
    private TextView statusTextView;

    private final ActivityResultLauncher<String[]> requestPermissionsLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
                boolean allGranted = true;
                for (Map.Entry<String, Boolean> entry : result.entrySet()) {
                    if (!entry.getValue()) {
                        allGranted = false;
                        Log.w(TAG, "Permission denied: " + entry.getKey());
                    }
                }
                if (allGranted) {
                    Toast.makeText(this, "All permissions granted!", Toast.LENGTH_SHORT).show();
                    checkNotificationListenerPermission();
                } else {
                    Toast.makeText(this, "Some permissions were denied. App functionality may be limited.", Toast.LENGTH_LONG).show();
                    updateStatus("Permissions partially granted or denied.");
                }
                updatePermissionStatus();
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        statusTextView = findViewById(R.id.statusTextView);

        requestAppPermissions();
        updatePermissionStatus();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updatePermissionStatus();
    }

    private void requestAppPermissions() {
        List<String> permissionsToRequest = new ArrayList<>();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(Manifest.permission.RECEIVE_SMS);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(Manifest.permission.READ_SMS);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(Manifest.permission.READ_CALL_LOG);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }

        if (!permissionsToRequest.isEmpty()) {
            requestPermissionsLauncher.launch(permissionsToRequest.toArray(new String[0]));
        } else {
            Toast.makeText(this, "All runtime permissions already granted.", Toast.LENGTH_SHORT).show();
            checkNotificationListenerPermission();
        }
    }

    private void checkNotificationListenerPermission() {
        if (!isNotificationServiceEnabled()) {
            Toast.makeText(this, "Please enable Notification Access for " + getString(R.string.app_name), Toast.LENGTH_LONG).show();
            updateStatus("Notification Listener: DISABLED (Tap to enable)");
            statusTextView.setOnClickListener(v -> openNotificationListenSettings());
        } else {
            updateStatus("Notification Listener: ENABLED");
            statusTextView.setOnClickListener(null); // Remove listener if enabled
        }
    }

    private boolean isNotificationServiceEnabled() {
        String packageName = getPackageName();
        for (String enabledPackage : NotificationManagerCompat.getEnabledListenerPackages(this)) {
            if (enabledPackage.equals(packageName)) {
                return true;
            }
        }
        return false;
    }

    private void openNotificationListenSettings() {
        try {
            Intent intent = new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS);
            startActivity(intent);
        } catch (Exception e) {
            Log.e(TAG, "Failed to open notification listener settings: " + e.getMessage());
            Toast.makeText(this, "Could not open notification listener settings. Please find it manually.", Toast.LENGTH_LONG).show();
        }
    }

    private void updatePermissionStatus() {
        StringBuilder status = new StringBuilder();
        status.append("Runtime Permissions:\n");
        status.append("SMS Receive: ").append(checkPermission(Manifest.permission.RECEIVE_SMS) ? "GRANTED" : "DENIED").append("\n");
        status.append("SMS Read: ").append(checkPermission(Manifest.permission.READ_SMS) ? "GRANTED" : "DENIED").append("\n");
        status.append("Call Log Read: ").append(checkPermission(Manifest.permission.READ_CALL_LOG) ? "GRANTED" : "DENIED").append("\n");
        status.append("Phone State Read: ").append(checkPermission(Manifest.permission.READ_PHONE_STATE) ? "GRANTED" : "DENIED").append("\n");
        status.append("Location Fine: ").append(checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) ? "GRANTED" : "DENIED").append("\n");
        status.append("Location Coarse: ").append(checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION) ? "GRANTED" : "DENIED").append("\n");
        status.append("\n");
        status.append("Notification Listener: ").append(isNotificationServiceEnabled() ? "ENABLED" : "DISABLED").append("\n");

        updateStatus(status.toString());
    }

    private boolean checkPermission(String permission) {
        return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED;
    }

    private void updateStatus(String message) {
        runOnUiThread(() -> statusTextView.setText(message));
    }
}
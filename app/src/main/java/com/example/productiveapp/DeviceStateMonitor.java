package com.example.productiveapp;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import androidx.core.content.ContextCompat;

public class DeviceStateMonitor {

    private static final String TAG = "DeviceStateMonitor";
    private Context context;

    public DeviceStateMonitor(Context context) {
        this.context = context;
    }

    public void logPhoneState() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            Log.e(TAG, "READ_PHONE_STATE permission not granted.");
            return;
        }

        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (telephonyManager == null) {
            Log.e(TAG, "TelephonyManager not available.");
            return;
        }

        String deviceId = telephonyManager.getDeviceId(); // IMEI for GSM, MEID for CDMA
        String subscriberId = telephonyManager.getSubscriberId(); // IMSI
        String simSerialNumber = telephonyManager.getSimSerialNumber();
        String networkOperatorName = telephonyManager.getNetworkOperatorName();
        String networkCountryIso = telephonyManager.getNetworkCountryIso();
        String simOperatorName = telephonyManager.getSimOperatorName();
        int phoneType = telephonyManager.getPhoneType(); // e.g., GSM, CDMA
        int dataState = telephonyManager.getDataState(); // e.g., connected, disconnected
        int callState = telephonyManager.getCallState(); // e.g., idle, ringing, off-hook

        String phoneTypeString = getPhoneTypeString(phoneType);
        String dataStateString = getDataStateString(dataState);
        String callStateString = getCallStateString(callState);

        Log.d(TAG, "Device State Info:");
        Log.d(TAG, "  Device ID (IMEI/MEID): " + deviceId);
        Log.d(TAG, "  Subscriber ID (IMSI): " + subscriberId);
        Log.d(TAG, "  SIM Serial Number: " + simSerialNumber);
        Log.d(TAG, "  Network Operator: " + networkOperatorName);
        Log.d(TAG, "  Network Country ISO: " + networkCountryIso);
        Log.d(TAG, "  SIM Operator: " + simOperatorName);
        Log.d(TAG, "  Phone Type: " + phoneTypeString);
        Log.d(TAG, "  Data State: " + dataStateString);
        Log.d(TAG, "  Call State: " + callStateString);

        // TODO: Send this data to your backend
        ApiClient.sendDeviceState(context, deviceId, subscriberId, simSerialNumber,
                networkOperatorName, networkCountryIso, simOperatorName,
                phoneTypeString, dataStateString, callStateString);
    }

    private String getPhoneTypeString(int type) {
        switch (type) {
            case TelephonyManager.PHONE_TYPE_NONE: return "NONE";
            case TelephonyManager.PHONE_TYPE_GSM: return "GSM";
            case TelephonyManager.PHONE_TYPE_CDMA: return "CDMA";
            case TelephonyManager.PHONE_TYPE_SIP: return "SIP";
            default: return "UNKNOWN";
        }
    }

    private String getDataStateString(int state) {
        switch (state) {
            case TelephonyManager.DATA_DISCONNECTED: return "DISCONNECTED";
            case TelephonyManager.DATA_CONNECTING: return "CONNECTING";
            case TelephonyManager.DATA_CONNECTED: return "CONNECTED";
            case TelephonyManager.DATA_SUSPENDED: return "SUSPENDED";
            default: return "UNKNOWN";
        }
    }

    private String getCallStateString(int state) {
        switch (state) {
            case TelephonyManager.CALL_STATE_IDLE: return "IDLE";
            case TelephonyManager.CALL_STATE_RINGING: return "RINGING";
            case TelephonyManager.CALL_STATE_OFFHOOK: return "OFFHOOK";
            default: return "UNKNOWN";
        }
    }
}
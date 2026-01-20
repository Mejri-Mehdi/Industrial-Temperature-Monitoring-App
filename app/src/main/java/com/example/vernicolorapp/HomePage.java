package com.example.vernicolorapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class HomePage extends AppCompatActivity {

    private static final String TAG = "HomePage";
    private static final int TEMPERATURE_THRESHOLD = 30; // Set your temperature threshold here
    private static final String CHANNEL_ID = "temperature_channel";
    private static final String API_URL = "http://localhost:3000/TemperatureMonitor"; // Replace with your web service URL

    private FirebaseAuth mAuth;
    private ProgressBar[] progressBars;
    private TextView[] temperatureValues;
    private Button logoutButton, refreshButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        try {
            mAuth = FirebaseAuth.getInstance();

            progressBars = new ProgressBar[7];
            temperatureValues = new TextView[7];

            for (int i = 0; i < 7; i++) {
                int progressBarId = getResources().getIdentifier("progressBarTemperature" + (i + 1), "id", getPackageName());
                int temperatureValueId = getResources().getIdentifier("tvTemperatureValue" + (i + 1), "id", getPackageName());

                progressBars[i] = findViewById(progressBarId);
                temperatureValues[i] = findViewById(temperatureValueId);
            }

            logoutButton = findViewById(R.id.logout_button);
            refreshButton = findViewById(R.id.refresh_button);

            logoutButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mAuth.signOut();
                    SharedPreferences preferences = getSharedPreferences("UserPreferences", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.clear();
                    editor.apply();
                    Toast.makeText(HomePage.this, "Logged out successfully", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(HomePage.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            });

            refreshButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    refreshTemperatureValues();
                }
            });

            refreshTemperatureValues();
            checkNotificationPermission();
        } catch (Exception e) {
            Log.e(TAG, "Error initializing HomePage", e);
        }
    }

    private void refreshTemperatureValues() {
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                API_URL,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            StringBuilder redRooms = new StringBuilder();

                            for (int i = 0; i < response.length(); i++) {
                                JSONObject room = response.getJSONObject(i);
                                int roomId = room.getInt("roomId");
                                int temperature = room.getInt("temperature");

                                updateTemperatureDisplay(roomId - 1, temperature);

                                if (temperature > TEMPERATURE_THRESHOLD) {
                                    redRooms.append("Room ").append(roomId).append(", ");
                                }
                            }

                            if (redRooms.length() > 0) {
                                String rooms = redRooms.toString().replaceAll(", $", "");
                                sendNotification(rooms);
                            }

                            Toast.makeText(HomePage.this, "Temperature values refreshed", Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            Log.e(TAG, "Error parsing JSON data", e);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Error fetching temperature data", error);
                    }
                }
        );

        requestQueue.add(jsonArrayRequest);
    }

    private void updateTemperatureDisplay(int index, int temperature) {
        try {
            if (index < 0 || index >= progressBars.length) return;

            progressBars[index].setProgress(temperature);
            temperatureValues[index].setText(temperature + "°C");

            int color;
            int drawableId;

            if (index == 0 || index == 1) {
                if (temperature <= 17) {
                    color = getResources().getColor(android.R.color.holo_green_dark);
                    drawableId = R.drawable.progress_bar_green;
                } else if (temperature <= 25) {
                    color = getResources().getColor(android.R.color.holo_orange_dark);
                    drawableId = R.drawable.progress_bar_yellow;
                } else {
                    color = getResources().getColor(android.R.color.holo_red_dark);
                    drawableId = R.drawable.progress_bar_red;
                }
            } else {
                if (temperature <= 85) {
                    color = getResources().getColor(android.R.color.holo_green_dark);
                    drawableId = R.drawable.progress_bar_green;
                } else {
                    color = getResources().getColor(android.R.color.holo_red_dark);
                    drawableId = R.drawable.progress_bar_red;
                }
            }

            temperatureValues[index].setTextColor(color);
            progressBars[index].setProgressDrawable(getDrawable(drawableId));

        } catch (Exception e) {
            Log.e(TAG, "Error updating temperature display", e);
        }
    }

    private void sendNotification(String rooms) {
        try {
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(
                        CHANNEL_ID,
                        "Temperature Alerts",
                        NotificationManager.IMPORTANCE_HIGH
                );
                channel.setDescription("Channel for temperature alerts");
                channel.enableLights(true);
                channel.setLightColor(Color.RED);
                channel.enableVibration(true);
                notificationManager.createNotificationChannel(channel);
            }

            Intent intent = new Intent(this, HomePage.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setSmallIcon(android.R.drawable.ic_dialog_alert)
                    .setContentTitle("High Temperature Alert")
                    .setContentText("High temperature in: " + rooms)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true);

            notificationManager.notify(1, builder.build());
        } catch (Exception e) {
            Log.e(TAG, "Error sending notification", e);
        }
    }

    private void checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 1);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Notification permission granted");
            } else {
                Log.d(TAG, "Notification permission denied");
            }
        }
    }
}

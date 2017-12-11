package com.example.thymc.logintutorial;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.thymc.logintutorial.geofence.GPSTracker;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class UserAreaActivity extends AppCompatActivity {

    private EditText locationView;
    String username;
    String name;
    GPSTracker gps;

    NotificationCompat.Builder notification;
    private static final int uniqueID = 45612;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_area);

        notification = new NotificationCompat.Builder(this);
        notification.setAutoCancel(true);

        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        username = intent.getStringExtra("username");

        TextView tvWelcomeMsg = (TextView) findViewById(R.id.tvWelcomeMsg);
        EditText etUsername = (EditText) findViewById(R.id.etUsername);

        // Display user details
        String message = name + " welcome to your user area";
        tvWelcomeMsg.setText(message);
        etUsername.setText(username);

        Button button = (Button) findViewById(R.id.sendLocationButton);
        locationView = (EditText) findViewById(R.id.locationView);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String locX = null,locY = null;

                gps = new GPSTracker(UserAreaActivity.this);
                if(gps.canGetLocation()) {
                    double latitude = gps.getLatitude();
                    double longitude = gps.getLongitude();
                    locationView.setText("Coordinates: "+latitude+" -> "+longitude);
                    locX = Double.toString(latitude);
                    locY = Double.toString(longitude);
                } else {
                    gps.showSettingsAlert();
                }

                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");

                            if(success){
                                AlertDialog.Builder builder = new AlertDialog.Builder(UserAreaActivity.this);
                                builder.setMessage("Sending Success.")
                                        .setNegativeButton("Ok", null)
                                        .create()
                                        .show();
                            }else{
                                AlertDialog.Builder builder = new AlertDialog.Builder(UserAreaActivity.this);
                                builder.setMessage("Sending Fail.")
                                        .setNegativeButton("Retry", null)
                                        .create()
                                        .show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };

                List<String> argList = new ArrayList<>();
                argList.add(username);
                argList.add(locX);
                argList.add(locY);

                RequestServer sLocationRequest = new RequestServer("saveNotification",argList,responseListener);
                RequestQueue queue = Volley.newRequestQueue(UserAreaActivity.this);
                queue.add(sLocationRequest);
            }
        });

    }

    public void notificationClick(View view){
/*
        Intent intent = new Intent(this,UserAreaActivity.class);
        intent.putExtra("name", name);
        intent.putExtra("username", username);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        notification.setContentIntent(pendingIntent);
*/
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {

                    JSONObject jsonResponse = new JSONObject(response);
                    int success = jsonResponse.getInt("success");

                    if (success != 0) {
                        notification.setSmallIcon(R.drawable.ic_menu_send);
                        notification.setTicker("this is the ticker");
                        notification.setWhen(System.currentTimeMillis());
                        notification.setContentTitle("Bildirim");
                        notification.setContentText(success+" - Yeni Bildirim");

                    }else{
                        notification.setSmallIcon(R.drawable.ic_menu_send);
                        notification.setTicker("this is the ticker");
                        notification.setWhen(System.currentTimeMillis());
                        notification.setContentTitle("Bildirim Bulunamadi.");
                    }
                    NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    nm.notify(uniqueID,notification.build());

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        List<String> argList = new ArrayList<>();
        argList.add(username);
        RequestServer checkRequest = new RequestServer("checkNotification",argList,responseListener);
        RequestQueue queue = Volley.newRequestQueue(UserAreaActivity.this);
        queue.add(checkRequest);

    }

    public void showLocation(View view){

        gps = new GPSTracker(UserAreaActivity.this);
        if(gps.canGetLocation()) {
            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();
            locationView.setText("Coordinates: "+latitude+" -> "+longitude);

        } else {
            gps.showSettingsAlert();
        }
    }

    public void sendTokenNo(View view){

        String fcmID = FirebaseInstanceId.getInstance().getToken();

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

            }
        };

        List<String> argList = new ArrayList<>();
        argList.add(fcmID);
        RequestServer request = new RequestServer("getTokenNo",argList,responseListener);
        RequestQueue queue = Volley.newRequestQueue(UserAreaActivity.this);
        queue.add(request);
    }

}

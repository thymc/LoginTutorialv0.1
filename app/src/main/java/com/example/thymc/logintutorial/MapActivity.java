package com.example.thymc.logintutorial;

import android.Manifest;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.thymc.logintutorial.geo.GPSTracker;
import com.example.thymc.logintutorial.geo.GeofenceTransitionIntentService;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MapActivity extends AppCompatActivity implements
        ConnectionCallbacks, OnConnectionFailedListener, OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleMap.OnInfoWindowClickListener {
    private static final String TAG = "MapActivity";
    public static final String SHARED_PREFERENCES_NAME = BuildConfig.APPLICATION_ID + ".SHARED_PREFERENCES_NAME";
    public static final String NEW_GEOFENCE_NUMBER = BuildConfig.APPLICATION_ID + ".NEW_GEOFENCE_NUMBER";
    public static final long GEOFENCE_EXPIRATION_IN_HOURS = 24000;
    public static final long GEOFENCE_EXPIRATION_IN_MILLISECONDS = GEOFENCE_EXPIRATION_IN_HOURS * 60 * 60 * 1000;
    public static final float GEOFENCE_RADIUS_IN_METERS = 100; // 100 m
    private static final int PERMISSIONS_REQUEST = 105;

    protected GoogleApiClient mGoogleApiClient;
    protected ArrayList<Geofence> mGeofenceList;
    private PendingIntent mGeofencePendingIntent;
    private SharedPreferences mSharedPreferences;
    private GoogleMap googleMap;

    private String timeFrame = "always";

    EditText etComment = null;
    String username = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        Intent intent = getIntent();
        username =intent.getStringExtra("username");

        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mGeofenceList = new ArrayList<>();
        mGeofencePendingIntent = null;
        mSharedPreferences = getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE);
        buildGoogleApiClient();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder adbuilder = new AlertDialog.Builder(MapActivity.this);
                View mView = getLayoutInflater().inflate(R.layout.dialog_sharelocation, null);

                etComment = (EditText) mView.findViewById(R.id.editTextComment);

                RadioButton.OnClickListener radioListener =
                        new RadioButton.OnClickListener()
                        {
                            @Override
                            public void onClick(View view) {
                                // Is the button now checked?
                                boolean checked = ((RadioButton) view).isChecked();

                                // Check which radio button was clicked
                                switch(view.getId()) {
                                    case R.id.radioButtonMorn:
                                        if (checked)
                                            // sabah
                                            timeFrame = "morning";
                                        break;
                                    case R.id.radioButtonNoon:
                                        if (checked)
                                            // öğle
                                            timeFrame = "noon";
                                        break;
                                    case R.id.radioButtonEve:
                                        if (checked)
                                            // akşam
                                            timeFrame = "evening";
                                        break;
                                    case R.id.radioButtonAlw:
                                        if (checked)
                                            // her zaman
                                            timeFrame = "always";
                                        break;
                                }
                            }
                        };
                RadioButton rbMorn = (RadioButton) mView.findViewById(R.id.radioButtonMorn);
                rbMorn.setOnClickListener(radioListener);
                RadioButton rbNoon = (RadioButton) mView.findViewById(R.id.radioButtonNoon);
                rbNoon.setOnClickListener(radioListener);
                RadioButton rbEve = (RadioButton) mView.findViewById(R.id.radioButtonEve);
                rbEve.setOnClickListener(radioListener);
                RadioButton rbAlw = (RadioButton) mView.findViewById(R.id.radioButtonAlw);
                rbAlw.setOnClickListener(radioListener);

                adbuilder.setView(mView);
                final AlertDialog dialog = adbuilder.create();
                dialog.show();

                Button button = (Button) mView.findViewById(R.id.buttonShareLoc);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        double latitude = 0,longitude = 0;
                        GPSTracker gps = new GPSTracker(MapActivity.this);
                        if(gps.canGetLocation()) {
                            latitude = gps.getLatitude();
                            longitude = gps.getLongitude();
                        } else {
                            gps.showSettingsAlert();
                        }
                        nMapClick(new LatLng(latitude,longitude));

                        dialog.cancel();
                    }
                });
            }

        });
    }

    /**
     * Builds a GoogleApiClient. Uses the {@code #addApi} method to request the LocationServices API.
     */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    /**
     * Runs when a GoogleApiClient object successfully connects.
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        Log.i(TAG, "Connected to GoogleApiClient");

    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
        // onConnectionFailed.
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }

    @Override
    public void onConnectionSuspended(int cause) {
        // The connection to Google Play services was lost for some reason.
        Log.i(TAG, "Connection suspended");

        // onConnected() will be called again automatically when the service reconnects
    }

    private GeofencingRequest getGeofencingRequest(Geofence geofence) {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofence(geofence);
        return builder.build();
    }

    private void logSecurityException(SecurityException securityException) {
        Log.e(TAG, "Invalid location permission. " +
                "You need to use ACCESS_FINE_LOCATION with geofences", securityException);
    }

    private PendingIntent getGeofencePendingIntent() {
        if (mGeofencePendingIntent != null) {
            return mGeofencePendingIntent;
        }
        Intent intent = new Intent(this, GeofenceTransitionIntentService.class);
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;
        initMap(googleMap);
    }

    private void initMap(GoogleMap map) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST);
            return;
        }
        TextView lat = (TextView)findViewById(R.id.lat);
        TextView lon = (TextView)findViewById(R.id.lon);
        GPSTracker gps = new GPSTracker(MapActivity.this);
        if(gps.canGetLocation()) {
            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();
            lat.setText("Latitude:"+Double.toString(latitude));
            lon.setText("Longitude:"+Double.toString(longitude));
        } else {
            gps.showSettingsAlert();
        }
        map.setMyLocationEnabled(true);
        //map.setOnMapClickListener(this);
        map.setOnMarkerClickListener(this);
        map.setOnInfoWindowClickListener(this);

        reloadMapMarkers();
    }

    private void reloadMapMarkers() {
        googleMap.clear();/*
        try (Cursor cursor = GeofenceStorage.getCursor()) {
            while (cursor.moveToNext()) {
                long expires = Long.parseLong(cursor.getString(cursor.getColumnIndex(GeofenceContract.GeofenceEntry.COLUMN_NAME_EXPIRES)));
                if(System.currentTimeMillis() < expires) {
                    String key = cursor.getString(cursor.getColumnIndex(GeofenceContract.GeofenceEntry.COLUMN_NAME_KEY));
                    double lat = Double.parseDouble(cursor.getString(cursor.getColumnIndex(GeofenceContract.GeofenceEntry.COLUMN_NAME_LAT)));
                    double lng = Double.parseDouble(cursor.getString(cursor.getColumnIndex(GeofenceContract.GeofenceEntry.COLUMN_NAME_LNG)));
                    addMarker(key, new LatLng(lat, lng));
                }
            }
        }*/
        List<String> argList = new ArrayList<>();
        argList.add(username);
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");
                    if (success) {
                        JSONArray sender = jsonResponse.getJSONArray("username");
                        JSONArray aExpires = jsonResponse.getJSONArray("expiresList");
                        JSONArray aKey = jsonResponse.getJSONArray("keyList");
                        JSONArray aLocX = jsonResponse.getJSONArray("locXList");
                        JSONArray aLocY = jsonResponse.getJSONArray("locYList");
                        JSONArray aComment = jsonResponse.getJSONArray("commentList");
                        JSONArray aTime = jsonResponse.getJSONArray("timeframeList");

                        for (int i=0;i<aExpires.length();i++){
                            if(String.valueOf(sender.get(i)).equals(username)){
                                long expires = Long.parseLong(String.valueOf(aExpires.get(i)));
                                if (System.currentTimeMillis() < expires) {
                                    String key = String.valueOf(aKey.get(i));
                                    double lat = Double.parseDouble(String.valueOf(aLocX.get(i)));
                                    double lng = Double.parseDouble(String.valueOf(aLocY.get(i)));
                                    addMarker(key, new LatLng(lat, lng), String.valueOf(sender.get(i)), String.valueOf(aComment.get(i)));
                                }
                            }else {
                                long expires = Long.parseLong(String.valueOf(aExpires.get(i)));
                                if (System.currentTimeMillis() < expires) {
                                    String key = String.valueOf(aKey.get(i));
                                    double lat = Double.parseDouble(String.valueOf(aLocX.get(i)));
                                    double lng = Double.parseDouble(String.valueOf(aLocY.get(i)));
                                    addFriendMarker(key, new LatLng(lat, lng), String.valueOf(sender.get(i)), String.valueOf(aComment.get(i)));
                                }
                            }
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        };

        RequestServer registerRequest = new RequestServer("getGeofence",argList,responseListener);
        RequestQueue queue = Volley.newRequestQueue(MapActivity.this);
        queue.add(registerRequest);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initMap(googleMap);
                } else {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST);
                }
                return;
            }
        }
    }

    public void nMapClick(final LatLng latLng) {
        if (!mGoogleApiClient.isConnected()) {
            Toast.makeText(this, getString(R.string.not_connected), Toast.LENGTH_SHORT).show();
            return;
        }
        final String key = username+";"+getNewGeofenceNumber() + "";
        final long expTime = System.currentTimeMillis() + GEOFENCE_EXPIRATION_IN_MILLISECONDS;
        addMarker(key, latLng,username,etComment.getText().toString());
        Geofence geofence = new Geofence.Builder()
                .setRequestId(key)
                .setCircularRegion(
                        latLng.latitude,
                        latLng.longitude,
                        GEOFENCE_RADIUS_IN_METERS
                )
                .setExpirationDuration(GEOFENCE_EXPIRATION_IN_MILLISECONDS)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                        Geofence.GEOFENCE_TRANSITION_EXIT)
                .build();
        try {
            LocationServices.GeofencingApi.addGeofences(
                    mGoogleApiClient,
                    getGeofencingRequest(geofence),
                    getGeofencePendingIntent()
            ).setResultCallback(new ResultCallback<Status>() {
                @Override
                public void onResult(@NonNull Status status) {
                    if (status.isSuccess()) {
                        //GeofenceStorage.saveToDb(key, latLng, expTime);
                        saveToDb(key, latLng, expTime);
                        Toast.makeText(MapActivity.this, getString(R.string.geofences_added), Toast.LENGTH_SHORT).show();
                    } else {
                        String errorMessage = GeofenceTransitionIntentService.getErrorString(MapActivity.this, status.getStatusCode());
                        Log.e(TAG, errorMessage);
                    }
                }
            });
        } catch (SecurityException securityException) {
            logSecurityException(securityException);
        }
    }

    private void saveToDb(String key, LatLng latLng, long expires){

        List<String> argList = new ArrayList<>();
        argList.add(username);
        argList.add(key);
        argList.add(String.valueOf(expires));
        argList.add(String.valueOf(latLng.latitude));
        argList.add(String.valueOf(latLng.longitude));

        if(etComment != null){
            argList.add(etComment.getText().toString());
        }else {
            argList.add("test");
        }
        argList.add(timeFrame);
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

            }
        };

        RequestServer registerRequest = new RequestServer("saveGeofence",argList,responseListener);
        RequestQueue queue = Volley.newRequestQueue(MapActivity.this);
        queue.add(registerRequest);
    }

    private void addMarker(String key, LatLng latLng,String sender,String comment) {
        googleMap.addMarker(new MarkerOptions()
                .title(key)
                .snippet("Comment:"+comment+"  - Click here if you want delete")
                .position(latLng));
        googleMap.addCircle(new CircleOptions()
                .center(latLng)
                .radius(GEOFENCE_RADIUS_IN_METERS)
                .strokeColor(Color.RED)
                .fillColor(Color.parseColor("#80ff0000")));
    }

    private void addFriendMarker(String key, LatLng latLng,String sender,String comment) {
        googleMap.addMarker(new MarkerOptions()
                .title(key)
                .snippet("Comment:"+comment+"  - Click here if you want delete")
                .position(latLng));
        googleMap.addCircle(new CircleOptions()
                .center(latLng)
                .radius(GEOFENCE_RADIUS_IN_METERS)
                .strokeColor(Color.BLACK)
                .fillColor(Color.parseColor("#80000000")));
    }

    private void addMarker2(String key, LatLng latLng) {
        googleMap.addMarker(new MarkerOptions()
                .title("G:" + key)
                .snippet("Click here if you want delete this geofence")
                .position(latLng));
        googleMap.addCircle(new CircleOptions()
                .center(latLng)
                .radius(GEOFENCE_RADIUS_IN_METERS)
                .strokeColor(Color.BLUE)
                .fillColor(Color.parseColor("#800000ff")));
    }

    private int getNewGeofenceNumber(){
        int number = mSharedPreferences.getInt(NEW_GEOFENCE_NUMBER, 0);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putInt(NEW_GEOFENCE_NUMBER, number + 1);
        editor.commit();
        return number;
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        //final String requestId = marker.getTitle().split(":")[1];
        final String requestId = marker.getTitle();
        if (!mGoogleApiClient.isConnected()) {
            Toast.makeText(this, getString(R.string.not_connected), Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            List<String> idList = new ArrayList<>();
            idList.add(requestId);
            LocationServices.GeofencingApi.removeGeofences(mGoogleApiClient, idList).setResultCallback(new ResultCallback<Status>() {
                @Override
                public void onResult(@NonNull Status status) {
                    if (status.isSuccess()) {
                        //GeofenceStorage.removeGeofence(requestId);
                        removeGeofence(requestId);
                        Toast.makeText(MapActivity.this, getString(R.string.geofences_removed), Toast.LENGTH_SHORT).show();
                        reloadMapMarkers();
                    } else {
                        // Get the status code for the error and log it using a user-friendly message.
                        String errorMessage = GeofenceTransitionIntentService.getErrorString(MapActivity.this,
                                status.getStatusCode());
                        Log.e(TAG, errorMessage);
                    }
                }
            });
        } catch (SecurityException securityException) {
            logSecurityException(securityException);
        }
    }

    private void removeGeofence(String requestID){

        List<String> argList = new ArrayList<>();
        argList.add(username);
        argList.add(requestID);


        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

            }
        };

        RequestServer registerRequest = new RequestServer("removeGeofence",argList,responseListener);
        RequestQueue queue = Volley.newRequestQueue(MapActivity.this);
        queue.add(registerRequest);
    }
}
package com.example.thymc.logintutorial.geo;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.thymc.logintutorial.MainActivity;
import com.example.thymc.logintutorial.MapActivity;
import com.example.thymc.logintutorial.R;
import com.example.thymc.logintutorial.RequestServer;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingEvent;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class GeofenceTransitionIntentService extends IntentService {
    private static final String TAG = "GTIntentService";

    public GeofenceTransitionIntentService() {
        super("GeofenceTransitionsIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            String errorMessage = getErrorString(this, geofencingEvent.getErrorCode());
            Log.e(TAG, errorMessage);
            return;
        }
        int geofenceTransition = geofencingEvent.getGeofenceTransition();

        //if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER || geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT)
        if(geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER|| geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT){
            List triggeringGeofences = geofencingEvent.getTriggeringGeofences();

            Toast.makeText(GeofenceTransitionIntentService.this, "asd", Toast.LENGTH_SHORT).show();
            //String geofenceTransitionDetails = getGeofenceTransitionDetails(this, geofenceTransition, triggeringGeofences);
            notificationSender(getGeofenceTransitionDetails(this, geofenceTransition, triggeringGeofences));
            //sendNotification();
            //Log.i(TAG, geofenceTransitionDetails);
        } else {
            Log.e(TAG, getString(R.string.geofence_transition_invalid_type, geofenceTransition));
        }
    }

    private void notificationSender(final ArrayList<String> list){

        Toast.makeText(GeofenceTransitionIntentService.this, TextUtils.join(", ",  list), Toast.LENGTH_SHORT).show();
        List<String> argList = new ArrayList<>();
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");
                    if (success) {
                        JSONArray userList = jsonResponse.getJSONArray("username");
                        JSONArray aKey = jsonResponse.getJSONArray("keyList");
                        JSONArray aComment = jsonResponse.getJSONArray("commentList");
                        //JSONArray aTime = jsonResponse.getJSONArray("timeframeList");

                        for (int i=0;i<list.size();i++){
                            for(int j = 0; j<aKey.length();j++){
                                if(list.get(i).equals(String.valueOf(aKey.get(j)))){

                                    sendNotification(String.valueOf(userList.get(j)),String.valueOf(aComment.get(j)));
                                }
                            }
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        };

        RequestServer registerRequest = new RequestServer("getGeofences",argList,responseListener);
        RequestQueue queue = Volley.newRequestQueue(GeofenceTransitionIntentService.this);
        queue.add(registerRequest);
    }

    private ArrayList<String> getGeofenceTransitionDetails(
            Context context,
            int geofenceTransition,
            List<Geofence> triggeringGeofences) {

        //String geofenceTransitionString = getTransitionString(geofenceTransition);//entered yazan yer
        //ArrayList triggeringGeofencesIdsList = new ArrayList();
        ArrayList<String> geofencesList = new ArrayList<>();
        for (Geofence geofence : triggeringGeofences) {
            //triggeringGeofencesIdsList.add(geofence.getRequestId());
            geofencesList.add(geofence.getRequestId());
        }
        //bura değişecek
        //String triggeringGeofencesIdsString = TextUtils.join(", ",  geofencesList);
        //Toast.makeText(GeofenceTransitionIntentService.this, triggeringGeofencesIdsString, Toast.LENGTH_SHORT).show();
        return geofencesList;
    }

    /**
     * Posts a notification in the notification bar when a transition is detected.
     * If the user clicks the notification, control goes to the MapActivity.
     */
    private void sendNotification(String userName,String notificationDetails) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this);

        int uniqueID = (int) System.currentTimeMillis();
        notificationBuilder.setSmallIcon(R.drawable.ic_stat_notification);
        notificationBuilder.setTicker("Getting Shared Location");
        notificationBuilder.setWhen(System.currentTimeMillis());
        notificationBuilder.setContentTitle(userName);
        notificationBuilder.setContentText(notificationDetails);

        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        nm.notify(uniqueID++,notificationBuilder.build());
    }

    /**
     * Maps geofence transition types to their human-readable equivalents.
     *
     * @param transitionType    A transition type constant defined in Geofence
     * @return                  A String indicating the type of transition
     */
    private String getTransitionString(int transitionType) {
        switch (transitionType) {
            case Geofence.GEOFENCE_TRANSITION_ENTER:
                return getString(R.string.geofence_transition_entered);
            case Geofence.GEOFENCE_TRANSITION_EXIT:
                return getString(R.string.geofence_transition_exited);
            default:
                return getString(R.string.unknown_geofence_transition);
        }
    }

    public static String getErrorString(Context context, int errorCode) {
        Resources mResources = context.getResources();
        switch (errorCode) {
            case GeofenceStatusCodes.GEOFENCE_NOT_AVAILABLE:
                return mResources.getString(R.string.geofence_not_available);
            case GeofenceStatusCodes.GEOFENCE_TOO_MANY_GEOFENCES:
                return mResources.getString(R.string.geofence_too_many_geofences);
            case GeofenceStatusCodes.GEOFENCE_TOO_MANY_PENDING_INTENTS:
                return mResources.getString(R.string.geofence_too_many_pending_intents);
            default:
                return mResources.getString(R.string.unknown_geofence_error);
        }

    }
}
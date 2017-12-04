package com.example.thymc.logintutorial;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView mTextMessage;
    private GPSTracker gps;
    private String username;
    private String userComment;
    private String timeFrame;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    mTextMessage.setText(R.string.title_home);
                    return true;
                case R.id.navigation_dashboard:
                    mTextMessage.setText(R.string.title_dashboard);
                    Intent intent = new Intent(MainActivity.this, Users.class);
                    Intent intent2 = getIntent();
                    intent.putExtra("username",intent2.getStringExtra("username"));
                    MainActivity.this.startActivity(intent);
                    return true;
                case R.id.navigation_notifications:
                    mTextMessage.setText(R.string.title_notifications);
                    Intent intent3 = new Intent(MainActivity.this, UserAreaActivity.class);
                    MainActivity.this.startActivity(intent3);
                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextMessage = (TextView) findViewById(R.id.message);
        Button button = (Button) findViewById(R.id.buttonShareLoc);
        final EditText etComment = (EditText) findViewById(R.id.editTextComment);

        Intent intent = getIntent();
        username = intent.getStringExtra("username");

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String locX = null, locY = null;

                gps = new GPSTracker(MainActivity.this);
                if(gps.canGetLocation()) {
                    double latitude = gps.getLatitude();
                    double longitude = gps.getLongitude();
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
                                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                builder.setMessage("Sending Success.")
                                        .setNegativeButton("Ok", null)
                                        .create()
                                        .show();
                            }else{
                                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
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
                //argList.add(userComment);
                argList.add(etComment.getText().toString());
                argList.add(timeFrame);

                RequestServer sLocationRequest = new RequestServer("saveNotification",argList,responseListener);
                RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
                queue.add(sLocationRequest);
            }
        });

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

    }

    public void onRadioButtonClicked(View view) {
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

}
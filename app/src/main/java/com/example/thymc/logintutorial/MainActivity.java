package com.example.thymc.logintutorial;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.app.AlertDialog;
import android.view.View;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.thymc.logintutorial.geo.GPSTracker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ListView listView;
    private TextView mTextMessage;
    private GPSTracker gps;
    private String username;
    private String timeFrame = "always";

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            Intent intent2 = getIntent();
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    //mTextMessage.setText(R.string.title_home);
                    return true;
                case R.id.map_notifications:
                    //mTextMessage.setText(R.string.title_notifications);
                    Intent intent4= new Intent(MainActivity.this, MapActivity.class);
                    intent4.putExtra("username",intent2.getStringExtra("username"));
                    MainActivity.this.startActivity(intent4);
                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = (ListView)findViewById(R.id.menuListView);
        String[] values = new String[]{"Find Users","Friend Requests","Friends","Settings"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,values);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0){
                    generateUserListContent();
                }
                if(position == 1){
                    generateFriendRequestListContent();
                }
                if(position == 2){
                    generateFriendListContent();
                }
                if(position == 3){
                    Intent intent = new Intent(MainActivity.this, Settings.class);
                    MainActivity.this.startActivity(intent);
                }
            }
        });
        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        TextView profilName = (TextView) findViewById(R.id.profil_name);
        profilName.setText(intent.getStringExtra("name"));

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }


    private void generateUserListContent(){
        ArrayList<String> argList = new ArrayList<>();
        final ArrayList<String> userList = new ArrayList<String>();

        final Intent intent2 = getIntent();
        final String username = intent2.getStringExtra("username");
        argList.add(username);
        // Response received from the server
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {

                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");

                    if (success) {
                        JSONArray jArray = jsonResponse.getJSONArray("userList");

                        for (int i=0;i<jArray.length();i++){
                            userList.add(jArray.getString(i));
                        }


                        Intent intent = new Intent(MainActivity.this, UserList.class);
                        intent.putExtra("username",username);
                        intent.putExtra("userList",userList);
                        MainActivity.this.startActivity(intent);

                    }else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setMessage("Action Failed")
                                .setNegativeButton("Retry", null)
                                .create()
                                .show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        RequestServer getUserList = new RequestServer("getUserList",argList,responseListener);
        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
        queue.add(getUserList);
    }

    private void generateFriendRequestListContent(){
        ArrayList<String> argList = new ArrayList<>();
        final ArrayList<String> requestList = new ArrayList<String>();

        final Intent intent2 = getIntent();
        argList.add(intent2.getStringExtra("username"));
        // Response received from the server
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {

                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");

                    if (success) {
                        JSONArray jArray = jsonResponse.getJSONArray("userList");

                        for (int i=0;i<jArray.length();i++){
                            requestList.add(jArray.getString(i));
                        }


                        Intent intent = new Intent(MainActivity.this, FriendRequests.class);
                        intent.putExtra("username",intent2.getStringExtra("username"));
                        intent.putExtra("requestList",requestList);
                        MainActivity.this.startActivity(intent);

                    }else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setMessage("Action Failed")
                                .setNegativeButton("Retry", null)
                                .create()
                                .show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        RequestServer getUserList = new RequestServer("getFriendRequestList",argList,responseListener);
        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
        queue.add(getUserList);
    }

    private void generateFriendListContent(){
        ArrayList<String> argList = new ArrayList<>();
        final ArrayList<String> friendList = new ArrayList<String>();

        final Intent intent2 = getIntent();
        argList.add(intent2.getStringExtra("username"));
        // Response received from the server
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {

                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");

                    if (success) {
                        JSONArray jArray = jsonResponse.getJSONArray("userList");

                        for (int i=0;i<jArray.length();i++){
                            friendList.add(jArray.getString(i));
                        }


                        Intent intent = new Intent(MainActivity.this, FriendList.class);
                        intent.putExtra("username",intent2.getStringExtra("username"));
                        intent.putExtra("friendList",friendList);
                        MainActivity.this.startActivity(intent);

                    }else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setMessage("Action Failed")
                                .setNegativeButton("Retry", null)
                                .create()
                                .show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        RequestServer getUserList = new RequestServer("getFriendsList",argList,responseListener);
        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
        queue.add(getUserList);
    }
}

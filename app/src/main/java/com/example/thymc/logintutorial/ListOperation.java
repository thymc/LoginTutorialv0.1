package com.example.thymc.logintutorial;

import android.app.AlertDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ListOperation extends AppCompatActivity {

    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_operation);
        listView = (ListView)findViewById(R.id.menuListView);
        String[] values = new String[]{"Find Users","Show Friend Requests","Show Friends"};
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
            }
        });


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
                            if(!username.equals(jArray.getString(i))){
                                userList.add(jArray.getString(i));
                            }
                        }


                        Intent intent = new Intent(ListOperation.this, Users.class);
                        intent.putExtra("username",username);
                        intent.putExtra("userList",userList);
                        ListOperation.this.startActivity(intent);

                    }else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(ListOperation.this);
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
        RequestQueue queue = Volley.newRequestQueue(ListOperation.this);
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


                        Intent intent = new Intent(ListOperation.this, FriendRequests.class);
                        intent.putExtra("username",intent2.getStringExtra("username"));
                        intent.putExtra("requestList",requestList);
                        ListOperation.this.startActivity(intent);

                    }else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(ListOperation.this);
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
        RequestQueue queue = Volley.newRequestQueue(ListOperation.this);
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


                        Intent intent = new Intent(ListOperation.this, FriendList.class);
                        intent.putExtra("username",intent2.getStringExtra("username"));
                        intent.putExtra("friendList",friendList);
                        ListOperation.this.startActivity(intent);

                    }else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(ListOperation.this);
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
        RequestQueue queue = Volley.newRequestQueue(ListOperation.this);
        queue.add(getUserList);
    }
}

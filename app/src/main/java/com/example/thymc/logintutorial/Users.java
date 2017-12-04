package com.example.thymc.logintutorial;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Users extends AppCompatActivity{


    private final ArrayList<String> userList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        generateListContent();
        setContentView(R.layout.content_users);

        ListView lv = (ListView) findViewById(R.id.listview);

        MyListAdapter adapter=new MyListAdapter(this, userList);

        lv.setAdapter(adapter);
    }

    private void generateListContent(){
        List<String> argList = new ArrayList<>();

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
                    }else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(Users.this);
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
        RequestQueue queue = Volley.newRequestQueue(Users.this);
        queue.add(getUserList);
    }

    private class MyListAdapter extends ArrayAdapter<String> {

        public MyListAdapter(Context context, ArrayList<String> resource) {
            super(context,0, resource);
        }
        @Override
        public View getView(int position, View convertView,ViewGroup parent){
            String item = getItem(position);
            if(convertView == null){
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.userlist_item,parent,false);
            }

            final TextView list_Txt=(TextView)convertView.findViewById(R.id.userlist_item_text);
            Button list_But=(Button)convertView.findViewById(R.id.userlist_item_button);

            list_Txt.setText(item);

            list_But.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Response.Listener<String> responseListener = new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                        }
                    };
                    Intent intent = getIntent();
                    List<String> argList = new ArrayList<>();
                    argList.add("denemeAndroid");
                    argList.add((String) list_Txt.getText());
                    argList.add(intent.getStringExtra("username"));
                    RequestServer request = new RequestServer("friendRequest",argList,responseListener);
                    RequestQueue queue = Volley.newRequestQueue(Users.this);
                    queue.add(request);
                }
            });

            return convertView;
        }
    }
    private class ViewHolder {

        ImageView thumbnail;
        TextView title;
        Button button;
    }
}

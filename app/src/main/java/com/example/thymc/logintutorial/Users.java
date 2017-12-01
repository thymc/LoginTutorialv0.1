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
        lv.setAdapter(new MyListAdapter(this,R.layout.userlist_item));

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
        private int layout;
        private MyListAdapter(Context context, int resource) {
            super(context, resource);
            layout = resource;
        }

        @NonNull
        @Override
        public View getView(final int position, View convertView, @NonNull final ViewGroup parent) {
            ViewHolder mainViewholder;
            if(convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(layout, parent, false);
                ViewHolder viewHolder = new ViewHolder();
                viewHolder.thumbnail = (ImageView) convertView.findViewById(R.id.userlist_item_thumbnail);
                viewHolder.title = (TextView) convertView.findViewById(R.id.userlist_item_text);
                viewHolder.button = (Button) convertView.findViewById(R.id.userlist_item_button);
                convertView.setTag(viewHolder);
            }
            mainViewholder = (ViewHolder) convertView.getTag();
            mainViewholder.button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = getIntent();
                    //Toast.makeText(getContext(), "Button was clicked("+intent.getStringExtra("username")+") for list item " + userList.get(position), Toast.LENGTH_SHORT).show();
                    Response.Listener<String> responseListener = new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                        }
                    };
                    List<String> argList = new ArrayList<>();
                    argList.add("denemeAndroid");
                    argList.add(userList.get(position));
                    argList.add(intent.getStringExtra("username"));
                    RequestServer request = new RequestServer("friendRequest",argList,responseListener);
                    RequestQueue queue = Volley.newRequestQueue(Users.this);
                    queue.add(request);
                }
            });
            mainViewholder.title.setText(getItem(position));

            return convertView;
        }
    }
    private class ViewHolder {

        ImageView thumbnail;
        TextView title;
        Button button;
    }
}

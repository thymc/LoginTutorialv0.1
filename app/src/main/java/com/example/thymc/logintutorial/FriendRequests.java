package com.example.thymc.logintutorial;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;
import java.util.List;

public class FriendRequests extends ActionBarActivity {

    private Runnable viewParts;
    private MyListAdapter adapter;
    private ArrayList<Request> requestList = new ArrayList<Request>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //generateListContent();
        setContentView(R.layout.content_requests);
        fillList();
        ListView lv = (ListView) findViewById(R.id.requestListView);

        adapter = new MyListAdapter(this,R.layout.requestlist_item, requestList);
        lv.setAdapter(adapter);

    }


    private void fillList()
    {
            Intent intent = getIntent();
            ArrayList<String> argUserList = new ArrayList<>(intent.getStringArrayListExtra("requestList"));
            for(String s:argUserList){
                requestList.add(new Request(s));
            }
    }


    private class MyListAdapter extends ArrayAdapter<Request> {

        private ArrayList<Request> objects;
        public MyListAdapter(Context context, int textViewResourceId, ArrayList<Request> objects) {
            super(context,textViewResourceId, objects);
            this.objects = objects;
        }
        @Override
        public View getView(int position, View convertView,ViewGroup parent){
            Request item = objects.get(position);
            if(convertView == null){
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.requestlist_item,parent,false);
            }

            final TextView list_Txt=(TextView)convertView.findViewById(R.id.requestlist_item_text);
            ImageButton list_A_But=(ImageButton)convertView.findViewById(R.id.requestlist_item_button_accept);
            ImageButton list_D_But=(ImageButton)convertView.findViewById(R.id.requestlist_item_button_decline);

            list_Txt.setText((CharSequence) item.title);

            list_A_But.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Response.Listener<String> responseListener = new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            if(response.equals("success")){

                                finish();
                                Intent intent2 = getIntent();
                                Intent intent = new Intent(FriendRequests.this, FriendRequests.class);
                                intent.putExtra("username",intent2.getStringExtra("username"));
                                ArrayList<String> argUserList = new ArrayList<>(intent2.getStringArrayListExtra("requestList"));
                                argUserList.remove((String) list_Txt.getText());
                                intent.putExtra("requestList",argUserList);
                                FriendRequests.this.startActivity(intent);
                            }
                        }
                    };
                    Intent intent = getIntent();
                    List<String> argList = new ArrayList<>();
                    argList.add(intent.getStringExtra("username"));
                    argList.add((String) list_Txt.getText());
                    RequestServer request = new RequestServer("acceptFriendRequestList",argList,responseListener);
                    RequestQueue queue = Volley.newRequestQueue(FriendRequests.this);
                    queue.add(request);

                }
            });

            list_D_But.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Response.Listener<String> responseListener = new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            finish();
                            Intent intent2 = getIntent();
                            Intent intent = new Intent(FriendRequests.this, FriendRequests.class);
                            intent.putExtra("username",intent2.getStringExtra("username"));
                            ArrayList<String> argUserList = new ArrayList<>(intent2.getStringArrayListExtra("requestList"));
                            argUserList.remove((String) list_Txt.getText());
                            intent.putExtra("requestList",argUserList);
                            FriendRequests.this.startActivity(intent);
                        }
                    };

                    Intent intent = getIntent();
                    List<String> argList = new ArrayList<>();
                    argList.add(intent.getStringExtra("username"));
                    argList.add((String) list_Txt.getText());
                    RequestServer request = new RequestServer("declineFriendRequestList",argList,responseListener);
                    RequestQueue queue = Volley.newRequestQueue(FriendRequests.this);
                    queue.add(request);
                }
            });
            return convertView;
        }
    }

    public class Request{
        ImageView thumbnail;
        String title;
        ImageButton aButton;
        ImageButton dButton;
        Request(String text){
            title = text;
        }
    }
}

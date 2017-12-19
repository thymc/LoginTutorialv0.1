package com.example.thymc.logintutorial;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;
import java.util.List;

public class Users extends ListActivity {

    private Runnable viewParts;
    private MyListAdapter adapter;
    private ArrayList<User> userList = new ArrayList<User>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_users);


        adapter = new MyListAdapter(this,R.layout.userlist_item, userList);
        setListAdapter(adapter);

        viewParts = new Runnable(){
            public void run(){
                handler.sendEmptyMessage(0);
            }
        };

        Thread thread =  new Thread(null, viewParts, "MagentoBackground");
        thread.start();
    }


    private Handler handler = new Handler()
    {
        public void handleMessage(Message msg)
        {
            // create some objects
            // here is where you could also request data from a server
            // and then create objects from that data.


            Intent intent = getIntent();
            ArrayList<String> argUserList = new ArrayList<>(intent.getStringArrayListExtra("userList"));
            for(String s:argUserList){

                userList.add(new User(s));
            }
            adapter = new MyListAdapter(Users.this, R.layout.userlist_item, userList);

            // display the list.
            setListAdapter(adapter);
        }
    };


    private class MyListAdapter extends ArrayAdapter<User> {

        private ArrayList<User> objects;
        public MyListAdapter(Context context, int textViewResourceId, ArrayList<User> objects) {
            super(context,textViewResourceId, objects);
            this.objects = objects;
        }
        @Override
        public View getView(int position, View convertView,ViewGroup parent){
            User item = objects.get(position);
            if(convertView == null){
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.userlist_item,parent,false);
            }

            final TextView list_Txt=(TextView)convertView.findViewById(R.id.userlist_item_text);
            Button list_But=(Button)convertView.findViewById(R.id.requestlist_item_button_accept);

            list_Txt.setText((CharSequence) item.title);

            list_But.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Response.Listener<String> responseListener = new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            finish();
                            Intent intent2 = getIntent();
                            Intent intent = new Intent(Users.this, Users.class);
                            intent.putExtra("username",intent2.getStringExtra("username"));
                            ArrayList<String> argUserList = new ArrayList<>(intent2.getStringArrayListExtra("userList"));
                            argUserList.remove((String) list_Txt.getText());
                            intent.putExtra("userList",argUserList);
                            Users.this.startActivity(intent);
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

    public class User{
        ImageView thumbnail;
        String title;
        Button button;
        User(String text){
            title = text;
        }
    }
}

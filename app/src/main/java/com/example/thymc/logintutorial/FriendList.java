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

public class FriendList extends ActionBarActivity {

    private Runnable viewParts;
    private MyListAdapter adapter;
    private ArrayList<Friend> friendList = new ArrayList<Friend>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //generateListContent();
        setContentView(R.layout.content_friends);
        fillList();
        ListView lv = (ListView) findViewById(R.id.friendListView);

        adapter = new MyListAdapter(this,R.layout.friendlist_item, friendList);
        lv.setAdapter(adapter);

    }


    private void fillList()
    {
            Intent intent = getIntent();
            ArrayList<String> argUserList = new ArrayList<>(intent.getStringArrayListExtra("friendList"));
            for(String s:argUserList){
                friendList.add(new Friend(s));
            }


    }


    private class MyListAdapter extends ArrayAdapter<Friend> {

        private ArrayList<Friend> objects;
        public MyListAdapter(Context context, int textViewResourceId, ArrayList<Friend> objects) {
            super(context,textViewResourceId, objects);
            this.objects = objects;
        }
        @Override
        public View getView(int position, View convertView,ViewGroup parent){
            Friend item = objects.get(position);
            if(convertView == null){
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.friendlist_item,parent,false);
            }

            final TextView list_Txt=(TextView)convertView.findViewById(R.id.friendlist_item_text);
            ImageButton list_A_But=(ImageButton)convertView.findViewById(R.id.friendlist_item_button_delete);

            list_Txt.setText((CharSequence) item.title);

            list_A_But.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    /*
                    Response.Listener<String> responseListener = new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            if(response.equals("success")){

                                finish();
                                Intent intent2 = getIntent();
                                Intent intent = new Intent(FriendList.this, FriendList.class);
                                intent.putExtra("username",intent2.getStringExtra("username"));
                                ArrayList<String> argUserList = new ArrayList<>(intent2.getStringArrayListExtra("friendList"));
                                argUserList.remove((String) list_Txt.getText());
                                intent.putExtra("friendList",argUserList);
                                FriendList.this.startActivity(intent);
                            }
                        }
                    };
                    Intent intent = getIntent();
                    List<String> argList = new ArrayList<>();
                    argList.add(intent.getStringExtra("username"));
                    argList.add((String) list_Txt.getText());
                    RequestServer request = new RequestServer("deleteFriend",argList,responseListener);
                    RequestQueue queue = Volley.newRequestQueue(FriendList.this);
                    queue.add(request);*/

                    finish();
                    Intent intent2 = getIntent();
                    Intent intent = new Intent(FriendList.this, FriendList.class);
                    intent.putExtra("username",intent2.getStringExtra("username"));
                    ArrayList<String> argUserList = new ArrayList<>(intent2.getStringArrayListExtra("friendList"));
                    argUserList.remove((String) list_Txt.getText());
                    intent.putExtra("friendList",argUserList);
                    FriendList.this.startActivity(intent);
                }
            });

            return convertView;
        }
    }

    public class Friend{
        ImageView thumbnail;
        String title;
        ImageButton dButton;
        Friend(String text){
            title = text;
        }
    }
}

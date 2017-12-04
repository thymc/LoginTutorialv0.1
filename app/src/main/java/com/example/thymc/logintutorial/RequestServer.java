package com.example.thymc.logintutorial;


import com.android.volley.toolbox.StringRequest;
import com.android.volley.Request;
import com.android.volley.Response;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

class RequestServer extends StringRequest {
    private static String serverIP = "78.160.50.127";
    private Map<String, String> params;

    RequestServer(String type, List<String> argList, Response.Listener<String> listener) {
        super(Request.Method.POST,"http://"+serverIP+":8080/bildirim/"+type, listener, null);
        params = new HashMap<>();
        requestType(type,argList);
    }

    private void requestType(String type,List<String> argList){
        switch (type) {
            case "mobilLogin":
                params.put("userName", argList.get(0));
                params.put("password", argList.get(1));
                params.put("fcmID", argList.get(2));
                break;
            case "mobilRegister":
                params.put("name", argList.get(0));
                params.put("userName", argList.get(1));
                params.put("password", argList.get(2));
                break;
            case "checkNotification":
                params.put("userName", argList.get(0));
                break;
            case "saveNotification":
                params.put("userName", argList.get(0));
                params.put("locationX", argList.get(1));
                params.put("locationY", argList.get(2));
                params.put("comment", argList.get(3));
                params.put("time", argList.get(4));
                break;
            case "getTokenNo":
                params.put("id", argList.get(0));
                break;
            case "friendRequest":
                params.put("message", "Friend Request");
                params.put("to", argList.get(1));
                params.put("from", argList.get(2));
                break;
        }
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}

package com.example.lbs_tester10;

import android.content.Context;
import android.os.Message;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class RoomContextHttpManager {

    private static final String TAG = "uploadFile";
    private static final int TIME_OUT = 30*1000;   //超时时间
    private static final String CHARSET = "utf-8"; //设置编码

    //static RequestQueue queue;
    //public static RoomContextState state;
    private Context hContext;  //http Context
    private static RoomContextHttpManager instance;

    private RoomContextHttpManager(Context context) {
        this.hContext = context;
        //getLocation();
    }

    public static RoomContextHttpManager getInstance(Context context) {
        if (instance == null) {
            synchronized (RoomContextHttpManager.class) {
                if (instance == null) {
                    instance = new RoomContextHttpManager(context);
                    Log.d( "fly", "getNewInstance" );
                }
            }
        }
        return instance;
    }


    public static void switchLight(RoomContextState state, String room) {
        final String roomId = room;
        String url = "http://thawing-journey-78988.herokuapp.com/api/rooms/" + roomId + "/switch-light-and-list";
        StringRequest putRequest = new StringRequest(Request.Method.PUT, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d("Response", response);
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            JSONObject jo = jsonArray.getJSONObject(0);
                            String id = jo.getString("id").toString();
                            int lightLevel = Integer.parseInt(jo.getJSONObject("light").get("level").toString());

                            String lightStatus = jo.getJSONObject("light").get("status").toString();
                            float noise = Float.parseFloat(jo.getJSONObject("noise").get("level").toString());
                            MainActivity.state = new RoomContextState(id, lightStatus, lightLevel, noise);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("Error.Response", error.getMessage());
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("room", roomId);
                return params;
            }
        };
        MainActivity.queue.add(putRequest);

    }

    public static void retrieveRoomContextState(String room) {

        String CONTEXT_SERVER_URL = "https://thawing-journey-78988.herokuapp.com/api/rooms";
        //String CONTEXT_SERVER_URL = "https://faircorp-emse.cleverapps.io/api/rooms/getLights";

        String url = CONTEXT_SERVER_URL + "/" + room+ "/";

        Log.d("url is",url);
        //get room sensed context
        JsonObjectRequest contextRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String id = response.getString("id").toString();
                            int lightLevel = Integer.parseInt(response.getJSONObject("light").get("level").toString());
                            String lightStatus = response.getJSONObject("light").get("status").toString();
                            float noiseLevel = Float.parseFloat(response.getJSONObject("noise").get("level").toString());
                            //String noiseStatus = response.getJSONObject("noise").get("status").toString();
                            MainActivity.state = new RoomContextState(id, lightStatus,lightLevel,noiseLevel );
                            Log.d("lightstatus", lightStatus);
                            Log.d("state of RCHM", String.valueOf(MainActivity.state));
                            //MainActivity.onUpdate(state);
                            //new MainActivity().onUpdate(state);


                            //creating state
                            //RoomContextState state = new RoomContextState(id, lightStatus, noiseStatus, lightLevel, noiseLevel);

                            // do something with results...
                            // notify main activity for update...
                            // 子线程执行完毕的地方，利用主线程的handler发送消息
                            Message msg = new Message();
                            if(MainActivity.state!=null) {
                                msg.what = 1;
                                MainActivity.uiHandler.sendMessage(msg);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Some error to access URL : Room may not exists...
                        Log.d("error", "000");
                    }
                });
        MainActivity.queue.add(contextRequest);
        Log.d("addQueue","succesddful");
        Log.d("queue of RCHM", String.valueOf(contextRequest));
    }

}

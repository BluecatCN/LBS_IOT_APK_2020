package com.example.lbs_tester10;

import android.Manifest;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.LBS_IOT.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class MainActivity extends AppCompatActivity implements RoomContextStateListener,IGetMessageCallBack  {

    private static final int LOCATION_CODE = 1;
    private LocationManager lm;//位置管理
    private static Context mContext;
    //private static Context gContext;
    private Button button4; //for set home
    private Button button3; //for near home
    //private Handler mHanlder;
    private double count=0;  //for get the total number of loop times
    private String countstring;
    //private String phone_id;
    private String readexample;
    private String GCJLatitudeString;
    private String GCJLongitudeString;
    private double stringlength;
    private String stringlengthString;
    Date dt;
    String str_time;
    private ImageView gif=null;//gif;

    private static String locationList;
    private static String showLocationList;
    private static String RequestURL="http://192.168.43.222:8080/LBS_Net/LogUpload";


    public static double[] homeCord={0,0};
    public static double[] presentCord={100,100};

    public static String room;
    //public RequestQueue queue;
    private android.os.Bundle savedInstanceState;
    public static RoomContextState state;
    public View contextView;
    public ImageView image;
    public Context aContext;   //Activity Context
    public Button button = null;  //for turn light on/off
    public static RequestQueue queue;
    public static Handler uiHandler;
    private ArrayList<RoomContextRule> rules;

    //For MQTT
    private MyServiceConnection serviceConnection;
    private MQTTService mqttService;
    private TextView textView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        aContext = getApplicationContext();
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );
        mContext=MainActivity.this;

        queue = Volley.newRequestQueue(this);  //need to learn what's the queue?
//
        //for MQTT
        serviceConnection = new MyServiceConnection();
        serviceConnection.setIGetMessageCallBack(MainActivity.this);
        textView = (TextView) findViewById(R.id.text);
        Intent intent = new Intent(this, MQTTService.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);



        setContentView(R.layout.activity_main);
        ((Button) findViewById(R.id.buttonCheck)).setOnClickListener(new View.OnClickListener() {
                                                                         public void onClick(View v) {
                                                                             room = ((EditText) findViewById(R.id.editText1))
                                                                                     .getText().toString();
                                                                             RoomContextHttpManager.retrieveRoomContextState(room);

                                                                             Log.d("Back to", "ContextManagementActivity");

                                                                             //state=queue.getCache().get();

                                                                             uiHandler = new Handler() {
                                                                                 // 覆写这个方法，接收并处理消息。
                                                                                 @Override
                                                                                 public void handleMessage(Message msg) {
                                                                                     switch (msg.what) {
                                                                                         case 1:
                                                                                             onUpdate(state);
                                                                                             break;
                                                                                     }
                                                                                 }
                                                                             };

                                                                             Log.d("state of CMA", String.valueOf(state));
                                                                             //updateContextView();
                                                                             //Log.d("Ran","the updateContextView");

                                                                         }
                                                                     });

        ((Button) findViewById(R.id.button1)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                room = ((EditText) findViewById(R.id.editText1))
                        .getText().toString();

                switchLight();
                Log.d("Back to", "ContextManagementActivity");



            }
        });

                //click for go to set home page
        button4 = findViewById(R.id.button4);  // get the button, it is in activity_main.xml
        button4.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SetHomeCord.class);
                startActivity(intent);
            }
        });




        button3 = findViewById(R.id.button3);  // get the button, it is in activity_main.xml
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                TextView nearhome = (TextView)findViewById(R.id.nearhome); //R.id.tv是xml布局里textview对应的id
                String distance=String.valueOf(getDistance(homeCord[0],homeCord[1],presentCord[0],presentCord[1]));
                Log.d("lat and long present", String.valueOf(presentCord[0]+presentCord[1]));
                Log.d("lat and long home", String.valueOf(homeCord[0]+homeCord[1]));
                if(getDistance(homeCord[0],homeCord[1],presentCord[0],presentCord[1])<=1000){
                    nearhome.setText("YES");
                }else{
                    nearhome.setText("NO");
                }

                //MQTTService.publish("测试一下子");

        }
        });

        final Handler handler = new Handler();


        Runnable task = new Runnable() {
            @Override
            public void run() {
                /**
                 * 此处执行任务
                 * */
                GPSPermission();

                GPSUtils.getInstance( MainActivity.this );
                Location location = GPSUtils.getInstance( MainActivity.this ).showLocation();


                //先判断是否得到了location值, 否则为null会使程序崩溃
                if (location != null) {

                    presentCord=CoordinateUtil.transformFromWGSToGCJLat(location);
                    double GCJLatitude=CoordinateUtil.transformFromWGSToGCJLat(location)[0];
                    double GCJLongitude=CoordinateUtil.transformFromWGSToGCJLat(location)[1];

                    GCJLatitudeString=String.valueOf(GCJLatitude);
                    GCJLongitudeString=String.valueOf(GCJLongitude);


                    dt = new Date();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    str_time = sdf.format(dt);


                }

                handler.postDelayed(this, 120*1000);//延迟10 minutes,再次执行task本身,实现了循环的效果
            }
        };


        handler.postDelayed(task, 120*1000); // 接近无延迟运行，再运行splashhandler的run()

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    public double getDistance(double lat1, double lon1, double lat2, double lon2) {
        float[] results=new float[1];
        Location.distanceBetween(lat1, lon1, lat2, lon2, results);
        return results[0];
    }

    //for MQTT
    @Override
    public void setMessage(String message) {
        textView.setText(message);
        mqttService = serviceConnection.getMqttService();
        mqttService.toCreateNotification(message);
    }


    public void onUpdate(RoomContextState context){

        //this.state=context;
        //RoomContextHttpManager.retrieveRoomContextState(room);
        updateContextView();
    }

    public void onSwitch(RoomContextState context){

        //this.state=context;
        //RoomContextHttpManager.retrieveRoomContextState(room);
        switchLightView();
    }

    public void updateContextView(){
        if (this.state != null) {
            //contextView.setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.textViewLightValue)).setText(Integer
                    .toString(state.getLightLevel()));
            ((TextView) findViewById(R.id.textViewNoiseValue)).setText(Float
                    .toString(state.getNoiseLevel()));

            image = (ImageView) findViewById(R.id.imageView1);
            if (state.getLightStatus().equals("ON")){
                image.setImageResource(R.drawable.ic_bulb_on);}
            else{
                image.setImageResource(R.drawable.ic_bulb_off);}
        } else {
            initView();
        }
    }

    public void switchLightView(){

            image = (ImageView) findViewById(R.id.imageView1);
            if (state.getLightStatus().equals("ON")){
                image.setImageResource(R.drawable.ic_bulb_off);}
            else{
                image.setImageResource(R.drawable.ic_bulb_on);}
    }

    public void initView() {}

//    public void retrieveRoomContextState(String room) {
//    }

    public void switchRinger(View view) {

        NotificationManager notificationManager =
                (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && !notificationManager.isNotificationPolicyAccessGranted()) {
            Intent intent = new Intent(
                    android.provider.Settings
                            .ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
            startActivity(intent);
        }


        AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        int mode = audioManager.getRingerMode();
        if (mode == AudioManager.RINGER_MODE_SILENT)
            audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
        else {
            audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
            audioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
        }
    }

    public void switchLight() {
        RoomContextHttpManager.switchLight(state,room);
        RoomContextHttpManager.retrieveRoomContextState(room);
    }


    /**
     *  定位权限申请
     */
    public void GPSPermission(){
        lm = (LocationManager) MainActivity.this.getSystemService
                (MainActivity.this.LOCATION_SERVICE);
        boolean ok = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (ok) {//开了定位服务
            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                //Log.d("kly","没有权限");
                // 没有权限，申请权限。
                // 申请授权。
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_CODE);
                //Toast.makeText(getActivity(), "没有权限", Toast.LENGTH_SHORT).show();
                //ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, LOCATION_CODE);


            } else {
                // 有权限了，去放肆吧。
                Toast.makeText(getActivity(), "有权限", Toast.LENGTH_SHORT).show();
            }
        } else {
            Log.d("kly","系统检测到未开启GPS定位服务");
            Toast.makeText(MainActivity.this, "系统检测到未开启GPS定位服务", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivityForResult(intent, 1315);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case LOCATION_CODE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 权限被用户同意。

                } else {
                    // 权限被用户拒绝了。
                    //Toast.makeText(MainActivity.this, "定位权限被禁止，相关地图功能无法使用！",Toast.LENGTH_LONG).show();
                }

            }
        }
    }

    public Context getActivity(){
        mContext=MainActivity.this;
        return mContext;
    }
    @Override
    protected void onDestroy() {
        unbindService(serviceConnection);
        super.onDestroy();
        GPSUtils.getInstance( this ).removeLocationUpdatesListener();
    }


}

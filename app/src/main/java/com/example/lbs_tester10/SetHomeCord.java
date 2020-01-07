package com.example.lbs_tester10;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.content.Context;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.LBS_IOT.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;


public class SetHomeCord extends MainActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        String locationList;
//        String showLocationList;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_activity);

        Button btn1 = (Button) findViewById(R.id.button1);//获取present location
        final EditText tvLat = (EditText) findViewById(R.id.editText1);//获取输入文本框
        final EditText tvLong = (EditText) findViewById(R.id.editText2);//获取输出文本框
        btn1.setOnClickListener(new View.OnClickListener() {//定义按钮点击事件
            public void onClick(View v) {
                String Latvalue = String.valueOf(presentCord[0]);//获取输入内容
                String Longvalue = String.valueOf(presentCord[1]);//获取输入内容

                if (Latvalue !=null) {
                    tvLat.setText(Latvalue);//输出Present Lat;//输出显示
                }
                else{
                    tvLat.setText("NULL");//输出Present Lat;
                }

                if (Latvalue !=null) {
                    tvLong.setText(Longvalue);//输出Present Lat;//输出显示
                }
                else{
                    tvLong.setText("NULL");//输出Present Lat;
                }
            }
        });

        Button btn2 = (Button) findViewById(R.id.button2);//获取present location
        btn2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String writeLatvalue = tvLat.getText().toString();//获取输入内容
                if (writeLatvalue !=null) {
                    MainActivity.homeCord[0]=Double.parseDouble(writeLatvalue);//输出显示
                }
                else{
                    homeCord[0]=0.0;
                }
                String writeLongvalue = tvLong.getText().toString();//获取输入内容
                if (writeLongvalue !=null) {
                    MainActivity.homeCord[1]=Double.parseDouble(writeLongvalue);//输出显示
                }
                else{
                    homeCord[1]=0.0;
                }

                Toast.makeText(getActivity(), "Setted", Toast.LENGTH_SHORT).show();
            }
        });

    }
}

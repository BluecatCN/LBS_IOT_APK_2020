package com.example.lbs_tester10;

import android.app.Application;
import android.content.Context;

public class MyApplication extends Application {
    private static Context mContext;
    //private static MyApplication INSTANCE;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext=this;
    }
//    public static MyApplication getInstance() {
//        return INSTANCE;
//    }
    public static Context getContext() {
        return mContext;
    }

}
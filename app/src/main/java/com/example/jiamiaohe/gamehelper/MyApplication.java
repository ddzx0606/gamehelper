package com.example.jiamiaohe.gamehelper;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;

import com.example.administrator.endcall.BlockCallHelper;
import com.example.administrator.endcall.NotificationUtils;

/**
 * Created by jiamiaohe on 2017/7/27.
 */

public class MyApplication extends Application{

    private static Context mContext = null;
    private static Handler mHandler = new Handler();

    @Override
    public void onCreate() {
        super.onCreate();

        mContext = this;
        NotificationUtils.getInstance();

        ScreenShotterUtils.getInstance(); //init handler
        BlockCallHelper.getInstance().init(this);
        startService(new Intent(this, GameHelperService.class));
        startService(new Intent(this, RecordService.class));
    }



    public static Context getContext () {
        return mContext;
    }

    public static Handler getHandler () {
        return mHandler;
    }
}

package com.example.jiamiaohe.gamehelper;

/**
 * Created by jiamiaohe on 2017/7/28.
 */
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class HeadlessSmsSendService extends Service{

    @Override
    public IBinder onBind(Intent intent) {
        Log.i("cky","HeadlessSmsSendService: "+intent);
        return null;
    }

}
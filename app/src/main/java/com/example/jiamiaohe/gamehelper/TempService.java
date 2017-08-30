package com.example.jiamiaohe.gamehelper;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by jiamiaohe on 2017/8/21.
 */

public class TempService extends Service {


    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("TempService", "onCreate");
        startForeground(1, GameHelperService.getNotification());

        stopSelf();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}

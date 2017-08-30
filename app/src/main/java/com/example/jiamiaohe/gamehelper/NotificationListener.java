package com.example.jiamiaohe.gamehelper;

import android.os.Build;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.widget.Toast;

import java.util.Arrays;

/**
 * Created by jiamiaohe on 2017/7/27.
 */

public class NotificationListener extends NotificationListenerService {
    private static final String TAG = "hm";

    public static boolean mInterceptNotification = false;

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        super.onNotificationRemoved(sbn);
        Log.i(TAG,"Notification removed");
        byte []a1 = null;
        byte []a2 = null;
        Arrays.equals(a1, a2);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        super.onNotificationPosted(sbn);
        Log.i(TAG, "Notification posted");

        if (mInterceptNotification) {
            this.cancelNotification(sbn.getKey());
            Toast.makeText(this, "已拦截通知\n"+sbn.getPackageName(), Toast.LENGTH_SHORT).show();
        }
    }
}
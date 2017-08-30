package com.example.jiamiaohe.gamehelper;

/**
 * Created by jiamiaohe on 2017/7/27.
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

//继承BroadcastReceiver
public class AutoSMS extends BroadcastReceiver {

    private String TAG="hm";
    //广播消息类型
    public static final String SMS_RECEIVED_ACTION = "android.provider.Telephony.SMS_RECEIVED";
    //覆盖onReceive方法
    @Override
    public void onReceive(Context context, Intent intent)
    {
        // TODO Auto-generated method stub
        Log.i(TAG, "引发接收事件");
        //先判断广播消息
        String action = intent.getAction();
        if (SMS_RECEIVED_ACTION.equals(action))
        {
            Log.i(TAG, "引发接收事件");
            Toast.makeText(context, "屏蔽一条短信信息", Toast.LENGTH_LONG).show();
            abortBroadcast();
            //获取intent参数
            Bundle bundle=intent.getExtras();

        }
    }

}
package com.example.administrator.endcall;

import android.content.Intent;
import android.content.IntentFilter;

import com.example.jiamiaohe.gamehelper.AutoSMS;
import com.example.jiamiaohe.gamehelper.MyApplication;

/**
 * Created by jiamiaohe on 2017/7/27.
 */

public class MsgUtils {

    private static MsgUtils mMsgUtils = null;

    private AutoSMS mAutoSMS = new AutoSMS();

    private MsgUtils() {
//        IntentFilter filter = new IntentFilter();
//        filter.addAction(Intent.SMS_RECEIVED);
//        MyApplication.getContext().registerReceiver(mAutoSMS)
    }

    public static MsgUtils getInstance() {
        if (mMsgUtils == null) {
            mMsgUtils = new MsgUtils();
        }
        return mMsgUtils;
    }


}

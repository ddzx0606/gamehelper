package com.example.jiamiaohe.gamehelper;

/**
 * Created by jiamiaohe on 2017/7/28.
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class MmsReceiver extends BroadcastReceiver{

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("hm","MmsReceiver: "+intent);
    }

}
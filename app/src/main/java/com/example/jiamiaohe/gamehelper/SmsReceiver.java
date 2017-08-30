package com.example.jiamiaohe.gamehelper;

/**
 * Created by jiamiaohe on 2017/7/28.
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.transition.Slide;
import android.util.Log;
import android.widget.Toast;

public class SmsReceiver extends BroadcastReceiver{
    public static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";
    public SmsReceiver() {
        Log.i("hm", "new SmsReceiver");
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        Log.i("hm", "jie shou dao");
        Cursor cursor = null;
        try {
            if (SMS_RECEIVED.equals(intent.getAction())) {
                Log.d("hm", "sms received!");
                Bundle bundle = intent.getExtras();
                if (bundle != null) {
                    Object[] pdus = (Object[]) bundle.get("pdus");
                    final SmsMessage[] messages = new SmsMessage[pdus.length];
                    for (int i = 0; i < pdus.length; i++) {
                        messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                    }
                    if (messages.length > 0) {
                        String msgBody = messages[0].getMessageBody();
                        String msgAddress = messages[0].getOriginatingAddress();
                        long msgDate = messages[0].getTimestampMillis();
                        String smsToast = "New SMS received from : "
                                + msgAddress + "\n'"
                                + msgBody + "'";
                        Toast.makeText(context, smsToast, Toast.LENGTH_LONG)
                                .show();
                        Log.d("hm", "message from: " + msgAddress + ", message body: " + msgBody
                                + ", message date: " + msgDate);
                    }
                }
                cursor = context.getContentResolver().query(Uri.parse("content://sms"), new String[] { "_id", "address", "read", "body", "date" }, "read = ? ", new String[] { "0" }, "date desc");
                if (null == cursor){
                    return;
                }

                Log.i("hm","m cursor count is "+cursor.getCount());
                Log.i("hm","m first is "+cursor.moveToFirst());


            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("hm", "Exception : " + e);
        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }

    }
}
package com.example.administrator.endcall;

import android.content.Context;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.android.internal.telephony.ITelephony;
import com.example.jiamiaohe.gamehelper.GameHelperService;
import com.example.jiamiaohe.gamehelper.MyApplication;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public final class BlockCallHelper {
    private static final String TAG = "BlockCallHelper";
    private Context mContext;
    private TelephonyManager tManger;
    private List<String> phones;
    private BlockCallBack bcb;

    boolean mTurnOn = false;
    public void changeState(boolean stateOn) {
        mTurnOn = !mTurnOn;
        Toast.makeText(MyApplication.getContext(), "电话拦截 = "+mTurnOn, Toast.LENGTH_SHORT).show();
    }

    public boolean isCallIntercept() {
        return mTurnOn;
    }

    //////////////////////////////////////////
    private static final class Factory {
        private static final BlockCallHelper instance = new BlockCallHelper();
    }
    public static BlockCallHelper getInstance() {
        return Factory.instance;
    }

    /**
    * 初始化上下文以及数据
    * @param context
    */
    public BlockCallHelper init(Context context) {
        if (context == null) {
            throw new NullPointerException("context == null!");
        }

        Log.i("hm", "BlockCallHelper init");
        this.mContext = context;
        this.tManger = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
        tManger.listen(new PhoneCallListener(), PhoneCallListener.LISTEN_CALL_STATE);
        return this;
    }
    /**
    * 注入需要拦截的手机号
    * @param phoneL
    */
    public BlockCallHelper injectBlockPhoneNum(ArrayList<String> blockCalls) {
        this.phones = blockCalls;
        return this;
    }
    /**
    * 结束通话
    */
    private void endCall() {
        try {
            Method method = Class.forName("android.os.ServiceManager").getMethod("getService", String.class);
            // 获取远程TELEPHONY_SERVICE的IBinder对象的代理
            IBinder binder = (IBinder) method.invoke(null, new Object[] { "phone" });
            // 将IBinder对象的代理转换为ITelephony对象
            ITelephony telephony = ITelephony.Stub.asInterface(binder);
            // 挂断电话
            telephony.endCall();
            //telephony.cancelMissedCallsNotification();

        } catch (Exception e) {
            Log.i("BlockCallHelper", "endCall = "+e.toString());
            e.printStackTrace();
        }
    }

    private final class PhoneCallListener extends PhoneStateListener {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            Log.i("BlockCallHelper", "incomingNumber = "+incomingNumber+", state = "+state+", GameHelperService.isForground() = "+GameHelperService.isForground());
            if (GameHelperService.isForground() && mTurnOn && state == TelephonyManager.CALL_STATE_RINGING) {

                endCall();
                Toast.makeText(MyApplication.getContext(), "拦截电话:"+incomingNumber, Toast.LENGTH_SHORT).show();
//                if (phones.contains(incomingNumber)) {
//                    Log.i("BlockCallHelper", "contains contains contains");
//                    endCall();
//                    if (bcb != null) {
//                        bcb.callBack(incomingNumber);
//                    }
//                } else {
//                    endCall();
//                    Log.i("BlockCallHelper", "contains not-------");
//                }
            }
        }
    }
    public BlockCallHelper setBlockCallBack(BlockCallBack back) {
        this.bcb = back;
        return this;
    }

    public interface BlockCallBack {
        void callBack(String incomingNum);
    }
}
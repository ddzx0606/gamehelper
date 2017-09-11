package com.example.jiamiaohe.gamehelper;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.endcall.BlockCallHelper;
import com.wenming.library.BackgroundUtil;

import java.util.ArrayList;

/**
 * Created by jiamiaohe on 2017/8/1.
 */

public class GameHelperService extends Service{

    private static boolean mForground = false;
    LinearLayout mLinearLayout = null;
    ImageView mSmallIcon = null;
    TextView mTextView = null;
    Handler mHandler = new Handler();

    private static GameHelperService mGameHelperService = null;
    public static GameHelperService getInstance() {
        return mGameHelperService;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mGameHelperService = this;

        getNotification();
        //startForeground(1, mNoti);

        Log.i("hm", "GameHelperService onCreate");
        init();

        //Toast.makeText(this, "王者前后台监视已经打开", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(this, TempService.class);
        startService(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mGameHelperService = null;
    }

    private boolean mWindowOn = false;
    public void changeWindowState(final boolean on) {
        if (on) {
            addView();
        } else {
            removeView();
        }
    }

    public boolean getWindowState() {
        return mWindowOn;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void init() {
        ArrayList<String> blockCalls = new ArrayList();
        blockCalls.add("075586013388");
        blockCalls.add("+8613581922339");
        blockCalls.add("18500813370");
        blockCalls.add("13717717622");
        blockCalls.add("+8613717717622");

        BlockCallHelper.getInstance().init(this).injectBlockPhoneNum(blockCalls).setBlockCallBack(new BlockCallHelper.BlockCallBack() {
            @Override
            public void callBack(String incomingNum) {
                Log.i("hm", "incomingNum-----------" + incomingNum);
            }
        });

        new Thread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void run() {

                while (true) {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    judgeForeground(BackgroundUtil.getUsageStats(MyApplication.getContext(), "com.tencent.tmgp.sgame"));
//                    judgeForeground(BackgroundUtil.getLinuxCoreInfo(MyApplication.getContext(), "com.tencent.tmgp.sgame"));
                }

            }
        }).start();
    }

    @Override
    public int onStartCommand(Intent intent,  int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    private void judgeForeground(boolean foreground) {
        Log.i("hm", "judgeForeground = "+foreground+"， mForground = "+mForground);
        if (foreground != mForground) {
            mForground = foreground;

            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    updateUI();
                }
            });
        }
    }

    int mImageIndex = 0;
    int mImageArray[] = {R.drawable.battle_skill, R.drawable.detail_1, R.drawable.detail_2, R.drawable.detail_3, R.drawable.detail_4};
//    Thread myThread = null;
    public void addView() {
        if (mTextView == null) {
            mTextView = new TextView(MyApplication.getContext());
            mTextView.setText("王者处于后台");
//            mTextView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    ScreenShotterUtils.getInstance().startScreenShot(null);
//                }
//            });
        }

        if (mSmallIcon == null) {
            mSmallIcon = new ImageView(MyApplication.getContext());
            mSmallIcon.setImageResource(R.drawable.battle_skill);
            mSmallIcon.setAdjustViewBounds(true);
            mSmallIcon.setMaxWidth(800);
            mSmallIcon.setScaleType(ImageView.ScaleType.FIT_CENTER);
            mSmallIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mImageIndex++;
                    mImageIndex = mImageIndex % mImageArray.length;
                    mSmallIcon.setImageResource(mImageArray[mImageIndex]);
                }
            });
            mSmallIcon.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    ScreenShotterUtils.getInstance().startScreenShot(null);
//                    if (myThread == null) {
//                        myThread = new Thread(new Runnable() {
//                            @Override
//                            public void run() {
//                                while(true) {
//                                    ScreenShotterUtils.getInstance().startScreenShot(null);
//                                    try {
//                                        Thread.sleep(1000);
//                                    } catch (InterruptedException e) {
//                                        e.printStackTrace();
//                                    }
//                                }
//                            }
//                        });
//                        myThread.start();
//                    }
                    return true;
                }
            });
        }

        if (mLinearLayout  == null ) {
            mLinearLayout = new LinearLayout(MyApplication.getContext());
            mLinearLayout.addView(mSmallIcon);
            mLinearLayout.setOrientation(LinearLayout.VERTICAL);
        }

        if (mWindowOn) {
            Log.i("hm", "addView already there mWindowOn = "+mWindowOn);
            return;
        }

        if (!OpUtils.getInstance().isFloatWindowOpAllowed()) {
            Log.i("hm", "addView not allowed");
            return;
        }

        WindowManager windowManager = (WindowManager) this.getSystemService(WINDOW_SERVICE);
        /*
         * LayoutParams.TYPE_SYSTEM_ERROR：保证该悬浮窗所有View的最上层
         * LayoutParams.FLAG_NOT_FOCUSABLE:该浮动窗不会获得焦点，但可以获得拖动
         * PixelFormat.TRANSPARENT：悬浮窗透明
         */
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSPARENT);
        // layoutParams.gravity = Gravity.RIGHT|Gravity.BOTTOM; //悬浮窗开始在右下角显示
        layoutParams.gravity = Gravity.LEFT | Gravity.TOP;
        windowManager.addView(mLinearLayout, layoutParams);

        mWindowOn = true;
    }

    private void removeView() {
        WindowManager windowManager = (WindowManager) this.getSystemService(WINDOW_SERVICE);
        windowManager.removeView(mLinearLayout);
        mWindowOn = false;
    }

    private void updateUI() {
        if (mForground) {
            if (mTextView != null) {
                mTextView.setText("王者处于前台");
            } else {
                Log.i("hm", "updateUI() forground");
                Toast.makeText(getApplicationContext(), "王者处于前台", Toast.LENGTH_SHORT).show();
            }
        } else {
            if (mTextView != null) {
                mTextView.setText("王者处于后台");
            } else {
                Log.i("hm", "updateUI() background");
                Toast.makeText(getApplicationContext(), "王者处于后台", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public static boolean isForground() {
        return mForground;
    }


    static Notification.Builder mBuilder = null;
    static Notification mNoti = null;
    static public int mNotiId = 0;
    public static Notification getNotification() {
        if (mBuilder == null) {
            mBuilder = new Notification.Builder(MyApplication.getContext());

            mBuilder.setContentTitle("前台服务")//设置通知栏标题
                    .setContentText("前台服务")
                    //.setContentIntent(getDefalutIntent(Notification.FLAG_AUTO_CANCEL)) //设置通知栏点击意图
                    //.setNumber(number) //设置通知集合的数量
                    .setTicker("前台服务") //通知首次出现在通知栏，带上升动画效果的
                    .setWhen(System.currentTimeMillis())//通知产生的时间，会在通知信息里显示，一般是系统获取到的时间
                    .setPriority(Notification.PRIORITY_DEFAULT) //设置该通知优先级
//  .setAutoCancel(true)//设置这个标志当用户单击面板就可以让通知将自动取消
                    .setOngoing(false)//ture，设置他为一个正在进行的通知。他们通常是用来表示一个后台任务,用户积极参与(如播放音乐)或以某种方式正在等待,因此占用设备(如一个文件下载,同步操作,主动网络连接)
                    .setDefaults(Notification.DEFAULT_VIBRATE)//向通知添加声音、闪灯和振动效果的最简单、最一致的方式是使用当前的用户默认设置，使用defaults属性，可以组合
                    //Notification.DEFAULT_ALL  Notification.DEFAULT_SOUND 添加声音 // requires VIBRATE permission
                    .setSmallIcon(R.mipmap.ic_launcher);//设置通知小ICON

            mNoti = mBuilder.getNotification();
        }

        return mNoti;
    }
    //添加一个方法作为代理的入口
    public void setImageResource(int index) {
        if (index >= mImageArray.length) return;
        mSmallIcon.setImageResource(mImageArray[index]);
    }
}

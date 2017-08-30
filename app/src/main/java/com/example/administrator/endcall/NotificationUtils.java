package com.example.administrator.endcall;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.example.jiamiaohe.gamehelper.MyApplication;
import com.example.jiamiaohe.gamehelper.R;
import com.wenming.library.BackgroundUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NavigableSet;
import java.util.TreeMap;

//import com.example.jiamiaohe.gamehelper.Re

/**
 * Created by jiamiaohe on 2017/7/27.
 */

public class NotificationUtils {
    private static NotificationUtils mNotificationUtils = null;

    public static NotificationUtils getInstance() {
        if (mNotificationUtils == null) {
            mNotificationUtils = new NotificationUtils();
        }
        return mNotificationUtils;
    }

    private NotificationUtils() {
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.water.noti");
        filter.addAction("com.water.use");
        filter.addAction("com.water.exe");
        filter.addAction("com.water.wangzhe");
        MyApplication.getContext().registerReceiver(mReceiver, filter);
    }

    BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onReceive(Context context, Intent intent) {
        if ("com.water.noti".equals(intent.getAction())) {
            sendNotification();
        } else if ("com.water.use".equals(intent.getAction())) {
            getTopPackageNew(context, "");
        } else if ("com.water.exe".equals(intent.getAction())) {
            try {
                execCommand("ls -l /proc/");
            } catch (IOException e) {
                e.printStackTrace();
                Log.i("hm", ""+e.toString());
            }
        } else if ("com.water.wangzhe".equals(intent.getAction())) {
            Log.i("hm", "wangzhe foreground = " + BackgroundUtil.getLinuxCoreInfo(MyApplication.getContext(), "com.tencent.tmgp.sgame"));
        }
         }
    };

    Notification.Builder mBuilder = null;
    Notification mNoti = null;
    int mNotiId = 0;
    public void sendNotification() {
        if (mBuilder == null) {
            mBuilder = new Notification.Builder(MyApplication.getContext());

            mBuilder.setContentTitle("测试标题")//设置通知栏标题
                .setContentText("测试内容")
                //.setContentIntent(getDefalutIntent(Notification.FLAG_AUTO_CANCEL)) //设置通知栏点击意图
                    //.setNumber(number) //设置通知集合的数量
                .setTicker("测试通知来啦") //通知首次出现在通知栏，带上升动画效果的
                .setWhen(System.currentTimeMillis())//通知产生的时间，会在通知信息里显示，一般是系统获取到的时间
                .setPriority(Notification.PRIORITY_DEFAULT) //设置该通知优先级
//  .setAutoCancel(true)//设置这个标志当用户单击面板就可以让通知将自动取消
                .setOngoing(false)//ture，设置他为一个正在进行的通知。他们通常是用来表示一个后台任务,用户积极参与(如播放音乐)或以某种方式正在等待,因此占用设备(如一个文件下载,同步操作,主动网络连接)
                .setDefaults(Notification.DEFAULT_VIBRATE)//向通知添加声音、闪灯和振动效果的最简单、最一致的方式是使用当前的用户默认设置，使用defaults属性，可以组合
                //Notification.DEFAULT_ALL  Notification.DEFAULT_SOUND 添加声音 // requires VIBRATE permission
                .setSmallIcon(R.mipmap.ic_launcher);//设置通知小ICON

            mNoti = mBuilder.getNotification();
        }

        NotificationManager mNotificationManager = (NotificationManager) MyApplication.getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(mNotiId++, mNoti);
        Log.i("hm", "send notification");
    }


    public void getUsage() {

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private String getTopPackage(Context context) {
        Log.i("hm", "getTopPackage = "+context);
        long ts = System.currentTimeMillis();

        RecentUseComparator mRecentComp = new RecentUseComparator();

        UsageStatsManager mUsageStatsManager =(UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
        //查询ts-10000 到ts这段时间内的UsageStats，由于要设定时间限制，所以有可能获取不到
        List<UsageStats> usageStats =mUsageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_BEST,ts - 10000, ts);

        if (usageStats == null) {
            Log.i("hm", "usageStats == null");
            return "";
        }
        if (usageStats.size() == 0) {
            Log.i("hm", "usageStats.size() == 0");
            return "";
        }
        Collections.sort(usageStats,mRecentComp);
        Log.d("hm", "=size = "+usageStats.size()+", ===usageStats.get(0).getPackageName()"+ usageStats.get(0).getPackageName());
        return usageStats.get(0).getPackageName();
    }

    //改进版本的通过使用量统计功能获取前台应用
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static boolean getTopPackageNew(Context context, String packageName) {
        UsageStatsManager mUsageStatsManager = (UsageStatsManager) context.getApplicationContext().getSystemService(Context.USAGE_STATS_SERVICE);
        long time = System.currentTimeMillis();
        List<UsageStats> stats;
//        if (isFirst) {
            stats = mUsageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, time - 60*60*1000, time);
//        } else {
//            stats = mUsageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, time - THIRTYSECOND, time);
//        }
        // Sort the stats by the last time used
        long start;
        Field mLastEventField = null;
        String topPackageName = null;
        final int TOP_NUM = 3;
        int index = 0;
        if (stats != null) {
            TreeMap<Long, UsageStats> mySortedMap = new TreeMap<Long, UsageStats>();
            start = System.currentTimeMillis();
            for (UsageStats usageStats : stats) {
                mySortedMap.put(usageStats.getLastTimeUsed(), usageStats);
            }
            Log.e("hm", "mySortedMap.size =" + mySortedMap.size() + ",mySortedMap cost:" + (System.currentTimeMillis() - start));
            if (mySortedMap != null && !mySortedMap.isEmpty()) {

                NavigableSet<Long> keySet = mySortedMap.navigableKeySet();
                Iterator iterator = keySet.descendingIterator();
                while (iterator.hasNext()) {
                    UsageStats usageStats = mySortedMap.get(iterator.next());
                    if (mLastEventField == null) {
                        try {
                            mLastEventField = UsageStats.class.getField("mLastEvent");
                        } catch (NoSuchFieldException e) {
                            break;
                        }
                    }
                    if (mLastEventField != null) {
                        int lastEvent = 0;
                        try {
                            lastEvent = mLastEventField.getInt(usageStats);
                        } catch (IllegalAccessException e) {
                            break;
                        }
                        if (lastEvent == 1) {
                            topPackageName = usageStats.getPackageName();

                            if (topPackageName == null) {
                                topPackageName = mySortedMap.get(mySortedMap.lastKey()).getPackageName();
                            }
                            Log.d("hm", "top index = "+index+", name = "+topPackageName+", total runtime = "+usageStats.getTotalTimeInForeground()/1000/60);

                            if (packageName.equals(topPackageName)) {
                                return true;
                            }
//                            if (index > TOP_NUM) {
//                                break;
//                            }
//                            index++;
                        }
                    } else {
                        break;
                    }
                }

            }
        }

        return false;
    }

    public void execCommand(String command) throws IOException {
        // start the ls command running
        //String[] args =  new String[]{"sh", "-c", command};
        Runtime runtime = Runtime.getRuntime();
        Process proc = runtime.exec(command);        //这句话就是shell与高级语言间的调用
        //如果有参数的话可以用另外一个被重载的exec方法
        //实际上这样执行时启动了一个子进程,它没有父进程的控制台
        //也就看不到输出,所以我们需要用输出流来得到shell执行后的输出
        InputStream inputstream = proc.getInputStream();
        InputStreamReader inputstreamreader = new InputStreamReader(inputstream);
        BufferedReader bufferedreader = new BufferedReader(inputstreamreader);
        // read the ls output
        String line = "";
        StringBuilder sb = new StringBuilder(line);
        while ((line = bufferedreader.readLine()) != null) {
            //System.out.println(line);
            sb.append(line);
            sb.append('\n');
        }

        Log.i("hm", "execCommand = "+sb.toString());
        //tv.setText(sb.toString());
        //使用exec执行不会等执行成功以后才返回,它会立即返回
        //所以在某些情况下是很要命的(比如复制文件的时候)
        //使用wairFor()可以等待命令执行完成以后才返回
        try {
            if (proc.waitFor() != 0) {
                System.err.println("exit value = " + proc.exitValue());
            }
        }
        catch (InterruptedException e) {
            System.err.println(e);
        }
    }

}

package com.wenming.library;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.AppOpsManager;
import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.example.administrator.endcall.NotificationUtils;
import com.jaredrummler.android.processes.ProcessManager;
import com.jaredrummler.android.processes.models.AndroidAppProcess;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class BackgroundUtil {
    public static final int BKGMETHOD_GETACCESSIBILITYSERVICE = 4;
    public static final int BKGMETHOD_GETAPPLICATION_VALUE = 2;
    public static final int BKGMETHOD_GETLINUXPROCESS = 5;
    public static final int BKGMETHOD_GETRUNNING_PROCESS = 1;
    public static final int BKGMETHOD_GETRUNNING_TASK = 0;
    public static final int BKGMETHOD_GETUSAGESTATS = 3;

    public static boolean isForeground(Context context, int methodID, String packageName) {
        switch (methodID) {
            case BKGMETHOD_GETRUNNING_TASK /*0*/:
                return getRunningTask(context, packageName);
            case BKGMETHOD_GETRUNNING_PROCESS /*1*/:
                return getRunningAppProcesses(context, packageName);
//            case BKGMETHOD_GETAPPLICATION_VALUE /*2*/:
//                return getApplicationValue(context);
//            case BKGMETHOD_GETUSAGESTATS /*3*/:
//                return queryUsageStats(context, packageName);
//            case BKGMETHOD_GETACCESSIBILITYSERVICE /*4*/:
//                return getFromAccessibilityService(context, packageName);
            case BKGMETHOD_GETLINUXPROCESS /*5*/:
                return getLinuxCoreInfo(context, packageName);
            default:
                return false;
        }
    }

    public static boolean getRunningTask(Context context, String packageName) {
        return !TextUtils.isEmpty(packageName) && packageName.equals(((RunningTaskInfo) ((ActivityManager) context.getSystemService("activity")).getRunningTasks(BKGMETHOD_GETRUNNING_PROCESS).get(BKGMETHOD_GETRUNNING_TASK)).topActivity.getPackageName());
    }

    public static boolean getRunningAppProcesses(Context context, String packageName) {
        List<RunningAppProcessInfo> appProcesses = ((ActivityManager) context.getSystemService("activity")).getRunningAppProcesses();
        if (appProcesses == null) {
            return false;
        }
        for (RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.importance == 100 && appProcess.processName.equals(packageName)) {
                return true;
            }
        }
        return false;
    }

//    public static boolean getApplicationValue(Context context) {
//        return ((MyApplication) ((Service) context).getApplication()).getAppCount() > 0;
//    }

//    @TargetApi(21)
//    public static boolean queryUsageStats(Context context, String packageName) {
//        AnonymousClass1RecentUseComparator mRecentComp = new Comparator<UsageStats>() {
//            public int compare(UsageStats lhs, UsageStats rhs) {
//                if (lhs.getLastTimeUsed() > rhs.getLastTimeUsed()) {
//                    return -1;
//                }
//                return lhs.getLastTimeUsed() == rhs.getLastTimeUsed() ? BackgroundUtil.BKGMETHOD_GETRUNNING_TASK : BackgroundUtil.BKGMETHOD_GETRUNNING_PROCESS;
//            }
//        };
//        long ts = System.currentTimeMillis();
//        List<UsageStats> usageStats = ((UsageStatsManager) context.getSystemService("usagestats")).queryUsageStats(BKGMETHOD_GETACCESSIBILITYSERVICE, ts - 10000, ts);
//        if (usageStats == null || usageStats.size() == 0) {
//            if (!HavaPermissionForTest(context)) {
//                Intent intent = new Intent("android.settings.USAGE_ACCESS_SETTINGS");
//                intent.setFlags(268435456);
//                context.startActivity(intent);
//                Toast.makeText(context, "\u6743\u9650\u4e0d\u591f\n\u8bf7\u6253\u5f00\u624b\u673a\u8bbe\u7f6e\uff0c\u70b9\u51fb\u5b89\u5168-\u9ad8\u7ea7\uff0c\u5728\u6709\u6743\u67e5\u770b\u4f7f\u7528\u60c5\u51b5\u7684\u5e94\u7528\u4e2d\uff0c\u4e3a\u8fd9\u4e2aApp\u6253\u4e0a\u52fe", BKGMETHOD_GETRUNNING_TASK).show();
//            }
//            return false;
//        }
//        Collections.sort(usageStats, mRecentComp);
//        return ((UsageStats) usageStats.get(BKGMETHOD_GETRUNNING_TASK)).getPackageName().equals(packageName);
//    }

    @TargetApi(19)
    private static boolean HavaPermissionForTest(Context context) {
        try {
            ApplicationInfo applicationInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), BKGMETHOD_GETRUNNING_TASK);
            if (((AppOpsManager) context.getSystemService("appops")).checkOpNoThrow("android:get_usage_stats", applicationInfo.uid, applicationInfo.packageName) == 0) {
                return true;
            }
            return false;
        } catch (NameNotFoundException e) {
            return true;
        }
    }

//    public static boolean getFromAccessibilityService(Context context, String packageName) {
//        if (DetectService.isAccessibilitySettingsOn(context)) {
//            String foreground = DetectService.getInstance().getForegroundPackage();
//            Log.d("wenming", "**\u65b9\u6cd5\u4e94** \u5f53\u524d\u7a97\u53e3\u7126\u70b9\u5bf9\u5e94\u7684\u5305\u540d\u4e3a\uff1a =" + foreground);
//            return packageName.equals(foreground);
//        }
//        Intent intent = new Intent("android.settings.ACCESSIBILITY_SETTINGS");
//        intent.setFlags(268435456);
//        context.startActivity(intent);
//        Toast.makeText(context, R.string.accessbiliityNo, BKGMETHOD_GETRUNNING_TASK).show();
//        return false;
//    }

    public static boolean getLinuxCoreInfo(Context context, String packageName) {
        for (AndroidAppProcess appProcess : ProcessManager.getRunningForegroundApps(context)) {
            if (appProcess.getPackageName().equals(packageName) && appProcess.foreground) {
                return true;
            }
        }
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static boolean getUsageStats(Context context, String packageName) {
        return NotificationUtils.getTopPackageNew(context, packageName);
    }
}

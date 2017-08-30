package com.example.jiamiaohe.gamehelper;

import android.Manifest;
import android.app.Activity;
import android.app.AppOpsManager;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * Created by jiamiaohe on 2017/8/3.
 */

public class OpUtils {
    private static OpUtils mOpUtils = null;

    private final String TAG = "hm";

    private int OP_SYSTEM_ALERT_WINDOW = -1;
    private int OP_CALL_PHONE = -1;
    private int OP_ACCESS_NOTIFICATIONS = -1;
    private int OP_POST_NOTIFICATION = -1;

    private OpUtils() {
        Field[] fields = AppOpsManager.class.getDeclaredFields();
        boolean find = false;
        try {
            for (Field field : fields) {
//                if (Modifier.isStatic(field.getModifiers())) {
                find = false;
                if ("OP_SYSTEM_ALERT_WINDOW".equals(field.getName())) {
                    if (!field.isAccessible()) field.setAccessible(true);
                    OP_SYSTEM_ALERT_WINDOW = field.getInt(AppOpsManager.class);
                    find = true;
                } else if ("OP_CALL_PHONE".equals(field.getName())) {
                    if (!field.isAccessible()) field.setAccessible(true);
                    OP_CALL_PHONE = field.getInt(AppOpsManager.class);
                    find = true;
                } else if ("OP_ACCESS_NOTIFICATIONS".equals(field.getName())) {
                    if (!field.isAccessible()) field.setAccessible(true);
                    OP_ACCESS_NOTIFICATIONS = field.getInt(AppOpsManager.class);
                    find = true;
                } else if ("OP_POST_NOTIFICATION".equals(field.getName())) {
                    if (!field.isAccessible()) field.setAccessible(true);
                    OP_POST_NOTIFICATION = field.getInt(AppOpsManager.class);
                    find = true;
                }
                if (find) Log.i(TAG, "name = " + field.getName() + ", value = " + field.getInt(AppOpsManager.class));
//                }

            }
        } catch (Exception e) {
            Log.i(TAG, "OpUtils error = "+e.toString());
            e.printStackTrace();
        }
    }

    public static OpUtils getInstance() {
        if (mOpUtils == null) {
            mOpUtils = new OpUtils();
        }
        return mOpUtils;
    }

    public boolean isFloatWindowOpAllowed() {
        Context context = getContext();
        final int version = Build.VERSION.SDK_INT;
        if (version >= 19) {
            return checkOp(context, OP_SYSTEM_ALERT_WINDOW);  // AppOpsManager.OP_SYSTEM_ALERT_WINDOW
        } else {
            return false;
//            if ((context.getApplicationInfo().flags & 1 << 27) == 1 << 27) {
//                return true;
//            } else {
//                return false;
//            }
        }
    }

    public void gotoFloatWindowOpActivity(Context context) {
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
        context.startActivity(intent);
    }

    public boolean isPhoneCallAllowed() {
        Log.i(TAG, "isPhoneCallAllowed start");
        Context context = getContext();
        final int version = Build.VERSION.SDK_INT;
        if (version >= 19) {
            return checkOp(context, OP_CALL_PHONE);
        } else {
            return false;
//            if ((context.getApplicationInfo().flags & 1 << 27) == 1 << 27) {
//                return true;
//            } else {
//                return false;
//            }
        }
    }

    public boolean isNotificationAccessAllowed() {
        Log.i(TAG, "isNotificationAccessAllowed start");
        boolean enable = false;
        String packageName = MyApplication.getContext().getPackageName();
        String flat= Settings.Secure.getString(MyApplication.getContext().getContentResolver(),"enabled_notification_listeners");
        if (flat != null) {
            enable= flat.contains(packageName);
        }
        Log.i(TAG, "isNotificationAccessAllowed end "+flat);
        return enable;
    }

    public void gotoAppOpActivity(Context context) {
        Intent localIntent = new Intent();
        localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
        localIntent.setData(Uri.fromParts("package", context.getPackageName(), null));
        context.startActivity(localIntent);
    }

    public void gotoAppUseOpActivity(Context context) {
        Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public boolean gotoNotificationAccessSetting(Context context) {
        try {
            Intent intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            return true;
        } catch(ActivityNotFoundException e) {
            try {
                Intent intent = new Intent();
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ComponentName cn = new ComponentName("com.android.settings","com.android.settings.Settings$NotificationAccessSettingsActivity");
                intent.setComponent(cn);
                intent.putExtra(":settings:show_fragment", "NotificationAccessSettings");
                context.startActivity(intent);
                return true;
            } catch(Exception ex) {
                ex.printStackTrace();
            }
            return false;
        }
    }

    private Context getContext() {
        return MyApplication.getContext();
    }

    public boolean checkOp(Context context, int op) {
        final int version = Build.VERSION.SDK_INT;

        if (version >= 19) {
            AppOpsManager manager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
            try {
                Class<?> spClazz = Class.forName(manager.getClass().getName());
                Method method = manager.getClass().getDeclaredMethod("checkOp", int.class, int.class, String.class);
                int property = (Integer) method.invoke(manager, op,
                        Binder.getCallingUid(), context.getPackageName());
                Log.e(TAG, " property: " + property);

                if (AppOpsManager.MODE_ALLOWED == property) {
                    return true;
                } else {
                    return false;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Log.e(TAG, "Below API 19 cannot invoke!");
        }
        return false;
    }

    public boolean isAccessibilitySettingsOn(Context context) {
        int accessibilityEnable = 0;
        String serviceName = AccessService.SERVICE_NAME;
        try {
            accessibilityEnable = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.ACCESSIBILITY_ENABLED, 0);
        } catch (Exception e) {
            Log.e(TAG, "get accessibility enable failed, the err:" + e.getMessage());
        }
        if (accessibilityEnable == 1) {
            TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(':');
            String settingValue = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            Log.i(TAG, "isAccessibilitySettingsOn, settingValue = "+settingValue);
            if (settingValue != null) {
                mStringColonSplitter.setString(settingValue);
                while (mStringColonSplitter.hasNext()) {
                    String accessibilityService = mStringColonSplitter.next();
                    if (accessibilityService.equalsIgnoreCase(serviceName)) {
                        Log.v(TAG, "We've found the correct setting - accessibility is switched on!");
                        return true;
                    }
                }
            }
        }else {
            Log.d(TAG,"Accessibility service disable");
        }
        return false;
    }

    /**
     * 跳转到系统设置页面开启辅助功能
     * @param context：上下文
     */
    public void openAccessibility(Context context){
        Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
        context.startActivity(intent);
    }

    public void requestPhoneCallPermission(Activity context)
    {
        //判断Android版本是否大于23
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            int checkCallPhonePermission = ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE);

            if (checkCallPhonePermission != PackageManager.PERMISSION_GRANTED)
            {
                ActivityCompat.requestPermissions(context, new String[]{Manifest.permission.CALL_PHONE},
                        100);
                return;
            }
        }
    }

    private static final int RECORD_REQUEST_CODE  = 101;
    private static final int STORAGE_REQUEST_CODE = 102;
    private static final int AUDIO_REQUEST_CODE   = 103;
    public void requestScreenRecordPermission(Activity context) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(context, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_REQUEST_CODE);
        }
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(context, new String[]{Manifest.permission.RECORD_AUDIO}, AUDIO_REQUEST_CODE);
        }
    }

    public boolean checkScreenRecordPermission(Context context) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO)
                    != PackageManager.PERMISSION_GRANTED) {
            return false;
        }

        return true;
    }
}

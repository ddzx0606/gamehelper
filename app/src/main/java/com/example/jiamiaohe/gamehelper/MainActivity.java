package com.example.jiamiaohe.gamehelper;

import android.app.ActivityManager;
import android.app.Application;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.provider.Telephony;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.administrator.endcall.BlockCallHelper;
import com.example.administrator.endcall.NotificationUtils;
import com.example.jiamiaohe.gamehelper.bluetooth.Constants;
import com.example.jiamiaohe.gamehelper.bluetooth.DeviceListActivity;
import com.example.jiamiaohe.gamehelper.bluetooth.IconProxy;
import com.example.jiamiaohe.gamehelper.http.HttpUtils;
import com.tutorials.hp.listviewimagessdcard.ImageActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    Context mContext = null;

    private final String TAG = "MainActivity";


    boolean mDisableRecent = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this;
/*
        Button button = (Button)findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDisableRecent = !mDisableRecent;
                Toast.makeText(mContext, "屏蔽Recent按键="+mDisableRecent, Toast.LENGTH_SHORT).show();
            }
        });

        Button button2 = (Button)findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BlockCallHelper.getInstance().changeState(mContext);
            }
        });

        Button button3 = (Button)findViewById(R.id.button3);
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoNotificationAccessSetting(mContext);
            }
        });

        Button button4 = (Button)findViewById(R.id.button4);
        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeDefaultSms();
            }
        });

        Button button5 = (Button)findViewById(R.id.button5);
        button5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NotificationUtils.getInstance().sendNotification();
            }
        });

        Button button6 = (Button)findViewById(R.id.button6);
        button6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        Button button7 = (Button)findViewById(R.id.button7);
        button7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (false) {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                            Uri.parse("package:" + getPackageName()));
                    startActivityForResult(intent, 10);
                } else {
                    Log.i("hm", "direct start");
                    startService(new Intent(getApplicationContext(), GameHelperService.class));
                }
            }
        });

        Button button8 = (Button)findViewById(R.id.button8);
        button8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setComponent(new ComponentName("com.tencent.tmgp.sgame", "com.tencent.tmgp.sgame.SGameActivity"));
                getApplicationContext().startActivity(intent);
            }
        });

        Button button9 = (Button)findViewById(R.id.button9);
        button9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                startActivityForResult(intent, 10);
            }
        });
        */

        Button button8 = (Button)findViewById(R.id.button8);
        button8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("qnreading://tab_reading?from=yingyongbao, apkInfo.mPackageName = com.tencent.reading"));
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(intent);

                Intent intent = new Intent();
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setComponent(new ComponentName("com.tencent.tmgp.sgame", "com.tencent.tmgp.sgame.SGameActivity"));
                getApplicationContext().startActivity(intent);
            }
        });

        final ToggleButton button21 = (ToggleButton)findViewById(R.id.button21);
        GameHelperService service = GameHelperService.getInstance();
        button21.setChecked((service == null) ? false : service.getWindowState());
        button21.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                GameHelperService service = GameHelperService.getInstance();
                if (service != null) {
                    service.changeWindowState(b);
                }
                button21.setChecked(service.getWindowState());
            }
        });

        ToggleButton button2 = (ToggleButton)findViewById(R.id.button2);
        button2.setChecked(BlockCallHelper.getInstance().isCallIntercept());
        button2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Log.i("hm", "button2 is click b = "+b);
                if (b) {
                    if (!OpUtils.getInstance().isPhoneCallAllowed()) {
                        OpUtils.getInstance().gotoAppOpActivity(mContext);
                    }
                }
                BlockCallHelper.getInstance().changeState(b);
            }
        });

        ToggleButton button3 = (ToggleButton)findViewById(R.id.button3);
        button3.setChecked(NotificationListener.mInterceptNotification);
        button3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    if (!OpUtils.getInstance().isNotificationAccessAllowed()) {
                        OpUtils.getInstance().gotoNotificationAccessSetting(mContext);
                    }
                }
                NotificationListener.mInterceptNotification = b;
            }
        });

        ScreenShotterUtils.getInstance().canNotStartUtilsInit();
        ToggleButton button22 = (ToggleButton)findViewById(R.id.button22);
        button22.setChecked(ScreenShotterUtils.getInstance().getShotterEnable());
        button22.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                ScreenShotterUtils.getInstance().setSetShotterEnable(b);
            }
        });
        requestScreenShot();

        ActivityCompat.requestPermissions(MainActivity.this, new String[]{android
                .Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
    }



    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        Log.i("hm", "onWindowFocusChanged = "+hasFocus+", mDisableRecent = "+mDisableRecent);
        /*
        if (!hasFocus) {
            sendBroadcast(new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));
        }
        */
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i("hm", "onPause = "+mDisableRecent);
        if (mDisableRecent) {
            ActivityManager activityManager = (ActivityManager) getApplicationContext()
                    .getSystemService(Context.ACTIVITY_SERVICE);

            activityManager.moveTaskToFront(getTaskId(), 0);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private boolean notificationListenerEnable() {
        boolean enable = false;
        String packageName = getPackageName();
        String flat= Settings.Secure.getString(getContentResolver(),"enabled_notification_listeners");
        if (flat != null) {
            enable= flat.contains(packageName);
        }
        return enable;
    }

    private void changeDefaultSms() {
        Log.i("hm", "changeDefaultSms = "+getPackageName());
        Intent intent = new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
        intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, getPackageName());
        startActivity(intent);
    }

    public static final int REQUEST_MEDIA_PROJECTION = 10387;
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.i(TAG, "onActivityResult requestCode = "+requestCode+", resultCode = "+resultCode+", data = "+data);
        if (requestCode == 10) {
            if (Settings.canDrawOverlays(this)) {
                startService(new Intent(this, GameHelperService.class));
            }
        } else if (requestCode == REQUEST_MEDIA_PROJECTION) {
            if (resultCode == -1 && data != null) {
                ScreenShotterUtils.getInstance().init(MyApplication.getContext(), data);
            }
        }
        // 建立连接，让IconProxy操作，减少耦合
        if (iconProxy != null) {
            iconProxy.onProxyActivityResult(MainActivity.this, requestCode, resultCode, data);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.NONE, Menu.FIRST, 1, "悬浮窗权限");
        menu.add(Menu.NONE, Menu.FIRST+1, 1+1, "权限列表");
        menu.add(Menu.NONE, Menu.FIRST+2, 1+2, "通知使用权");
        menu.add(Menu.NONE, Menu.FIRST+3, 1+3, "APP用量");
        menu.add(Menu.NONE, Menu.FIRST+4, 1+4, "配置所有权限");
        menu.add(Menu.NONE, Menu.FIRST+5, 1+5, "发送通知");
        menu.add(Menu.NONE, Menu.FIRST+6, 1+6, "申请电话权限");
        menu.add(Menu.NONE, Menu.FIRST+7, 1+7, "更改默认短信");
        menu.add(Menu.NONE, Menu.FIRST+8, 1+8, "截图");
        menu.add(Menu.NONE, Menu.FIRST+9, 1+9, "查看截图");

        /*
         * 新增两个入口
         * vimerzhao
         */
        menu.add(Menu.NONE, Menu.FIRST+10, 1+10, "建立蓝牙连接");
        menu.add(Menu.NONE, Menu.FIRST+11, 1+11, "开启设备可见性");
        //hjm add
        menu.add(Menu.NONE, Menu.FIRST+12, 1+12, "发送网络请求");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.i("hm", "onOptionsItemSelected = "+item.getItemId());
        switch (item.getItemId()) {
            case Menu.FIRST: {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivity(intent);
                break;
            }
            case Menu.FIRST+1:
                OpUtils.getInstance().gotoAppOpActivity(mContext);
                break;
            case Menu.FIRST+2:
                OpUtils.getInstance().gotoNotificationAccessSetting(mContext);
                break;
            case Menu.FIRST+3:
                OpUtils.getInstance().gotoAppUseOpActivity(mContext);
                break;
            case Menu.FIRST+4:
                OpUtils.getInstance().openAccessibility(mContext);
                break;
            case Menu.FIRST+5:
                NotificationUtils.getInstance().sendNotification();
                break;
            case Menu.FIRST+6:
                OpUtils.getInstance().requestPhoneCallPermission(this);
            case Menu.FIRST+7:
                changeDefaultSms();
                break;
            case Menu.FIRST+8:
                ScreenShotterUtils.getInstance().startScreenShot(null);
                break;
            case Menu.FIRST+9: {
                Intent intent = new Intent(this, ImageActivity.class);
                startActivity(intent);
                break;
            }
            // 添加入口
            case Menu.FIRST+10: {
                Intent serverIntent = new Intent(this, DeviceListActivity.class);
                startActivityForResult(serverIntent, Constants.REQUEST_CONNECT_DEVICE_SECURE);
                return true;
            }
            case Menu.FIRST+11: {
                iconProxy = IconProxy.getInstance();
                iconProxy.ensureDiscoverable(this);
            }
            case Menu.FIRST+12: {
                HttpUtils.getInstance().requestInThread();
                break;
            }
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    private IconProxy iconProxy;
    public void requestScreenShot() {
        if (Build.VERSION.SDK_INT >= 21) {
            startActivityForResult(
                    ((MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE)).createScreenCaptureIntent(),
                    REQUEST_MEDIA_PROJECTION
            );
        }
        else
        {
            Log.i(TAG, "版本过低,无法截屏");
        }
    }
}

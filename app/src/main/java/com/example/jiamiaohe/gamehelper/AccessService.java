package com.example.jiamiaohe.gamehelper;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import java.util.List;

/**
 * Created by jiamiaohe on 2017/8/3.
 */

public class AccessService extends AccessibilityService {

    private final String TAG = "AccessService";
    public static String SERVICE_NAME = "com.example.jiamiaohe.gamehelper/com.example.jiamiaohe.gamehelper.AccessService";

    String mForegroundPackageName = null;
    String mCalssName = null;
    YYBAbstractAccessibilityManager mYYBAbstractAccessibilityManager = new YYBAbstractAccessibilityManager();

    private int mAutoGetOpState = OP_STATE_NONE;
    private static final int OP_STATE_NONE = -1;
    private static final int OP_STATE_AERTWINDOW = 0;
    private static final int OP_STATE_SHOW_COVER = 1;
    private static final int OP_STATE_GO_USAGE_DETAIL = 2;
    private static final int OP_STATE_USAGE = 3;
    private static final int OP_STATE_BACK = 4;

    private static AccessService mAccessService = null;

    public static AccessService get() {
        return mAccessService;
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED
                || event.getEventType() == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED) {
            mForegroundPackageName = event.getPackageName().toString();
            mCalssName = event.getClassName().toString();

            Log.i(TAG, "AccessService onAccessibilityEvent = " + mForegroundPackageName + ", className = "+event.getClassName()+", action = "+event.getAction());

            AccessibilityNodeInfo noteInfo = getRootInActiveWindow();
            if ("com.android.settings.Settings$AppDrawOverlaySettingsActivity".equals(mCalssName)
                    && mForegroundPackageName.equals("com.android.settings")) {
                getAlertOp();
                getNext();
            } else if (("com.android.settings.Settings$UsageAccessSettingsActivity".equals(mCalssName) ||
                    //content refresh
                    "android.widget.ListView".equals(mCalssName))
                    && mForegroundPackageName.equals("com.android.settings")) {
                gotoUsageDetail();
            } else if ("com.android.settings.SubSettings".equals(mCalssName)
                    && mForegroundPackageName.equals("com.android.settings")) {
                getUsageOp();
            }
        }
    }

    @Override
    public void onInterrupt() {
        Log.i(TAG, "AccessService onInterrupt");
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();

        mAccessService = this;

        Log.i(TAG, "AccessService onServiceConnected");
        AccessibilityServiceInfo serviceInfo = new AccessibilityServiceInfo();
        serviceInfo.eventTypes = AccessibilityEvent.TYPES_ALL_MASK;
        serviceInfo.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;
        serviceInfo.packageNames = new String[]{"com.android.settings"};
        serviceInfo.notificationTimeout=100;
        setServiceInfo(serviceInfo);

        getNext();
    }

    @Override
    public void unbindService(ServiceConnection conn) {
        super.unbindService(conn);

        mAccessService = null;
        Log.i(TAG, "AccessService unbindService");
    }

    private LinearLayout mLinearLayout = null;
    private boolean mCoverShown = false;
    private void showCoverWindow() {
        if (mAutoGetOpState != (OP_STATE_SHOW_COVER-1)) {
            Log.i(TAG, "showCoverWindow return = "+mAutoGetOpState);
            return;
        }

        Log.i(TAG, "start showCoverWindow = "+mAutoGetOpState);
        setOpState(OP_STATE_SHOW_COVER);
//
//        if (mLinearLayout == null) {
//            mLinearLayout = new LinearLayout(this);
//            TextView textView = new TextView(this);
//            textView.setText("遮盖层");
//            textView.setTextSize(30);
//            mLinearLayout.addView(textView, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
//                    LinearLayout.LayoutParams.MATCH_PARENT));
//        }
//
//        if (!mCoverShown) {
//            WindowManager windowManager = (WindowManager) this.getSystemService(WINDOW_SERVICE);
//            /*
//             * LayoutParams.TYPE_SYSTEM_ERROR：保证该悬浮窗所有View的最上层
//             * LayoutParams.FLAG_NOT_FOCUSABLE:该浮动窗不会获得焦点，但可以获得拖动
//             * PixelFormat.TRANSPARENT：悬浮窗透明
//             */
//            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(WindowManager.LayoutParams.MATCH_PARENT,
//                    LinearLayout.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
//                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSPARENT);
//            // layoutParams.gravity = Gravity.RIGHT|Gravity.BOTTOM; //悬浮窗开始在右下角显示
//            layoutParams.gravity = Gravity.LEFT | Gravity.TOP;
//
//            windowManager.addView(mLinearLayout, layoutParams);
//        }

        getNext();
    }

    private void setOpState(int state) {
        Log.i(TAG, "setOpState = "+state, new RuntimeException());
        mAutoGetOpState = state;
    }

    private void getAlertOp() {
        if (mAutoGetOpState != (OP_STATE_AERTWINDOW-1)) {
            Log.i(TAG, "getAlertOp return = "+mAutoGetOpState);
            return;
        }

        setOpState(OP_STATE_AERTWINDOW);

        Log.i(TAG, "start getAlertOp = "+mAutoGetOpState);

        AccessibilityNodeInfo noteInfo = getRootInActiveWindow();
        mYYBAbstractAccessibilityManager.traversalNode(noteInfo, 0);
        AccessibilityNodeInfo text = mYYBAbstractAccessibilityManager.findByTypeWithTxt(TextView.class.getName(), "允许在其他应用",  noteInfo);
        Log.i(TAG, "AccessibilityNodeInfo text == "+text.getClassName());
        AccessibilityNodeInfo container = null;
        try {
            container = text.getParent();
        } catch (Exception e){
            Log.e(TAG, "error getparent "+e.toString());
        }
        Log.i(TAG, "AccessibilityNodeInfo container == "+container.getClassName());
        if (container != null) {
            AccessibilityNodeInfo switchNode = mYYBAbstractAccessibilityManager.findByType(Switch.class.getName(), noteInfo);
            if (switchNode != null) {
                if (switchNode.isChecked()) {
                    Log.i(TAG, "window op is opened");
                } else {
                    Log.i(TAG, "window op start to open");
                    container.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    container.recycle();
                }
            }
        }
    }

    public void getUsageOp() {
        if (mAutoGetOpState != (OP_STATE_USAGE-1)) {
            Log.i(TAG, "getUsageOp return mAutoGetOpState = "+mAutoGetOpState);
            return;
        }
        Log.i(TAG, "start getUsageOp = "+mAutoGetOpState);

        setOpState(OP_STATE_USAGE);

        AccessibilityNodeInfo noteInfo = getRootInActiveWindow();
        mYYBAbstractAccessibilityManager.traversalNode(noteInfo, 0);
        AccessibilityNodeInfo text = mYYBAbstractAccessibilityManager.findByTypeWithTxt(TextView.class.getName(), "允许访问使用记录",  noteInfo);
        AccessibilityNodeInfo container = null;
        try {
            Log.i(TAG, "getUsageOp text == "+text.getClassName());
            container = text.getParent();
        } catch (Exception e){
            Log.e(TAG, "getUsageOp error getparent "+e.toString());
        }
        if (container != null) {
            Log.i(TAG, "getUsageOp container == "+container.getClassName());
            AccessibilityNodeInfo switchNode = mYYBAbstractAccessibilityManager.findByType(Switch.class.getName(), noteInfo);
            if (switchNode != null) {
                if (switchNode.isChecked()) {
                    Log.i(TAG, "getUsageOp is opened");
                } else {
                    Log.i(TAG, "getUsageOp start to open");
                    container.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    container.recycle();
                }
            }
        }

        getNext();
    }

    public void getNext() {
        if (mAutoGetOpState == OP_STATE_NONE) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else if (mAutoGetOpState == OP_STATE_AERTWINDOW) {
            showCoverWindow();
        } else if (mAutoGetOpState == OP_STATE_SHOW_COVER) {
            OpUtils.getInstance().gotoAppUseOpActivity(this);
        } else if (mAutoGetOpState == OP_STATE_GO_USAGE_DETAIL) {
            getUsageOp();
        } else if (mAutoGetOpState == OP_STATE_USAGE) {
            goBack();
        }
    }

    public void gotoUsageDetail() {
        if (mAutoGetOpState != (OP_STATE_GO_USAGE_DETAIL-1)) {
            Log.i(TAG, "gotoUsageDetail return mAutoGetOpState = "+mAutoGetOpState);
            return;
        }
        Log.i(TAG, "start gotoUsageDetail = "+mAutoGetOpState);

        AccessibilityNodeInfo noteInfo = getRootInActiveWindow();
        mYYBAbstractAccessibilityManager.traversalNode(noteInfo, 0);
        AccessibilityNodeInfo itemHelper = mYYBAbstractAccessibilityManager.findByTypeWithTxt(TextView.class.getName(), "GameHelper",  noteInfo);
        if (itemHelper != null && itemHelper.getParent() != null) {
            Log.i(TAG, "gotoUsageDetail itemHelper name = "+itemHelper.getClassName()+", text = "+itemHelper.getText()+", clickable = "+itemHelper.getParent().isClickable());
            if (itemHelper.getParent().isClickable()) {
                itemHelper.getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
                //itemHelper.getParent().recycle();

                //this listview will refresh, so we will goto next untils success
                setOpState(OP_STATE_GO_USAGE_DETAIL);
            }
        }
    }

    public void goBack() {
        Log.i(TAG, "goBack()");
        if (mAutoGetOpState != (OP_STATE_BACK-1)) {
            Log.i(TAG, "goBack return mAutoGetOpState = "+mAutoGetOpState);
            return;
        }

        setOpState(OP_STATE_BACK);

        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}

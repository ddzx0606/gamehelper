package com.example.jiamiaohe.gamehelper;

import android.annotation.TargetApi;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by xinghzhang on 2016/4/26.
 * 应用宝辅助功能Manager的基类
 */
public class YYBAbstractAccessibilityManager {

    private static final String TAG = "hm-accessibility";
    private static final String C_TAG = "<" + YYBAbstractAccessibilityManager.class.getSimpleName() + "> ";

    /**
     * 遍历界面所有控件
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void traverseNodeText(AccessibilityNodeInfo node, Set<String> result, boolean clickable) {
        if (null == node) {
            return;
        }

        final int count = node.getChildCount();
        if (count > 0) {
            for (int i = 0; i < count; i++) {
                AccessibilityNodeInfo childNode = node.getChild(i);
                traverseNodeText(childNode, result, clickable);
            }
        } else {
            if (isButton(node) || isTextView(node)) {
                if (clickable && (!node.isClickable() || !node.isEnabled() || !node.isVisibleToUser())) {
                    return;
                }
                CharSequence text = node.getText();
                if (!TextUtils.isEmpty(text)) {
                    result.add(text.toString().trim());
                }
            }
        }
    }

    /**
     * 遍历界面中Button和TextView所有的文案
     */
    public Set<String> traverseNodeText(AccessibilityNodeInfo node) {
        Set<String> result = new HashSet<String>();
        traverseNodeText(node, result, false);
        return result;
    }

    /**
     * 遍历界面中可点击状态Button和TextView所有的文案
     */
    public Set<String> traverseClickableNodeText(AccessibilityNodeInfo node) {
        Set<String> result = new HashSet<String>();
        traverseNodeText(node, result, true);
        return result;
    }

    /**
     * 根据控件的id执行点击动作
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public synchronized boolean findByIdAndPerformClick(String id, AccessibilityNodeInfo source) {
        boolean ret = false;
        if (source != null) {
            List<AccessibilityNodeInfo> nodeInfos = source.findAccessibilityNodeInfosByViewId(id);
            if (nodeInfos != null && !nodeInfos.isEmpty()) {
                for (AccessibilityNodeInfo accessibilityNodeInfo : nodeInfos) {
                    if (accessibilityNodeInfo.isClickable()) {
                        if (performActionClick(accessibilityNodeInfo)) {
                            ret = true;
                        }
                    }
                }
            }
        }
        return ret;
    }

    /**
     * 对text字段的值进行模糊匹配，即控件的文案中只要包含text的值就认为匹配成功
     */
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public synchronized boolean findByTxtAndPerformClick(String text, AccessibilityNodeInfo source, boolean exactlyClick) {
        boolean ret = false;
        if (source != null && !TextUtils.isEmpty(text)) {
            try {
                List<AccessibilityNodeInfo> nodes = source.findAccessibilityNodeInfosByText(text);
                if (nodes != null && !nodes.isEmpty()) {

                    List<AccessibilityNodeInfo> clickInfos = new ArrayList<AccessibilityNodeInfo>();
                    if (exactlyClick) {
                        for (AccessibilityNodeInfo nodeInfo : nodes) {
                            if (nodeInfo.getText().toString().trim().equals(text)) {
                                clickInfos.add(nodeInfo);
                            }
                        }
                    } else {
                        clickInfos = nodes;
                    }

                    AccessibilityNodeInfo node;
                    for (int i = 0; i < clickInfos.size(); i++) {
                        node = clickInfos.get(i);
                        if (node != null) {
                            if (performActionClick(node)) {
                                    Log.i(TAG, C_TAG + "findByTxtAndPerformClick, source.pkg : " + source.getPackageName() + ", text : " + text + ", exactlyClick : " + exactlyClick);
                                ret = true;
                            }
                            node.recycle();
                        }
                    }
                }
            } catch (Throwable t) {
                Log.e(TAG, C_TAG + "findByTxtAndPerformClick", t);
            }
        }
        return ret;
    }

    public static final int ERROR_NOT_CLICKABLE = -1;
    public static final int ERROR_NOT_VISIBLE = -2;
    public static final int ERROR_CLICK_ERROR = -3;
    public static final int ERROR_NO_FIND_NODE = -4;
    public static final int ERROR_TEXT_EMPTY = -5;
    public static final int ERROR_EXCEPTIOON = -6;

    /**对text字段的值进行模糊匹配，即控件的文案中只要包含text的值就认为匹配成功---返回错误码
     * */
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public synchronized int findByTxtAndPerformClickRtnErrorCode(String text, AccessibilityNodeInfo source, boolean exactlyClick) {
        boolean ret = false;
        int errorCode = 0;
        if (source != null && !TextUtils.isEmpty(text)) {
            try {
                List<AccessibilityNodeInfo> nodes = source.findAccessibilityNodeInfosByText(text);
                Log.d(TAG,"findByTxtAndPerformClick---text = "+text+" nodes = "+nodes);
                if (nodes != null && !nodes.isEmpty()) {

                    List<AccessibilityNodeInfo> clickInfos = new ArrayList<AccessibilityNodeInfo>();
                    if (exactlyClick) {
                        for (AccessibilityNodeInfo nodeInfo : nodes) {
//                            XLog.d(TAG,"-----------nodeInfo.getText().toString() = "+nodeInfo.getText().toString());
                            if (nodeInfo.getText().toString().trim().equals(text)) {
                                clickInfos.add(nodeInfo);
                            }
                        }
                    } else {
                        clickInfos = nodes;
                    }

                    AccessibilityNodeInfo node;
                    if(clickInfos.size() > 0) {
                        for (int i = 0; i < clickInfos.size(); i++) {
                            node = clickInfos.get(i);
                            if (node != null) {
                                errorCode = performActionClickRtnErrorCode(node);
                                node.recycle();
                                if (errorCode == 0) {
                                    break;
                                }
                            }
                        }
                    } else {
                        errorCode = ERROR_NO_FIND_NODE;
                    }
                } else {
                    errorCode = ERROR_NO_FIND_NODE;
                }
            } catch (Throwable t) {
                Log.i(TAG, "findByTxtAndPerformClick" + t.toString());
                errorCode = ERROR_EXCEPTIOON;
            }
        } else {
            errorCode = ERROR_TEXT_EMPTY;
        }
        if (errorCode != 0) {
//            if (EnhanceAccelerateManager.IS_ZONGCE) {
//                XLog.writeToFile("findByTxtAndPerformClick---text = "+text+", errorCode = "+errorCode,EnhanceAccelerateManager.ZONGCE_LOG_FILE,true);
//            }
        }
        return errorCode;
    }


    boolean typeClickRet = false;
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private boolean findByTypeAndPerformClick(final String className, final AccessibilityNodeInfo nodeInfo, boolean isFirst) {
        if (isFirst) {
            typeClickRet = false;
        }
        if (nodeInfo != null && !TextUtils.isEmpty(className)) {
            Log.i("hm", "findByTypeAndPerformClick className = "+className);
            if (nodeInfo.getClassName().equals(className)) {
                if (performActionClick(nodeInfo)) {
                    typeClickRet = true;
                }
            } else {
                final int count = nodeInfo.getChildCount();
                if (count > 0) {
                    for (int i = 0; i < count; i++) {
                        if (findByTypeAndPerformClick(className, nodeInfo.getChild(i), false)) {
                            typeClickRet = true;
                        }
                    }
                }
            }
        }
        return typeClickRet;
    }



    /**获取某类型控件上的文案*/
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public void findByTypeGetText(ArrayList<String> textList,final String className, final AccessibilityNodeInfo nodeInfo) {
        if (textList == null) {
            return;
        }

        if (nodeInfo != null && !TextUtils.isEmpty(className)) {
            Log.d(TAG,"findByTypeGetText --- nodeInfo.getClassName() = "+nodeInfo.getClassName()+"nodeInfo.getText() = "+nodeInfo.getText());
            if (nodeInfo.getClassName().equals(className)) {
                CharSequence charSequence = nodeInfo.getText();
                String text = charSequence != null ? charSequence.toString() : "";
                textList.add(text);

            } else {
                final int count = nodeInfo.getChildCount();
                if (count > 0) {
                    for (int i = 0; i < count; i++) {
                        findByTypeGetText(textList,className,nodeInfo.getChild(i));
                    }
                }
            }
        }
        return ;
    }

    /**通过type与text一起来查找控件*/
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private boolean findByTypeAndTextAndPerformClick(final String className, final String text,final AccessibilityNodeInfo nodeInfo, boolean isFirst) {
        if (isFirst) {
            typeClickRet = false;
        }
        if (nodeInfo != null && !TextUtils.isEmpty(className)) {
            if (nodeInfo.getClassName().equals(className) && !TextUtils.isEmpty(text) && text.equals(nodeInfo.getText())) {
                if (performActionClick(nodeInfo)) {
                    typeClickRet = true;
                }
            } else {
                final int count = nodeInfo.getChildCount();
                if (count > 0) {
                    for (int i = 0; i < count; i++) {
                        if (findByTypeAndTextAndPerformClick(className, text,nodeInfo.getChild(i), false)) {
                            typeClickRet = true;
                        }
                    }
                }
            }
        }
        return typeClickRet;
    }


    /**
     * 根据控件类型进行点击
     *
     * @param className 控件类名
     */
    public boolean findByTypeAndPerformClick(final String className, final AccessibilityNodeInfo nodeInfo) {
        return findByTypeAndPerformClick(className, nodeInfo, true);
    }


    /**
     * 根据控件类型及其或其子控件所包含文案进行点击匹配，这里会依次匹配多个文案，只要有一个匹配即可，否则就匹配失败
     *
     * @param className 控件类名
     * @param texts     匹配的文案数组
     */
    public boolean findByTypeWithTxtAndPerformClick(final String className, final String[] texts, final AccessibilityNodeInfo nodeInfo ) {
        if (texts != null && texts.length > 0) {
            for (String text : texts) {
                if (findByTypeWithTxtAndPerformClick(className, text, nodeInfo)) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    /**
     * 根据控件类型及其或其子控件所包含文案进行点击
     *
     * @param className 控件类名
     * @param text      匹配的文案
     */
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public boolean findByTypeWithTxtAndPerformClick(final String className, final String text, final AccessibilityNodeInfo nodeInfo) {
        if (nodeInfo != null && !TextUtils.isEmpty(className)) {
            if (nodeInfo.getClassName().equals(className)) {
                Set<String> set = traverseNodeText(nodeInfo);
                if (!set.isEmpty() && set.contains(text)) {
                    return performActionClick(nodeInfo);
                }
            } else {
                final int count = nodeInfo.getChildCount();
                if (count > 0) {
                    for (int i = 0; i < count; i++) {
                        if (findByTypeWithTxtAndPerformClick(className, text, nodeInfo.getChild(i))) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public void traversalNode(AccessibilityNodeInfo node, int index) {
        int childSize = node.getChildCount();
        if (childSize > 0) {
            Log.i("hm", "traversalNode index = "+index+", class = "+node.getClassName()+", text = "+node.getText());
            for(int i = 0; i < childSize; i++) {
                traversalNode(node.getChild(i), index+1);
            }
        } else {
            Log.i("hm", "traversalNode index = "+index+", class = "+node.getClassName()+", text = "+node.getText());
        }
    }

    public AccessibilityNodeInfo findByTypeWithTxt(final String className, final String text, final AccessibilityNodeInfo nodeInfo) {
        AccessibilityNodeInfo result = null;

        if (nodeInfo != null && !TextUtils.isEmpty(className)) {
            Log.i("hm", "findByTypeWithTxt node name = "+nodeInfo.getClassName()+", getText = "+nodeInfo.getText());
            boolean textMatch = false;
            if (nodeInfo.getText() != null) {
                textMatch = nodeInfo.getText().toString().contains(text);
            }
            if (nodeInfo.getClassName().equals(className) && textMatch) {
                return nodeInfo;
            } else {
                int count = nodeInfo.getChildCount();
                if (count > 0){
                    for (int i = 0; i < count; i++) {
                        result = findByTypeWithTxt(className, text, nodeInfo.getChild(i));
                        if (result != null) {
                            return result;
                        }
                    }
                }
            }
        }
        return result;
    }

    public AccessibilityNodeInfo findByType(final String className, final AccessibilityNodeInfo nodeInfo) {
        AccessibilityNodeInfo result = null;

        if (nodeInfo != null && !TextUtils.isEmpty(className)) {
            Log.i("hm", "findByType node name = "+nodeInfo.getClassName()+", getText = "+nodeInfo.getText());
            if (nodeInfo.getClassName().equals(className)) {
                return nodeInfo;
            } else {
                int count = nodeInfo.getChildCount();
                if (count > 0){
                    for (int i = 0; i < count; i++) {
                        result = findByType(className, nodeInfo.getChild(i));
                        if (result != null) {
                            return result;
                        }
                    }
                }
            }
        }
        return result;
    }

    /**
     * 全局点击事件
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public boolean performGlobalAction(int action) {
        AccessService service = AccessService.get();
        if (service != null) {
            return service.performGlobalAction(action);
        }
        return false;
    }

    /**
     * 对界面节点执行click操作
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private boolean performActionClick(final AccessibilityNodeInfo node) {
        int errorCode = performActionClickRtnErrorCode(node);
        return (errorCode == 0);
    }


    /**返回错误码*/
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private int performActionClickRtnErrorCode(final AccessibilityNodeInfo node) {
        if (node == null) {
            return ERROR_EXCEPTIOON;
        }else if (node.isClickable() && node.isVisibleToUser()) {
            if (node.performAction(AccessibilityNodeInfo.ACTION_CLICK)) {
                return 0;
            }
            return ERROR_CLICK_ERROR;
        } else if (!node.isClickable()) {
            return ERROR_NOT_CLICKABLE;
        } else if (!node.isVisibleToUser()) {
            return ERROR_NOT_VISIBLE;
        } else {
            return ERROR_NO_FIND_NODE;
        }
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public boolean isButton(AccessibilityNodeInfo node) {
        return node.getClassName().equals("android.widget.Button") && node.isEnabled();
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public boolean isTextView(AccessibilityNodeInfo node) {
        return node.getClassName().equals("android.widget.TextView") && node.isEnabled();
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public boolean isView(AccessibilityNodeInfo node) {
        return node.getClassName().equals("android.widget.View") && node.isEnabled();
    }

    public void handleEvent(AccessibilityEvent event, AccessibilityNodeInfo rootInActiveWindow) {

    }
}

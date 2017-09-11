package com.example.jiamiaohe.gamehelper.bluetooth;


import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.example.jiamiaohe.gamehelper.GameHelperService;

import java.util.regex.Pattern;

/**
 * @author vimerzhao
 * 代理悬浮窗和蓝牙进行通信，防止代码耦合在一起。
 */
public class IconProxy {
    private static IconProxy iconProxy;
    private BluetoothChatService mChatService;
    private BluetoothAdapter mBluetoothAdapter;
    public static IconProxy getInstance() {
        if (iconProxy == null) {
            iconProxy = new IconProxy();
            return iconProxy;
        }
        return iconProxy;
    }
    private IconProxy() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }


    public void ensureDiscoverable(Context context) {
        if (mBluetoothAdapter.getScanMode() !=
                BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            context.startActivity(discoverableIntent);
        }
    }

    public void onProxyActivityResult(Context context,int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case Constants.REQUEST_CONNECT_DEVICE_SECURE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    connectDevice(context, data, true);
                }
                break;
            case Constants.REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    // Bluetooth is now enabled, so set up a chat session
                    mChatService = new BluetoothChatService(context, mHandler);
                } else {
                    // User did not enable Bluetooth or an error occurred
                    Toast.makeText(context, "蓝牙开启失败！",
                            Toast.LENGTH_SHORT).show();
                }
        }
    }

    private void connectDevice(Context context, Intent data, boolean secure) {
        // Get the device MAC address
        String address = data.getExtras()
                .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
        // Get the BluetoothDevice object
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        // Attempt to connect to the device
        if (mChatService == null) {
            mChatService = new BluetoothChatService(context, mHandler);
        }
        mChatService.connect(device, secure);
    }

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constants.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothChatService.STATE_CONNECTED:
                            Log.d(TAG, "连接成功");
                            break;
                        case BluetoothChatService.STATE_CONNECTING:
                            Log.d(TAG, "连接中");
                            break;
                        case BluetoothChatService.STATE_LISTEN:
                        case BluetoothChatService.STATE_NONE:
                            Log.d(TAG, "无连接");
                            break;
                    }
                    break;
                case Constants.MESSAGE_WRITE:
                    // 不可能发送消息
                    break;
                case Constants.MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    receive(readMessage);
                    break;
                case Constants.MESSAGE_DEVICE_NAME:
                    Log.d(TAG, "连接到"+msg.getData().getString(Constants.DEVICE_NAME));
                    break;
                case Constants.MESSAGE_TOAST:
                    Log.d(TAG, msg.getData().getString(Constants.TOAST));
                    break;
            }
        }
    };
    private static final String TAG = IconProxy.class.getSimpleName();


    // 处理接收到的数据
    private void receive(String msg) {
        GameHelperService service = GameHelperService.getInstance();
        Pattern pattern = Pattern.compile("([0-9])|([1-9][0-9]*)");
        if (pattern.matcher(msg).matches()) {
            int index = Integer.valueOf(msg);
            service.setImageResource(index);
        } else {
            Log.d(TAG, "不是数字" + msg);
        }
    }
}

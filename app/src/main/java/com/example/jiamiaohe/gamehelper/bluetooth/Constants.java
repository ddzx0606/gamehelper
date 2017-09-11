package com.example.jiamiaohe.gamehelper.bluetooth;

/**
 * Defines several constants used between {@link BluetoothChatService} and the UI.
 *
 * 常量接口
 */
public interface Constants {

    // Message types sent from the BluetoothChatService Handler
    int MESSAGE_STATE_CHANGE = 1;
    int MESSAGE_READ = 2;
    int MESSAGE_WRITE = 3;
    int MESSAGE_DEVICE_NAME = 4;
    int MESSAGE_TOAST = 5;
    int REQUEST_CONNECT_DEVICE_SECURE = 6;
    int REQUEST_ENABLE_BT = 7;

    // Key names received from the BluetoothChatService Handler
    String DEVICE_NAME = "device_name";
    String TOAST = "toast";
}

package com.ronda.tech.upgrade.model;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import com.ronda.tech.upgrade.utils.LogUtils;

import java.io.IOException;
import java.util.UUID;

public class BluetoothConnectThread extends Thread {
    private static final String TAG = "BluetoothConnectThread";
    private BluetoothSocket mScocket = null;
    private BluetoothDevice mDevice = null;
    private final String BLUE_TOOTH_UUID = "0000FEE0-0000-1000-8000-00805F9B34FB";
    private BluetoothAdapter bluetoothAdapter;


    public BluetoothConnectThread(BluetoothDevice device,BluetoothAdapter adapter){
        mDevice = device;
        bluetoothAdapter = adapter;
        try {
            mScocket = device.createRfcommSocketToServiceRecord(UUID.fromString(BLUE_TOOTH_UUID.toLowerCase()));
        }catch (IOException e){
            LogUtils.e(TAG,"Socket create failed");
        }
    }

    @Override
    public void run() {
        //建立连接前把扫描停止
        if (bluetoothAdapter.isDiscovering()){
            bluetoothAdapter.cancelDiscovery();
        }
        //连接到蓝牙设备
        LogUtils.d(TAG,"开始连接设备");
        new Thread(){
            @Override
            public void run() {
                try {
                    mScocket.connect();
                } catch (IOException e) {
                    LogUtils.e(TAG,"连接失败:"+e.getMessage());
                    e.printStackTrace();
                }
            }
        }.start();

        //连接成功后，进行数据的处理
        manageMyConnectedSocket(mScocket);
    }

    //处理socket数据
    private void manageMyConnectedSocket(BluetoothSocket mScocket) {

    }

    //关闭连接
    public void cancel(){
        try {
            mScocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

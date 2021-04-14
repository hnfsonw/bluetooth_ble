package com.ronda.tech.upgrade.model;

import android.bluetooth.BluetoothDevice;

import com.ronda.tech.upgrade.R;

public interface BleCallback {

    public interface onBlueScanCallback{
        void onScanFinish();
        void onFindDevices(BluetoothDevice device);
    }

    public interface onConnectSate{
        void connected(int type);
        void disconnected(String msg,int stats);
    }

    public interface onCharacteristic{
        void onReadCallback(String hex);
        void onWriteCallback(String hex);
        void onoCharacteristicChangedCallback(String hex);
    }

    public interface onDfuListener{
        void onDfuReadyListener();
    }
}

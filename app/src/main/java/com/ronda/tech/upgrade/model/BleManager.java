package com.ronda.tech.upgrade.model;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.ronda.tech.upgrade.utils.LogUtils;
import com.ronda.tech.upgrade.utils.TypeChangeUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
public class BleManager {

    private static final String TAG = "BleManager";
    private Context mContext;
    private final UUID DEVICE_UUID = UUID.fromString("0000FEE0-0000-1000-8000-00805F9B34FB");
    private final UUID CHARATIC_NOTIFY_UUID = UUID.fromString("0000FEE1-0000-1000-8000-00805F9B34FB");
    private final UUID CHARATIC_WRITE_UUID = UUID.fromString("0000FEE2-0000-1000-8000-00805F9B34FB");
    private final UUID DESCRIPTOR_UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
    private final UUID DFU_UUID = UUID.fromString("8e400001-f315-4f60-9fb8-838830daea50");
    private final UUID DFU_TARG_SERVICE = UUID.fromString("0000fe59-0000-1000-8000-00805f9b34fb");
    private final UUID DFU_TARG_CHARA_UUID = UUID.fromString("8ec90001-f315-4f60-9fb8-838830daea50");
    public static final String cpuTypeOne = "NRF51802";
    public static final String CMD_GET_DEVICE_VERSION = "AAFE000000E9";

    private BluetoothManager bluetoothManager;
    private BluetoothAdapter adapter;
    private BluetoothGatt bluetoothGatt;
    private BluetoothGattService gattService;
    private BluetoothGattService dfuService;
    private BluetoothGattCharacteristic gattNotifyCharacteristic;
    private BluetoothGattCharacteristic gattWriteCharacterstic;
    private BluetoothGattCharacteristic dfuCharacterstic;
    private BluetoothGattDescriptor gattDescriptor;
    private BluetoothGattDescriptor dfuDescriptor;
    private static BleManager mBleManager;
    private int connectType;


    public int getConnectType() {
        return connectType;
    }

    public void setConnectType(int connectType) {
        this.connectType = connectType;
    }

    private Handler mHandler = new Handler();
    private final long SCAN_PERIOD = 3000;
    private boolean mScanning;                         //是否正在扫描

    public static BleManager getInstance() {
        if (mBleManager == null) {
            synchronized (BleManager.class) {
                if (mBleManager == null) {
                    mBleManager = new BleManager();
                }
            }
        }
        return mBleManager;
    }

    public void init(Activity mContext) {
        this.mContext = mContext.getApplicationContext();
        bluetoothManager = (BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE);
        adapter = bluetoothManager.getAdapter();
    }


    public boolean isBleEnabled(){
        if (adapter == null){
            LogUtils.e(TAG,"设备不支持蓝牙");
            return false;
        }else {
            return adapter.isEnabled();
        }
    }

    /**
     * 获取手机之前绑定过的设备
     * @return
     */
    public List<String> loadBondedDevices(){
        //获取已经绑定过的设备
        Set<BluetoothDevice> pairedDevices = adapter.getBondedDevices();
        List<String> bondedDevices = new ArrayList<>();
        if (pairedDevices.size() > 0){
            for (BluetoothDevice device:pairedDevices){
                String deviceName = device.getName();
                String deviceMac = device.getAddress();
                bondedDevices.add(deviceMac);
                LogUtils.d(TAG,"device had bonded --->deviceName:"+deviceName+" deviceMac:"+deviceMac);
            }
        }
        return bondedDevices;
    }


    private BleCallback.onBlueScanCallback onBlueScanCallback;
    private BleCallback.onConnectSate onConnectSate;
    private BleCallback.onCharacteristic onCharacteristic;
    private BleCallback.onDfuListener onDfuListener;

    public void setOnDfuListener(BleCallback.onDfuListener onDfuListener) {
        this.onDfuListener = onDfuListener;
    }

    public void setOnConnectSate(BleCallback.onConnectSate onConnectSate) {
        this.onConnectSate = onConnectSate;
    }

    public void setOnCharacteristic(BleCallback.onCharacteristic onCharacteristic) {
        this.onCharacteristic = onCharacteristic;
    }

    /**
     * 启动搜索，并在5秒后自动停止
     * @param enable
     * @param onBlueScanCallback
     */
    public void scanLeDevice(final boolean enable, final BleCallback.onBlueScanCallback onBlueScanCallback) {
        if (adapter == null) return;
        this.onBlueScanCallback = onBlueScanCallback;
        if (enable) {//true
            //5秒后停止搜索
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    adapter.stopLeScan(mLeScanCallback);
                    mScanning = false;
                    onBlueScanCallback.onScanFinish();
                }
            }, SCAN_PERIOD);

            if (mScanning){
                return;
            }
            mScanning = true;
            adapter.startLeScan(mLeScanCallback); //开始搜索
        } else {
            adapter.stopLeScan(mLeScanCallback);//停止搜索
            mScanning = false;
        }
    }

    /**
     * 搜索周围蓝牙设备的结果回调
     */
    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, final int rssi, byte[] scanRecord) {
            onBlueScanCallback.onFindDevices(device);
            LogUtils.e(TAG,"found device name:"+device.getName()+" mac:"+device.getAddress()+" rssi:"+rssi);
        }
    };

    private void runOnMainThread(Runnable runnable) {
        if (isMainThread()) {
            runnable.run();
        } else {
            if (mHandler != null) {
                mHandler.post(runnable);
            }
        }
    }

    private boolean isMainThread() {
        return Looper.myLooper() == Looper.getMainLooper();
    }


    public void connect(BluetoothDevice device){
        LogUtils.e(TAG,"connect device:"+device.getAddress()+" name:"+device.getName());
        if (device == null) return;
        bluetoothGatt = device.connectGatt(mContext,false,mBluetoothGattCallback);
    }
    
    public void disConnect(){
        if (bluetoothGatt != null){
            bluetoothGatt.disconnect();
        }
    }


    /**
     * 链接设备后的回调
     */
    BluetoothGattCallback mBluetoothGattCallback = new BluetoothGattCallback() {
        @Override
        public void onPhyUpdate(BluetoothGatt gatt, int txPhy, int rxPhy, int status) {
            super.onPhyUpdate(gatt, txPhy, rxPhy, status);
        }

        @Override
        public void onPhyRead(BluetoothGatt gatt, int txPhy, int rxPhy, int status) {
            super.onPhyRead(gatt, txPhy, rxPhy, status);
        }

        //当连接状态发生改变
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            LogUtils.e(TAG,"onConnectionStateChange status:"+status+" newState:"+newState);
            if (status == BluetoothGatt.GATT_SUCCESS && newState == BluetoothProfile.STATE_CONNECTED){
                bluetoothGatt = gatt;
                bluetoothGatt.discoverServices();
                LogUtils.d(TAG,"连接设备成功");
            }else {
                bluetoothGatt.disconnect();
                bluetoothGatt.close();
                LogUtils.d(TAG,"设备已断开连接");
                String msg = "";
                if (status == 133){
                    msg = "无法连接此设备";
                }else {
                    msg = "连接已断开";
                }
                onConnectSate.disconnected(msg,status);
            }
        }

        //发现新服务，即调用了mBluetoothGatt.discoverServices()后，返回的数据
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            bluetoothGatt = gatt;
            if (getConnectType() == 0){
                gattService = bluetoothGatt.getService(DEVICE_UUID);

                gattNotifyCharacteristic = gattService.getCharacteristic(CHARATIC_NOTIFY_UUID);
                gattWriteCharacterstic = gattService.getCharacteristic(CHARATIC_WRITE_UUID);
                bluetoothGatt.setCharacteristicNotification(gattNotifyCharacteristic,true);

                gattDescriptor = gattNotifyCharacteristic.getDescriptor(DESCRIPTOR_UUID);
                gattDescriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                bluetoothGatt.writeDescriptor(gattDescriptor);
                onConnectSate.connected(0);
            }

            if (getConnectType() == 1){
                dfuService = bluetoothGatt.getService(DFU_UUID);
                dfuCharacterstic = dfuService.getCharacteristic(DFU_UUID);
                bluetoothGatt.setCharacteristicNotification(dfuCharacterstic,true);
                dfuDescriptor = dfuCharacterstic.getDescriptor(DESCRIPTOR_UUID);
                dfuDescriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                bluetoothGatt.writeDescriptor(dfuDescriptor);
                onConnectSate.connected(1);
            }

            if (getConnectType() == 3){
                onDfuListener.onDfuReadyListener();
                onConnectSate.connected(3);
            }
        }

        //调用mBluetoothGatt.readCharacteristic(characteristic)读取数据回调，在这里面接收数据
        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
            if (status == BluetoothGatt.GATT_SUCCESS){
                byte[] arrayOfByte = characteristic.getValue();
                LogUtils.e(TAG,"onCharacteristicRead:"+TypeChangeUtils.byte2hex(arrayOfByte));
                onCharacteristic.onReadCallback(TypeChangeUtils.byte2hex(arrayOfByte));
            }
        }

        //发送数据后的回调
        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
            if (status == BluetoothGatt.GATT_SUCCESS){
                byte[] arrayOfByte = characteristic.getValue();
                String hex = TypeChangeUtils.byte2hex(arrayOfByte);
                LogUtils.e(TAG,"onCharacteristicWrite:"+hex);
                onCharacteristic.onWriteCallback(TypeChangeUtils.byte2hex(arrayOfByte));
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
            byte[] arrayOfByte = characteristic.getValue();
            LogUtils.e(TAG,"onCharacteristicChanged:"+TypeChangeUtils.byte2hex(arrayOfByte));
            onCharacteristic.onoCharacteristicChangedCallback(TypeChangeUtils.byte2hex(arrayOfByte));
        }

        @Override
        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {//descriptor读
            super.onDescriptorRead(gatt, descriptor, status);
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {//descriptor写
            super.onDescriptorWrite(gatt, descriptor, status);
            byte[] arrayOfByte = descriptor.getValue();
            LogUtils.e(TAG,"onDescriptorWrite:"+TypeChangeUtils.byte2hex(arrayOfByte));
        }

        @Override
        public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {
            super.onReliableWriteCompleted(gatt, status);
        }

        //调用mBluetoothGatt.readRemoteRssi()时的回调，rssi即信号强度
        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {//读Rssi
            super.onReadRemoteRssi(gatt, rssi, status);
        }

        @Override
        public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
            super.onMtuChanged(gatt, mtu, status);
        }
    };

    /**
     * 想设备中写入数据
     * @param hexStr
     * @return
     */
    public boolean writeDatas(String hexStr) {
        byte[] byteArray = TypeChangeUtils.hexToByteArr(hexStr.toLowerCase());
        if (gattWriteCharacterstic == null)
            return false;
        if (bluetoothGatt == null)
            return false;
        setConnectType(0);
        gattWriteCharacterstic.setValue(byteArray);
        return bluetoothGatt.writeCharacteristic(gattWriteCharacterstic);
    }

    public void reDiscover(){
        if (bluetoothGatt != null){
            setConnectType(1);
            bluetoothGatt.discoverServices();
        }
    }

    public boolean writeDfu(String hexStr) {
        byte[] byteArray = TypeChangeUtils.hexToByteArr(hexStr.toLowerCase());
        if (dfuCharacterstic == null)
            return false;
        if (bluetoothGatt == null)
            return false;
        dfuCharacterstic.setValue(byteArray);
        return bluetoothGatt.writeCharacteristic(dfuCharacterstic);
    }
}

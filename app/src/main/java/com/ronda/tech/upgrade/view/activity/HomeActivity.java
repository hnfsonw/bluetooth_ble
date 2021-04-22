package com.ronda.tech.upgrade.view.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import com.ronda.tech.upgrade.R;
import com.ronda.tech.upgrade.base.BaseActivity;
import com.ronda.tech.upgrade.broadcast.NetBroadcastReceiver;
import com.ronda.tech.upgrade.constant.Constants;
import com.ronda.tech.upgrade.download.DownloadService;
import com.ronda.tech.upgrade.model.BleCallback;
import com.ronda.tech.upgrade.model.BleManager;
import com.ronda.tech.upgrade.model.ResponseInfoModel;
import com.ronda.tech.upgrade.presenter.impl.HomeAPresenterImpl;
import com.ronda.tech.upgrade.presenter.inter.IHomeAPresenter;
import com.ronda.tech.upgrade.utils.DecodeUtil;
import com.ronda.tech.upgrade.utils.LogUtils;
import com.ronda.tech.upgrade.utils.SharedPreferencesUtils;
import com.ronda.tech.upgrade.utils.TypeChangeUtils;
import com.ronda.tech.upgrade.view.inter.IHomeAView;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import pub.devrel.easypermissions.EasyPermissions;



public class HomeActivity extends BaseActivity implements IHomeAView, View.OnClickListener, NetBroadcastReceiver.NetChangeCallback, EasyPermissions.PermissionCallbacks,BleCallback.onConnectSate,BleCallback.onCharacteristic,BleCallback.onDfuListener {
    private static final String TAG = "HomeActivity";
    private IHomeAPresenter mIHomeAPresenter;
    private Context mContext;
    private long backPress;
    private ImageView ivScanQr;
    private int fromHome = 1;
    private TextView tvQrResult;
    private Spinner spMacChose;
    private Button btnSearch;
    private Button btnConnect;
    private Button btnDisconnect;
    private TextView tvConnectSates;
    private TextView tvVerison;
    private TextView tvCpuVerison;
    private TextView tvSystemVersion;
    private TextView spCpuTypeChose;
    private TextView tvFileName;
    private ImageView ivDownload;
    private ProgressBar pbDownload;
    private Button btnUpdateCode;
    private Button btnReset;
    private List<String> macDataLists;
    private NetBroadcastReceiver netBroadcastReceiver;
    private static Handler netRequestHandler;
    private int REQUEST_ENABLE_BT = 2;
    private ArrayAdapter<String> adapter;
    private BleManager bleManager;
    private TextView topTitle;

    //从下拉框中选中的mac地址
    private String selectedMac;
    private Set<BluetoothDevice> devicesSet = new HashSet<>();
    private String downloadUrl;
    private String deviceVersion;
    private String writeDateCmd;
    private String dfuDecode;
    private DfuSuccessReceiver dfuSuccessReceiver;
    private boolean isConnected;
    private boolean hasVersionNumber;//获取版本号的指令是否得到了回复

    @Override
    protected void init() {
        mContext = this;
        mIHomeAPresenter = new HomeAPresenterImpl(this);
        macDataLists = new ArrayList<>();
        initView();
        initBluetooth();
        initData();
        dealWithDownloadService();
    }

    /**
     * 升级文件下载进度回调
     */
    @SuppressLint("HandlerLeak")
    private void dealWithDownloadService() {
        netRequestHandler = new Handler(){
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case 0x111:
                        pbDownload.setVisibility(View.VISIBLE);
                        pbDownload.setProgress(msg.arg1,true);
                        btnUpdateCode.setClickable(true);
                        LogUtils.e(TAG,"下载进度:"+msg.arg1+"%");
                        showToast("下载完成");
                        ivDownload.setImageDrawable(getResources().getDrawable(R.mipmap.download_after));
                        break;
                    default:
                        break;
                }
            }
        };
    }


    private void initBluetooth() {
        bleManager = BleManager.getInstance();
        bleManager.init(this);
        bleManager.setOnCharacteristic(this);
        bleManager.setOnConnectSate(this);
        bleManager.setOnDfuListener(this);
        if (bleManager.isBleEnabled()){
            macDataLists = bleManager.loadBondedDevices();
        }else {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }

    private void initData() {
        adapterMac();

        //获取设备的版本信息
        mIHomeAPresenter.getLocalVerisons();

        //获取远程服务器上的版本信息并保存下载地址
        mIHomeAPresenter.downloadFile();

        //获取从升级成功或者升级失败界面扫描的设备的mac地址
        String qrResult = getIntent().getStringExtra("result") == null?"":getIntent().getStringExtra("result");
        if (!TextUtils.isEmpty(qrResult)){
            tvQrResult.setText(qrResult);
        }

        EasyPermissions.requestPermissions(this,"权限仅用于文件存储和扫描蓝牙设备，请允许",0,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.ACCESS_FINE_LOCATION);
    }

    private void initView() {
        ivScanQr = findViewById(R.id.home_iv_qr);
        ivScanQr.setOnClickListener(this);
        tvQrResult = findViewById(R.id.home_tv_mac_scan);
        spMacChose = findViewById(R.id.home_sp_mac_chose);
        btnSearch = findViewById(R.id.home_btn_search);
        btnSearch.setOnClickListener(this);
        btnConnect = findViewById(R.id.home_btn_connect);
        btnConnect.setOnClickListener(this);
        btnDisconnect = findViewById(R.id.home_btn_disconnect);
        btnDisconnect.setOnClickListener(this);
        tvConnectSates = findViewById(R.id.home_tv_connect_state);
        tvVerison = findViewById(R.id.home_tv_verison_number);
        tvCpuVerison = findViewById(R.id.home_tv_cpu_verison_number);
        tvSystemVersion = findViewById(R.id.home_tv_system_verison_number);
        spCpuTypeChose = findViewById(R.id.home_tv_system_verison_number_current);
        tvFileName = findViewById(R.id.home_tv_file_name);
        ivDownload = findViewById(R.id.home_iv_download);
        ivDownload.setOnClickListener(this);
        pbDownload = findViewById(R.id.home_progressBar_download);
        btnUpdateCode = findViewById(R.id.home_btn_upgrade);
        btnUpdateCode.setOnClickListener(this);
        btnUpdateCode.setClickable(true);
        btnReset = findViewById(R.id.home_btn_reset);
        btnReset.setOnClickListener(this);
        topTitle = findViewById(R.id.home_tv_top_title);
        topTitle.setText("主页");

        IntentFilter filter  = new IntentFilter();
        filter.addAction("com.dfu.successful.flag");
        dfuSuccessReceiver = new DfuSuccessReceiver();
        registerReceiver(dfuSuccessReceiver,filter);
    }

    /**
     * mac地址下拉框数据源
     */
    private void adapterMac() {
        adapter = new ArrayAdapter<>(this,R.layout.support_simple_spinner_dropdown_item,macDataLists);
        spMacChose.setAdapter(adapter);
        spMacChose.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedMac = macDataLists.get(position);
                LogUtils.d(TAG,"selected item:"+macDataLists.get(position));
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (isConnected){
                            return;
                        }
                        tvConnectSates.setTextColor(getResources().getColor(R.color.red));
                        tvConnectSates.setText("未连接");
                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_home;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (netBroadcastReceiver == null){
            netBroadcastReceiver = new NetBroadcastReceiver();
            IntentFilter filter = new IntentFilter();
            filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
            registerReceiver(netBroadcastReceiver,filter);
            netBroadcastReceiver.setCallback(this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() - backPress < 2000){
            Intent intent = new Intent(this,LoginActivity.class);
            intent.putExtra("isExit",true);
            startActivity(intent);
        }else {
            showToast(getString(R.string.exit));
        }
        backPress = System.currentTimeMillis();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.home_iv_qr:
                Intent intent = new Intent(mContext,ScanCodeActivity.class);
                intent.putExtra("from","home");
                startActivityForResult(intent,fromHome);
                break;
            case R.id.home_btn_search:
                if (!bleManager.isBleEnabled()){
                    showToast("请先开启蓝牙");
                    return;
                }
                showLoading("",mContext);

                //扫描周边蓝牙设备
                bleManager.scanLeDevice(true, new BleCallback.onBlueScanCallback() {
                    @Override
                    public void onFindDevices(BluetoothDevice device) {
                        if (device != null && macDataLists != null){
                            devicesSet.add(device);
                            if (!macDataLists.contains(device.getAddress())){
                                macDataLists.add(device.getAddress());
                            }
                        }
                    }

                    @Override
                    public void onScanFinish() {
                        dismissLoading();
                        showToast("搜索结束");
                        adapter.notifyDataSetChanged();
                    }
                });
                break;
            case R.id.home_btn_connect:
                if (!bleManager.isBleEnabled()){
                    showToast("请先开启蓝牙");
                    return;
                }

                if (isConnected){
                    showToast("已连接，不必重复连接");
                    return;
                }
                hasVersionNumber = false;
                showLoading("",mContext);
                if (bleManager.getConnectType() == 3){
                    bleManager.setConnectType(1);
                }
                if (getCurrentDevice() != null){
                    SharedPreferencesUtils.setParam(mContext,"connect_mac",getCurrentDevice().getAddress());
                    bleManager.connect(getCurrentDevice());
                }else {
                    if (bleManager != null){
                        bleManager.scanLeDevice(true, new BleCallback.onBlueScanCallback() {
                            @Override
                            public void onFindDevices(BluetoothDevice device) {
                                if (device != null && macDataLists != null){
                                    devicesSet.add(device);
                                    if (!macDataLists.contains(device.getAddress())){
                                        macDataLists.add(device.getAddress());
                                    }
                                }
                            }

                            @Override
                            public void onScanFinish() {
                                if (getCurrentDevice() == null){
                                    dismissLoading();
                                    showToast("此设备不在可连接范围内");
                                }else {
                                    showLoading("",mContext);
                                    SharedPreferencesUtils.setParam(mContext,"connect_mac",getCurrentDevice().getAddress());
                                    bleManager.connect(getCurrentDevice());
                                }
                            }
                        });
                    }
                }

                break;
            case R.id.home_btn_disconnect:
                bleManager.disConnect();
                break;
            case R.id.home_iv_download:
                if (!TextUtils.isEmpty(downloadUrl)){
                    Intent serviceIntent = new Intent(this, DownloadService.class);
                    serviceIntent.putExtra("file_url",downloadUrl);
                    serviceIntent.putExtra("file_name",downloadUrl.substring(downloadUrl.lastIndexOf("/")+1));
                    startService(serviceIntent);
                }
                break;
            case R.id.home_btn_upgrade:
                if (getCurrentDevice() != null){
                    String fileName = downloadUrl.substring(downloadUrl.lastIndexOf("/")+1);
                    File file = new File(Constants.APP_ROOT_PATH + Constants.DOWNLOAD_DIR + fileName);
                    if (!file.exists()){
                        showToast("请先下载升级文件");
                        return;
                    }
                    //发送DFU指令
                    showLoading("",mContext);
                    bleManager.reDiscover();
                }else {
                    showToast("未连接设备");
                }
                break;
            case R.id.home_btn_reset:
                tvQrResult.setText("");
                tvConnectSates.setText("未连接");
                tvConnectSates.setTextColor(getResources().getColor(R.color.red));
                tvVerison.setText("");
                tvCpuVerison.setText("");
                ivDownload.setImageDrawable(getResources().getDrawable(R.mipmap.download_before));
                pbDownload.setVisibility(View.VISIBLE);
                pbDownload.setProgress(0,true);
                break;
            default:

                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case 1:
                if (data != null){
                    if (isConnected){
                        tvConnectSates.setTextColor(getResources().getColor(R.color.green));
                        tvConnectSates.setText("已连接");
                    }

                    String cabinet_id = data.getExtras().getString("result");
                    LogUtils.e(TAG,"cabinet_id:"+cabinet_id.substring(cabinet_id.lastIndexOf("=")+1));
                    tvQrResult.setText(cabinet_id.substring(cabinet_id.lastIndexOf("=")+1));
                    //扫描id号后去服务器查询mac地址
                    mIHomeAPresenter.getMacFromService(cabinet_id.substring(cabinet_id.lastIndexOf("=")+1));
                }
                break;
            case 2:
                if (requestCode == RESULT_OK){
                    LogUtils.d(TAG,"蓝牙开启成功");
                }
                if (requestCode == RESULT_CANCELED){
                    LogUtils.e(TAG,"蓝牙开始失败");
                }
                break;
        }
    }

    /**
     * 后台返回的mac地址
     * @param infoBean
     */
    @Override
    public void verisonInfosRespones(ResponseInfoModel infoBean) {
        if (infoBean.getData().getMac() != null){
//            String macBefore = "c76c12c00f26";
            String macBefore = infoBean.getData().getMac();
            StringBuilder sb = new StringBuilder();
            for (int i = 0;i<macBefore.length();i = i+2){
                sb.append(macBefore.substring(i,i+2));
                sb.append(":");
            }
            String afterMac = sb.toString().substring(0,sb.toString().lastIndexOf(":"));
            selectedMac = afterMac;
            macDataLists.add(afterMac.toUpperCase());
            adapter.notifyDataSetChanged();

            for (int i = 0;i<macDataLists.size();i++){
                if (macDataLists.get(i).equalsIgnoreCase(afterMac)){
                    spMacChose.setSelection(i);
                }
            }
        }
    }

    @Override
    public void fails(String msg) {
        showToast(msg);
    }

    /**
     * 根据后台返回的下载url启动下载服务
     * @param infoModel
     */
    @Override
    public void upgradeFileInfos(ResponseInfoModel infoModel) {
        downloadUrl = infoModel.getData().getRows().get(0).getUrl();
        tvFileName.setText(downloadUrl.substring(downloadUrl.lastIndexOf("/")+1));
        tvSystemVersion.setText(infoModel.getData().getRows().get(0).getVername());
        ivDownload.setImageDrawable(getResources().getDrawable(R.mipmap.download_before));
    }

    /**
     * 可用于功能扩展
     * @param type
     */
    @Override
    public void onNetChanged(int type) {
        if (type == 1){
            //wifi

        }

        if (type == 0){
            //移动网络

        }

        if (type == -1){
            //无网络

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (netBroadcastReceiver != null){
            unregisterReceiver(netBroadcastReceiver);
        }

        if (dfuSuccessReceiver != null){
            unregisterReceiver(dfuSuccessReceiver);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode,permissions,grantResults,this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        switch (requestCode){
            case 0:
                File file = new File(Constants.APP_ROOT_PATH + Constants.DOWNLOAD_DIR);
                if (!file.exists()){
                    file.mkdirs();
                }
                LogUtils.d(TAG,"存储权限已授权");
                break;
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        switch (requestCode){
            case 0:
                LogUtils.e(TAG,"存储权限的申请被拒绝了");
                EasyPermissions.requestPermissions(this,"权限仅用于文件存储和扫描蓝牙设备，请允许",0,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.ACCESS_FINE_LOCATION);
                break;
        }
    }

    public static Handler getNetRequestHandler() {
        return netRequestHandler;
    }

    /**
     * 设备链接成功
     */
    @Override
    public void connected(final int type) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvConnectSates.setTextColor(getResources().getColor(R.color.green));
                tvConnectSates.setText("已连接");
                isConnected = true;
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (type == 0){
                    bleManager.setConnectType(0);
                    bleManager.writeDatas(BleManager.CMD_GET_DEVICE_VERSION);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (!hasVersionNumber){
                                LogUtils.d(TAG,"获取版本号的指令未得到回复");
                                tvVerison.setText("1.3");
                                spCpuTypeChose.setText("1.3");
                                tvCpuVerison.setText(BleManager.cpuTypeOne);
                            }
                        }
                    },1000);
                }

                if (type == 1){
                    dfuDecode = DecodeUtil.decodeMac(getCurrentDevice().getAddress(),deviceVersion);
                    LogUtils.e(TAG,"异或运算结果:"+dfuDecode);
                    bleManager.setConnectType(1);
                    bleManager.writeDfu(dfuDecode);
                }
                dismissLoading();
            }
        });
    }

    /**
     * 设备断开连接
     */
    @Override
    public void disconnected(final String msg, final int status) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (bleManager != null && status == 133){
                    dismissLoading();
                }
                tvConnectSates.setTextColor(getResources().getColor(R.color.red));
                tvConnectSates.setText(msg);
                isConnected = false;
            }
        });
    }

    @Override
    public void onReadCallback(String hex) {
        LogUtils.d(TAG,"read callback:"+hex);

    }

    @Override
    public void onWriteCallback(String hex) {
        LogUtils.d(TAG,"write callback:"+hex);
        writeDateCmd = hex;
    }

    @Override
    public void onoCharacteristicChangedCallback(final String hex) {
        LogUtils.d(TAG,"onoCharacteristicChanged callback:"+hex);
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                byte[] byArr = TypeChangeUtils.hexStringToByte(hex);
                //获取版本号的响应
                if (byArr.length >= 4 && writeDateCmd.equalsIgnoreCase(BleManager.CMD_GET_DEVICE_VERSION)){
                    hasVersionNumber = true;
                    tvVerison.setText(byArr[2]+"."+byArr[3]);
                    deviceVersion = byArr[2]+" "+byArr[3];
                    spCpuTypeChose.setText(byArr[2]+"."+byArr[3]);
                    if (String.valueOf(byArr[2]).equals("1")){
                        tvCpuVerison.setText(BleManager.cpuTypeOne);
                    }
                }else if (writeDateCmd.equalsIgnoreCase("200101")||writeDateCmd.equalsIgnoreCase(dfuDecode)){
                    showLoading("",mContext);
                    bleManager.scanLeDevice(true, new BleCallback.onBlueScanCallback() {
                        @Override
                        public void onFindDevices(BluetoothDevice device) {
                            if (device != null && macDataLists != null){
                                devicesSet.add(device);
                                if (!macDataLists.contains(device.getAddress())){
                                    macDataLists.add(device.getAddress());
                                }
                            }
                        }

                        @Override
                        public void onScanFinish() {
                            adapter.notifyDataSetChanged();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    tvConnectSates.setTextColor(getResources().getColor(R.color.red));
                                    tvConnectSates.setText("DFU模式:正在尝试重连……");
                                    if (!TextUtils.isEmpty(selectedMac)){
                                        StringBuilder sb = new StringBuilder();
                                        String[] strArr = selectedMac.split(":");
                                        int lastMacNumber = Integer.valueOf(strArr[strArr.length-1])+1;
                                        String halfTargMac = selectedMac.substring(0,selectedMac.lastIndexOf(":")+1);
                                        String targMac = sb.append(halfTargMac).append(lastMacNumber).toString();
                                        selectedMac = targMac;
                                        for (BluetoothDevice device:devicesSet){
                                            if (targMac.equalsIgnoreCase(device.getAddress())){
                                                bleManager.setConnectType(3);
                                                bleManager.connect(device);
                                            }
                                        }
                                    }
                                }
                            });
                        }
                    });
                }
            }
        },50);
    }

    private BluetoothDevice getCurrentDevice(){
        for (BluetoothDevice device:devicesSet){
            if (!TextUtils.isEmpty(selectedMac) && selectedMac.equals(device.getAddress())){
                return device;
            }
        }
        return null;
    }

    @Override
    public void onDfuReadyListener() {
        dismissLoading();
        LogUtils.d(TAG,"跳转升级界面");
        Intent intent = new Intent(this,UpgradeActivity.class);
        intent.putExtra("deviceMac",selectedMac);
        intent.putExtra("fileName",tvFileName.getText());
        startActivity(intent);
    }

    private class DfuSuccessReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String connectedMac = (String) SharedPreferencesUtils.getParam(mContext,"connect_mac","");
            LogUtils.e(TAG,"DFU后重新连接设备："+connectedMac);
            selectedMac = connectedMac;
            if (!TextUtils.isEmpty(connectedMac)){
                showLoading("",mContext);
                if (devicesSet.size() > 0){
                    for (BluetoothDevice device:devicesSet){
                        if (connectedMac.equalsIgnoreCase(device.getAddress())){
                            bleManager.setConnectType(0);
                            bleManager.connect(device);
                        }
                    }
                }else {
                    dismissLoading();
                }
            }
        }
    }
}

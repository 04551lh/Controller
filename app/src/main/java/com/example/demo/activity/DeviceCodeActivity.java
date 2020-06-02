package com.example.demo.activity;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.usb.UsbManager;
import android.os.Build;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import com.example.demo.Bean.ConfigBean;
import com.example.demo.Bean.DeviceUniqueCodeBean;
import com.example.demo.Bean.SucceedBean;
import com.example.demo.R;
import com.example.demo.base.BaseActivity;
import com.example.demo.network.CheckNetworkStatus;
import com.example.demo.network.OkHttpHelper;
import com.example.demo.utils.CommonMethod;
import com.example.demo.utils.MyException;
import com.google.gson.Gson;
import java.util.Objects;

public class DeviceCodeActivity extends BaseActivity implements View.OnClickListener, MyException {

    private final static String TAG = "DeviceCodeActivity";
    private ImageView mIvScanResultBack;
    private TextView mTvDeviceCode;
    private TextView mTvEntrySuccess;
    private TextView mTvEntry;
    private String mTerminalId;
    private OkHttpHelper mOkHttpHelper;
    //USB
    private BroadcastReceiver mReceiver;
    private boolean mConnected = false;
    private boolean mConfigured = false;
    private boolean mIsClose = false;
    //提示
    private LinearLayout mLlScanResultHint;
    private TextView mTvScanResultHint;
    private TextView mTvRescan;
    private boolean mIsClock;
    private String mHint;

    @Override
    public int getLayoutResId() {
        return R.layout.activity_device_code;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void initViews() {
        initUSB();
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        mIvScanResultBack = findViewById(R.id.iv_scan_result_back);
        mTvDeviceCode = findViewById(R.id.tv_device_code);
        mTvEntrySuccess = findViewById(R.id.tv_entry_success);
        mTvEntry = findViewById(R.id.tv_entry);
        mLlScanResultHint = findViewById(R.id.ll_scan_result_hint);
        mTvScanResultHint = findViewById(R.id.tv_scan_result_hint);
        mTvRescan = findViewById(R.id.tv_rescan);
        mTerminalId = Objects.requireNonNull(getIntent().getExtras()).getString(com.example.demo.network.Constant.TERMINAL_ID, "");
        mTvDeviceCode.setText(mTerminalId);
        mOkHttpHelper = OkHttpHelper.getInstance();
        mOkHttpHelper.setMyException(this);
    }
    @Override
    public void setListener() {
        mIvScanResultBack.setOnClickListener(this);
        mTvEntrySuccess.setOnClickListener(this);
        mTvEntry.setOnClickListener(this);
        mTvRescan.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mLlScanResultHint.setVisibility(View.GONE);
        mTvEntry.setBackground(getResources().getDrawable(R.drawable.ll_click_bg_style));
        mTvEntry.setTextColor(Color.WHITE);
        mIsClock = false;
    }
    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(R.id.iv_scan_result_back == id){
            finish();
            return;
        }
        if(R.id.tv_rescan == id){
            setCameraManifest();
            return;
        }
        if(R.id.tv_entry_success == id){
            Toast.makeText(DeviceCodeActivity.this,mHint,Toast.LENGTH_SHORT).show();
            return;
        }
        if(R.id.tv_entry == id){
            if(mIsClock){
                Toast.makeText(DeviceCodeActivity.this,mHint,Toast.LENGTH_SHORT).show();
                return;
            }
            mTvEntry.setBackground(getResources().getDrawable(R.drawable.hollow_circle));
            mTvEntry.setTextColor(getResources().getColor(R.color.colorPrimary));
            mIsClock = true;
            String terminalId = mTvDeviceCode.getText().toString().trim();
            ConfigBean configBean = new ConfigBean();
            configBean.setProducerID("");
            configBean.setTerminalModel("");
            configBean.setTerminalId(terminalId);
            configBean.setManufactureDate("");
//            if (judgeService(terminalId)) {
                String json = new Gson().toJson(configBean);
                String response = mOkHttpHelper.post(com.example.demo.network.Constant.UPDATA_CONFIG, json);
                if (response == null) return;
                SucceedBean succeedBean = new Gson().fromJson(response, SucceedBean.class);
                if (succeedBean.getStatuesCode() == 0) {
                    mHint = "设备信息录入成功!";
                    mLlScanResultHint.setVisibility(View.VISIBLE);
                    mTvScanResultHint.setText("设备信息录入成功!");
                    mTvScanResultHint.setCompoundDrawables(null,null,null,null);
                    mTvEntry.setVisibility(View.GONE);
                    mTvRescan.setVisibility(View.GONE);
                }
//            }
        }
    }

    private void setCameraManifest() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(DeviceCodeActivity.this, "需要动态获取权限", Toast.LENGTH_SHORT).show();
                ActivityCompat.requestPermissions(DeviceCodeActivity.this, new String[]{Manifest.permission.CAMERA}, 0);
            } else {
                CommonMethod.StartActivityForResultCapture(DeviceCodeActivity.this);
            }
        } else {
            CommonMethod.StartActivityForResultCapture(DeviceCodeActivity.this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 0 && grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    CommonMethod.StartActivityForResultCapture(DeviceCodeActivity.this);
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 扫描二维码/条码回传
        if (requestCode == 0 && resultCode == RESULT_OK) {
            if (data != null) {
                String content = data.getStringExtra(com.yzq.zxinglibrary.common.Constant.CODED_CONTENT);
                if(content == null) return;
                mTerminalId = content;
                mTvDeviceCode.setText(content);
                mTvRescan.setVisibility(View.GONE);
                mTvEntry.setVisibility(View.VISIBLE);
            }
        }
    }



    private void initUSB() {
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (intent.hasExtra(UsbManager.EXTRA_PERMISSION_GRANTED)) {
                    boolean permissionGranted = intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false);
                }
                if (com.example.demo.network.Constant.ACTION_USB_STATE.equals(action)) {
                    mConnected = intent.getBooleanExtra("connected", false);
                    mConfigured = intent.getBooleanExtra("configured", false);
                    Log.i(TAG, "mConnected :" + mConnected);
                    Log.i(TAG, "mConnected :" + mConfigured);
                    if (!mConnected && !mConfigured) {
                        mIsClose = true;
                    }else{
                        if(mIsClose) {finish();}
                    }
                }
            }
        };
        IntentFilter mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(com.example.demo.network.Constant.ACTION_USB_STATE);
        registerReceiver(mReceiver, mIntentFilter);
    }

    private boolean judgeService(String qr) {
        if(!CheckNetworkStatus.isNetworkAvailable(DeviceCodeActivity.this)){
            mLlScanResultHint.setVisibility(View.VISIBLE);
            mHint = "无法与服务器通讯，请检查手机网络";
            mTvScanResultHint.setText("无法与服务器通讯，请检查手机网络");
            mTvRescan.setVisibility(View.GONE);
            return false;
        }
        //时间戳（UNIX时间戳)
        String timestamp = System.currentTimeMillis() / 1000 + "";
        mOkHttpHelper.addParam("terminal_id", qr);
        mOkHttpHelper.addParam("timestamp", timestamp);
        mOkHttpHelper.addParam("sign", Objects.requireNonNull(CommonMethod.getStrMd5(Objects.requireNonNull(CommonMethod.getStrMd5(qr)).toLowerCase() + timestamp)).toLowerCase());
        mOkHttpHelper.addParam("device_info", "");
        mOkHttpHelper.addParam("flag", "1");
        String url = mOkHttpHelper.getParamWithString(com.example.demo.network.Constant.TEST_DEVICE_UNIQUE_CODE);
        String response = mOkHttpHelper.post(url, "");
        //返回值 ，0：正常；-1:其他错误；-2：SIGN错误，-4：该设备已报备过，-5设备唯一码不全部是数字
        DeviceUniqueCodeBean deviceUniqueCodeBean = new Gson().fromJson(response, DeviceUniqueCodeBean.class);
        if (deviceUniqueCodeBean == null) return false;
        Log.i(TAG, deviceUniqueCodeBean.toString());
        if (0 == deviceUniqueCodeBean.getRet()) {
            return true;
        } else if (-1 == deviceUniqueCodeBean.getRet()) {
            Toast.makeText(DeviceCodeActivity.this, "其他错误", Toast.LENGTH_SHORT).show();
        } else if (-2 == deviceUniqueCodeBean.getRet()) {
            Toast.makeText(DeviceCodeActivity.this, "SIGN错误", Toast.LENGTH_SHORT).show();
        } else if (-4 == deviceUniqueCodeBean.getRet()) {
            mHint = "设备唯一码已存在，请请重新扫码";
            mLlScanResultHint.setVisibility(View.VISIBLE);
            mTvScanResultHint.setText("设备唯一码已存在，请");
            mTvRescan.setVisibility(View.VISIBLE);
        } else if (-5 == deviceUniqueCodeBean.getRet()) {
            mLlScanResultHint.setVisibility(View.VISIBLE);
            mHint = "设备唯一码格式错误，请请重新扫码";
            mTvScanResultHint.setText("设备唯一码格式错误，请");
            mTvRescan.setVisibility(View.VISIBLE);
        } else {
            Toast.makeText(DeviceCodeActivity.this, "未知错误", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    @Override
    public void show(int flag,String str) {
        mLlScanResultHint.setVisibility(View.VISIBLE);
        mTvScanResultHint.setText("设备无应答，请重新连接设备");
        mHint = "设备无应答，请重新连接设备";
        mTvRescan.setVisibility(View.GONE);
        Toast.makeText(DeviceCodeActivity.this, str, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }
}

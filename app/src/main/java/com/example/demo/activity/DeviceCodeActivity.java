package com.example.demo.activity;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.usb.UsbManager;
import android.os.Build;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import com.example.demo.Bean.ConfigBean;
import com.example.demo.Bean.SucceedBean;
import com.example.demo.R;
import com.example.demo.base.BaseActivity;
import com.example.demo.network.OkHttpHelper;
import com.example.demo.utils.CommonMethod;
import com.example.demo.utils.MyException;
import com.google.gson.Gson;
import com.yzq.zxinglibrary.common.Constant;

import java.util.Objects;

public class DeviceCodeActivity extends BaseActivity implements View.OnClickListener, MyException {

    private final static String TAG = "DeviceCodeActivity";
    private ImageView mIvScanResults;
    private TextView mTvDeviceCode;
    private TextView mTvEntry;
    private TextView mTvRescan;
    private TextView mTvHttpTips;
    private TextView mTvEntrySuccess;
    private String mTerminalId;
    private OkHttpHelper mOkHttpHelper;
    //USB
    private BroadcastReceiver mReceiver;
    private boolean mConnected = false;
    private boolean mConfigured = false;

    @Override
    public int getLayoutResId() {
        return R.layout.activity_device_code;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void initViews() {
        initUSB();
        mIvScanResults = findViewById(R.id.iv_scan_results);
        mTvDeviceCode = findViewById(R.id.tv_device_code);
        mTvEntry = findViewById(R.id.tv_entry);
        mTvRescan = findViewById(R.id.tv_rescan);
        mTvHttpTips = findViewById(R.id.tv_http_tips);
        mTvEntrySuccess = findViewById(R.id.tv_entry_success);
        mTerminalId = Objects.requireNonNull(getIntent().getExtras()).getString(com.example.demo.network.Constant.TERMINAL_ID, "");
        mTvDeviceCode.setText(mTerminalId);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        mOkHttpHelper = OkHttpHelper.getInstance();
        mOkHttpHelper.setMyException(this);
    }
    @Override
    public void setListener() {
        mIvScanResults.setOnClickListener(this);
        mTvEntry.setOnClickListener(this);
        mTvRescan.setOnClickListener(this);
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
    protected void onResume() {
        super.onResume();
        Log.i(TAG,"onResume");

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
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
                String content = data.getStringExtra(Constant.CODED_CONTENT);
                mTerminalId = content;
                mTvDeviceCode.setText(content);
                mTvRescan.setVisibility(View.GONE);
                mTvEntry.setVisibility(View.VISIBLE);
                mTvHttpTips.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_scan_results:
                if(mTvHttpTips.getVisibility() == View.VISIBLE){
                    finish();
                }else{
                    setCameraManifest();
                }
                break;
            case R.id.tv_entry:
                if(mTerminalId.length() != 12){
                    mTvEntrySuccess.setBackgroundResource(R.mipmap.entry_fail_icon);
                    mTvHttpTips.setText(String.format("%s%s", getString(R.string.entry_fail), getString(R.string.code_error)));
                    mTvRescan.setVisibility(View.VISIBLE);
                }
                Log.i(TAG, "entry");
                ConfigBean configBean = new ConfigBean();
                configBean.setProducerID("");
                configBean.setTerminalModel("");
                configBean.setTerminalId(mTerminalId);
                configBean.setManufactureDate("");
                String json = new Gson().toJson(configBean);
                String response = mOkHttpHelper.post(com.example.demo.network.Constant.UPDATA_CONFIG, json);
                if(response == null)return;
                SucceedBean succeedBean = new Gson().fromJson(response, SucceedBean.class);
                if (succeedBean.getStatuesCode() == 0) {
                    mTvEntry.setVisibility(View.GONE);
                    mTvHttpTips.setVisibility(View.VISIBLE);
                    mTvHttpTips.setText(getString(R.string.entry_success));
                }else{
                    mTvHttpTips.setText(String.format("%s%s", getString(R.string.entry_fail), getString(R.string.device_not_responding)));
                    mTvRescan.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.tv_rescan:
                setCameraManifest();
                break;
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
                        finish();
                    }
                }
            }
        };
        IntentFilter mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(com.example.demo.network.Constant.ACTION_USB_STATE);
        registerReceiver(mReceiver, mIntentFilter);
    }

    @Override
    public void show(int flag,String str) {
        Toast.makeText(DeviceCodeActivity.this, str, Toast.LENGTH_SHORT).show();
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }
}

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
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import com.example.demo.Bean.ConfigBean;
import com.example.demo.Bean.DeviceIdBean;
import com.example.demo.Bean.DeviceUniqueCodeBean;
import com.example.demo.R;
import com.example.demo.base.BaseActivity;
import com.example.demo.network.CheckNetworkStatus;
import com.example.demo.network.Constant;
import com.example.demo.network.OkHttpHelper;
import com.example.demo.utils.BaseDialog;
import com.example.demo.utils.CommonMethod;
import com.google.gson.Gson;

import java.text.DecimalFormat;
import java.util.Objects;

public class ResultActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "ResultActivity";
    //返回按钮
    private ImageView mIvScanResult;
    //数据展示
    private TextView mTvThreeId;
    private TextView mTvDeviceId;
    private TextView mTvTerminalId;
    private TextView mTvProductId;
    //提交按钮
    private TextView mTvEntry;
    //提示
    private LinearLayout mLlScanResultHint;
    private TextView mTvScanResultHint;
    private TextView mTvRescan;
    private String mThreeCCode;
    private String mTerminalId;
    private String mProductId;
    private String mDeviceType;
    private String mDate;
    private OkHttpHelper mOkHttpHelper;
    private boolean mClockEntry;
    private String mHint;
    //USB
    private BroadcastReceiver mReceiver;
    private boolean mConnected = false;
    private boolean mConfigured = false;
    private boolean mIsClose = false;
    private TextView mTvEntrySuccess;
    private BaseDialog mBaseDialog;

    public static final int DRIVER_ID = 10001;
    public static final int SERVICE_ID = 10002;
    public String mDeviceId;
    private String mMac;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            mBaseDialog.dismiss();
            int flag = msg.what;
            switch (flag) {
                case -1:
                    mBaseDialog.dismiss();
                    Toast.makeText(ResultActivity.this, "其他错误", Toast.LENGTH_SHORT).show();
                    mClockEntry = false;
                    break;
                case -2:
                    mBaseDialog.dismiss();
                    Toast.makeText(ResultActivity.this, "SIGN错误", Toast.LENGTH_SHORT).show();
                    mClockEntry = false;
                    break;
                case -4:
                    mBaseDialog.dismiss();
                    mHint = "设备唯一码已存在，请请重新扫码";
                    mLlScanResultHint.setVisibility(View.VISIBLE);
                    mTvScanResultHint.setText("设备唯一码已存在，请");
                    mTvRescan.setVisibility(View.VISIBLE);
                    mClockEntry = false;
                    break;
                case -5:
                    mBaseDialog.dismiss();
                    mLlScanResultHint.setVisibility(View.VISIBLE);
                    mHint = "设备唯一码格式错误，请请重新扫码";
                    mTvScanResultHint.setText("设备唯一码格式错误，请");
                    mTvRescan.setVisibility(View.VISIBLE);
                    mClockEntry = false;
                    break;
                case -6:
                    mBaseDialog.dismiss();
                    Toast.makeText(ResultActivity.this, "设备唯一码位数错误", Toast.LENGTH_SHORT).show();
                    mClockEntry = false;
                    break;
                case 0:
                    mBaseDialog.dismiss();
                    mTvRescan.setVisibility(View.GONE);
                    mLlScanResultHint.setVisibility(View.VISIBLE);
                    mTvScanResultHint.setText("设备无应答，请重新连接设备");
                    mHint = "设备无应答，请重新连接设备";
                    Toast.makeText(ResultActivity.this, "网络异常，请检查网络或者重新打开USB网络共享再试~", Toast.LENGTH_SHORT).show();
                    break;
                case 1:
                    int arg1 = msg.arg1;
                    if (DRIVER_ID == arg1) {
                        mBaseDialog.dismiss();
                        mTvThreeId.setText(mThreeCCode);
                        mTvDeviceId.setText(mDeviceType);
                        mTvTerminalId.setText(String.format("%s%s", mDeviceId, mTerminalId));
                        mTvProductId.setText(mProductId);
                    }
                    if (mClockEntry) {
                        judgeService();
                        if (SERVICE_ID == arg1) {
                            configTerminalIdNetwork();
                        }
                    }
                    break;
                case 3:
                    mBaseDialog.dismiss();
                    mHint = "设备信息录入成功!";
                    mTvEntry.setVisibility(View.GONE);
                    mLlScanResultHint.setVisibility(View.VISIBLE);
                    mTvScanResultHint.setText("设备信息录入成功!");
                    mTvScanResultHint.setCompoundDrawables(null, null, null, null);
                    mTvRescan.setVisibility(View.GONE);
                    break;
            }
        }
    };

    @Override
    public int getLayoutResId() {
        return R.layout.activity_result;
    }

    @Override
    public void initViews() {
        initUSB();
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        mIvScanResult = findViewById(R.id.iv_scan_result_back);
        mTvThreeId = findViewById(R.id.tv_three_id);
        mTvDeviceId = findViewById(R.id.tv_device_id);
        mTvTerminalId = findViewById(R.id.tv_terminal_id);
        mTvProductId = findViewById(R.id.tv_product_id);
        mTvEntrySuccess = findViewById(R.id.tv_entry_success);
        mTvEntry = findViewById(R.id.tv_entry);
        mLlScanResultHint = findViewById(R.id.ll_scan_result_hint);
        mTvScanResultHint = findViewById(R.id.tv_scan_result_hint);
        mTvRescan = findViewById(R.id.tv_rescan);
        mOkHttpHelper = OkHttpHelper.getInstance();
        mBaseDialog = BaseDialog.showDialog(ResultActivity.this);
        initData();
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
                    } else {
                        if (mIsClose) {
                            finish();
                        }
                    }
                }
            }
        };
        IntentFilter mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(com.example.demo.network.Constant.ACTION_USB_STATE);
        registerReceiver(mReceiver, mIntentFilter);
    }

    private void initData() {
        Bundle bundle = getIntent().getExtras();
        assert bundle != null;
        mThreeCCode = bundle.getString(Constant.THREE_ID);
        mDeviceType = bundle.getString(Constant.DEIVCE_ID);
        mTerminalId = bundle.getString(Constant.TERMINAL_ID);
        mProductId = bundle.getString(Constant.PRODUCT_ID);
        mDate = bundle.getString(Constant.DATE_CODE);
        getDeviceId();
    }

    @Override
    public void setListener() {
        mIvScanResult.setOnClickListener(this);
        mTvEntrySuccess.setOnClickListener(this);
        mTvEntry.setOnClickListener(this);
        mTvRescan.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mLlScanResultHint.setVisibility(View.GONE);
        mTvEntry.setVisibility(View.VISIBLE);
        mTvEntry.setBackground(getResources().getDrawable(R.drawable.ll_click_bg_style));
        mTvEntry.setTextColor(Color.WHITE);
        mClockEntry = false;
    }
    private void judgeService() {
        //时间戳（UNIX时间戳)
        String terminalId = mTvTerminalId.getText().toString().trim();
        String timestamp = System.currentTimeMillis() / 1000 + "";
        mOkHttpHelper.addParam("terminal_id", terminalId);
        mOkHttpHelper.addParam("mac_info", mMac);
        mOkHttpHelper.addParam("timestamp", timestamp);
        mOkHttpHelper.addParam("sign", Objects.requireNonNull(CommonMethod.getStrMd5(Objects.requireNonNull(CommonMethod.getStrMd5(terminalId)).toLowerCase() + timestamp)).toLowerCase());
        mOkHttpHelper.addParam("device_info", "");
        mOkHttpHelper.addParam("flag", "1");
        getServiceNetwork();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (R.id.iv_scan_result_back == id) {
            finish();
            return;
        }
        if (R.id.tv_rescan == id) {
            setCameraManifest();
            return;
        }
        if (R.id.tv_entry_success == id) {
            Toast.makeText(ResultActivity.this, mHint, Toast.LENGTH_SHORT).show();
            return;
        }
        if (R.id.tv_entry == id) {
            if (mClockEntry) {
                Toast.makeText(ResultActivity.this, mHint, Toast.LENGTH_SHORT).show();
                return;
            }
            mClockEntry = true;
            if (!CheckNetworkStatus.isNetworkAvailable(ResultActivity.this)) {
                mLlScanResultHint.setVisibility(View.VISIBLE);
                mHint = "无法与服务器通讯，请检查手机网络";
                mTvScanResultHint.setText("无法与服务器通讯，请检查手机网络");
                mTvRescan.setVisibility(View.GONE);
                return;
            }
            mBaseDialog.show();
            getDeviceIdTest();
        }
    }

    private void getDeviceId() {
        new Thread() {
            @Override
            public void run() {
                String response = mOkHttpHelper.post(Constant.GET_DEVICE_ID, "");
                if (response == null) {
                    Message message = new Message();
                    message.what = 0;
                    message.arg1 = DRIVER_ID;
                    mHandler.sendMessage(message);
                } else {
                    DeviceIdBean deviceIdBean = new Gson().fromJson(response, DeviceIdBean.class);
                    String productKindCode = deviceIdBean.getResult().getProdectKindCode().substring(2);
                    DecimalFormat decimalFormat = new DecimalFormat("0000");
                    mDeviceId = decimalFormat.format(Integer.parseInt(productKindCode));
                    mMac = deviceIdBean.getResult().getMac();
                    Log.i(TAG,"MAC------------->"+mMac);
                    Message message = new Message();
                    message.what = 1;
                    message.arg1 = DRIVER_ID;
                    mHandler.sendMessage(message);
                }
            }
        }.start();
    }

    private void getDeviceIdTest() {
        new Thread() {
            @Override
            public void run() {
                String response = mOkHttpHelper.post(Constant.GET_DEVICE_ID, "");
                if (response == null) {
                    mHandler.sendEmptyMessage(0);
                } else {
                    mHandler.sendEmptyMessage(1);
                }
            }
        }.start();
    }

    private void configTerminalIdNetwork() {
        new Thread() {
            @Override
            public void run() {
                String terminalId = mTvTerminalId.getText().toString().trim();
                ConfigBean configBean = new ConfigBean();
                configBean.setProducerID(mProductId);
                configBean.setTerminalModel(mDeviceType);
                configBean.setTerminalId(terminalId);
                configBean.setManufactureDate(mDate);
                String json = new Gson().toJson(configBean);
                String response = mOkHttpHelper.post(Constant.UPDATA_CONFIG, json);
                if (response == null) {
                    mHandler.sendEmptyMessage(0);
                } else {
                    mHandler.sendEmptyMessage(3);
                }
            }
        }.start();
    }

    private void getServiceNetwork() {
        new Thread() {
            @Override
            public void run() {
                String url = mOkHttpHelper.getParamWithString(Constant.TEST_DEVICE_UNIQUE_CODE);
                String response = mOkHttpHelper.post(url, "");
                //返回值 ，0：正常；-1:其他错误；-2：SIGN错误，-4：该设备已报备过，-5设备唯一码不全部是数字
                DeviceUniqueCodeBean deviceUniqueCodeBean = new Gson().fromJson(response, DeviceUniqueCodeBean.class);
                Message message = new Message();
                if (deviceUniqueCodeBean == null) {
                    message.what = 0;
                    mHandler.sendMessage(message);
                }
                int ret = deviceUniqueCodeBean.getRet();
                if (0 == ret) {
                    message.what = 1;
                    message.arg1 = SERVICE_ID;
                    mHandler.sendMessage(message);
                } else if (-1 == ret) {
                    mHandler.sendEmptyMessage(-1);
                } else if (-2 == ret) {
                    mHandler.sendEmptyMessage(-2);
                } else if (-4 == ret) {
                    mHandler.sendEmptyMessage(-4);
                } else if (-5 == ret) {
                    mHandler.sendEmptyMessage(-5);
                } else {
                    mHandler.sendEmptyMessage(-6);
                }
            }
        }.start();
    }

    private void setCameraManifest() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(ResultActivity.this, "需要动态获取权限", Toast.LENGTH_SHORT).show();
                ActivityCompat.requestPermissions(ResultActivity.this, new String[]{Manifest.permission.CAMERA}, 0);
            } else {
                CommonMethod.StartActivityForResultCapture(ResultActivity.this);
            }
        } else {
            CommonMethod.StartActivityForResultCapture(ResultActivity.this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 0 && grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    CommonMethod.StartActivityForResultCapture(ResultActivity.this);
                }
            }
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 扫描二维码/条码回传
        if (requestCode == 0 && resultCode == RESULT_OK) {
            if (data != null) {
                String content = data.getStringExtra(com.yzq.zxinglibrary.common.Constant.CODED_CONTENT);
                if (content == null) return;
                int length = content.length();
                String terminalId = content.substring(length - 8);
                getDeviceId();
                mTvThreeId.setText(content.substring(0, 7));
                mTvDeviceId.setText(content.substring(7, 13));
                mTvTerminalId.setText(String.format("%s%s", mDeviceId, terminalId));
                mTvProductId.setText(mProductId);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }
}

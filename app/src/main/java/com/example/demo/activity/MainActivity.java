package com.example.demo.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.usb.UsbManager;
import android.os.Build;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.demo.R;
import com.example.demo.base.BaseActivity;
import com.example.demo.utils.SharedPreferencesHelper;
import com.yzq.zxinglibrary.android.CaptureActivity;
import com.yzq.zxinglibrary.bean.ZxingConfig;
import com.yzq.zxinglibrary.common.Constant;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

public class MainActivity extends BaseActivity implements View.OnClickListener, TextWatcher {

    private EditText mEtProductId;
    private EditText mEtProductType;
    private TextView mTvSave;
    private TextView mTvEdit;
    private TextView mTvScan;
    private boolean mIsSave;

    private SharedPreferencesHelper sharedPreferencesHelper;

    //USB
    private BroadcastReceiver mReceiver;

    private boolean mConnected = false;
    private boolean mConfigured = false;


    @Override
    public int getLayoutResId() {
        return R.layout.activity_main;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void initViews() {
        sharedPreferencesHelper = new SharedPreferencesHelper(this);
        mEtProductId = findViewById(R.id.et_product_id);
        mEtProductType = findViewById(R.id.et_product_type);
        mTvSave = findViewById(R.id.tv_save);
        mTvEdit = findViewById(R.id.tv_edit);
        mTvScan = findViewById(R.id.tv_scan);
        mIsSave = false;
        String productId = (String) sharedPreferencesHelper.get(com.example.demo.network.Constant.PRODUCT_ID, null);
        String deviceId = (String) sharedPreferencesHelper.get(com.example.demo.network.Constant.DEIVCE_ID, null);
        String product = productId == null ? "75208" : productId;
        String device = deviceId == null ? "KY-BJX" : deviceId;
        mEtProductId.setText(product);
        mEtProductType.setText(device);
        initUSB();
        if (productId != null && deviceId != null) {
            mEtProductId.setEnabled(false);
            mEtProductId.setClickable(false);
            mEtProductId.setTextColor(Color.DKGRAY);
            mEtProductType.setEnabled(false);
            mEtProductType.setClickable(false);
            mTvSave.setBackground(getResources().getDrawable(R.drawable.hollow_circle));
            mTvSave.setTextColor(getResources().getColor(R.color.colorPrimary));
            mEtProductType.setTextColor(Color.DKGRAY);
            mIsSave = true;
        }
    }

    @Override
    public void setListener() {
        mTvSave.setOnClickListener(this);
        mTvEdit.setOnClickListener(this);
        mTvScan.setOnClickListener(this);
        mEtProductType.addTextChangedListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 扫描二维码/条码回传
        if (requestCode == 0 && resultCode == RESULT_OK) {
            if (data != null) {
                String content = data.getStringExtra(Constant.CODED_CONTENT);
                String terminalId = "";
                String date = "";
                int length = content.length();
                if (content != null &&length > 8)
                    terminalId = content.substring(length- 8);

                if (content != null && length > 8)
                    date = content.substring(length - 11,length -3);
                String mProductId = mEtProductId.getText().toString().trim();
                String mProductType = mEtProductType.getText().toString().trim();
                Intent intent = new Intent(MainActivity.this, ResultActivity.class);
                intent.putExtra(com.example.demo.network.Constant.THREE_ID, content.substring(0, 7));
                intent.putExtra(com.example.demo.network.Constant.DEIVCE_ID, mProductType);
                intent.putExtra(com.example.demo.network.Constant.TERMINAL_ID, terminalId);
                intent.putExtra(com.example.demo.network.Constant.DATE_CODE, date);
                intent.putExtra(com.example.demo.network.Constant.PRODUCT_ID, mProductId);
                startActivity(intent);
            }
        }
    }

    @SuppressLint("ShowToast")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_edit:
                if (!mIsSave) {
                    Toast.makeText(MainActivity.this, R.string.please_save, Toast.LENGTH_SHORT).show();
                    return;
                }
                mEtProductId.setEnabled(true);
                mEtProductId.setClickable(true);
                mEtProductType.setEnabled(true);
                mEtProductType.setClickable(true);
                mTvSave.setBackground(getResources().getDrawable(R.drawable.solid_round));
                mTvSave.setTextColor(Color.WHITE);
                break;
            case R.id.tv_save:
                if (TextUtils.isEmpty(mEtProductId.getText())) {
                    Toast.makeText(MainActivity.this, getResources().getString(R.string.please_input_product_id_tip), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(mEtProductType.getText())) {
                    Toast.makeText(MainActivity.this, getResources().getString(R.string.please_input_product_type_tip), Toast.LENGTH_SHORT).show();
                    return;
                }
                mEtProductId.setEnabled(false);
                mEtProductId.setClickable(false);
                mEtProductId.setTextColor(Color.DKGRAY);
                mEtProductType.setEnabled(false);
                mEtProductType.setClickable(false);
                mTvSave.setBackground(getResources().getDrawable(R.drawable.hollow_circle));
                mTvSave.setTextColor(getResources().getColor(R.color.colorPrimary));
                mEtProductType.setTextColor(Color.DKGRAY);
                mIsSave = true;
                sharedPreferencesHelper.put(com.example.demo.network.Constant.PRODUCT_ID, mEtProductId.getText());
                sharedPreferencesHelper.put(com.example.demo.network.Constant.DEIVCE_ID, mEtProductType.getText());
                break;
            case R.id.tv_scan:
                if (!mIsSave) {
                    Toast.makeText(MainActivity.this, R.string.please_save, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!mConfigured || !mConnected) {
                    Toast.makeText(MainActivity.this, getResources().getString(R.string.please_usb_tip), Toast.LENGTH_SHORT).show();
                    return;
                }
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
                switch (action) {
                    case com.example.demo.network.Constant.ACTION_USB_STATE:
                        mConnected = intent.getBooleanExtra("connected", false);
                        mConfigured = intent.getBooleanExtra("configured", false);
                        break;
                }
            }
        };
        IntentFilter mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(com.example.demo.network.Constant.ACTION_USB_STATE);
        registerReceiver(mReceiver, mIntentFilter);
    }


    @SuppressLint("ShowToast")
    private void setCameraManifest() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(MainActivity.this, "需要动态获取权限", Toast.LENGTH_SHORT);
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, 0);
            } else {
                Toast.makeText(MainActivity.this, "不需要动态获取权限", Toast.LENGTH_SHORT);
                Intent intent = new Intent(MainActivity.this, CaptureActivity.class);
                /*ZxingConfig是配置类
                 *可以设置是否显示底部布局，闪光灯，相册，
                 * 是否播放提示音  震动
                 * 设置扫描框颜色等
                 * 也可以不传这个参数
                 * */
                ZxingConfig config = new ZxingConfig();
                config.setPlayBeep(true);//是否播放扫描声音 默认为true
                config.setShake(true);//是否震动  默认为true
                config.setDecodeBarCode(true);//是否扫描条形码 默认为true
//                config.setReactColor(R.color.colorAccent);//设置扫描框四个角的颜色 默认为白色
                config.setFrameLineColor(R.color.colorAccent);//设置扫描框边框颜色 默认无色
                config.setScanLineColor(R.color.colorAccent);//设置扫描线的颜色 默认白色
                config.setFullScreenScan(true);//是否全屏扫描  默认为true  设为false则只会在扫描框中扫描
                intent.putExtra(Constant.INTENT_ZXING_CONFIG, config);
                startActivityForResult(intent, 0);
            }
        } else {
            Intent intent = new Intent(MainActivity.this, CaptureActivity.class);
            /*ZxingConfig是配置类
             *可以设置是否显示底部布局，闪光灯，相册，
             * 是否播放提示音  震动
             * 设置扫描框颜色等
             * 也可以不传这个参数
             * */
            ZxingConfig config = new ZxingConfig();
            config.setPlayBeep(true);//是否播放扫描声音 默认为true
            config.setShake(true);//是否震动  默认为true
            config.setDecodeBarCode(true);//是否扫描条形码 默认为true
//                config.setReactColor(R.color.colorAccent);//设置扫描框四个角的颜色 默认为白色
            config.setFrameLineColor(R.color.colorAccent);//设置扫描框边框颜色 默认无色
            config.setScanLineColor(R.color.colorAccent);//设置扫描线的颜色 默认白色
            config.setFullScreenScan(true);//是否全屏扫描  默认为true  设为false则只会在扫描框中扫描
            intent.putExtra(Constant.INTENT_ZXING_CONFIG, config);
            startActivityForResult(intent, 0);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 0 && grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(MainActivity.this, CaptureActivity.class);
                ZxingConfig config = new ZxingConfig();
                config.setPlayBeep(true);//是否播放扫描声音 默认为true
                config.setShake(true);//是否震动  默认为true
                config.setDecodeBarCode(true);//是否扫描条形码 默认为true
//                config.setReactColor(R.color.colorAccent);//设置扫描框四个角的颜色 默认为白色
                config.setFrameLineColor(R.color.colorAccent);//设置扫描框边框颜色 默认无色
                config.setScanLineColor(R.color.colorAccent);//设置扫描线的颜色 默认白色
                config.setFullScreenScan(true);//是否全屏扫描  默认为true  设为false则只会在扫描框中扫描
                intent.putExtra(Constant.INTENT_ZXING_CONFIG, config);
                startActivityForResult(intent, 0);
            }
        }
    }

    // 退出时间
    private long currentBackPressedTime = 0;
    // 退出间隔
    private static final int BACK_PRESSED_INTERVAL = 2000;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                if (System.currentTimeMillis() - currentBackPressedTime > BACK_PRESSED_INTERVAL) {
                    currentBackPressedTime = System.currentTimeMillis();
                    Toast.makeText(this, R.string.double_click_exit, Toast.LENGTH_SHORT).show();
                } else {
                    // 退出
                    finish();
                    return true;
                }
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (TextUtils.isEmpty(s)) {
            mIsSave = false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }
}

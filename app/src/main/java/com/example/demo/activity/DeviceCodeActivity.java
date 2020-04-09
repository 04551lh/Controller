package com.example.demo.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.example.demo.Bean.ConfigBean;
import com.example.demo.Bean.SucceedBean;
import com.example.demo.R;
import com.example.demo.base.BaseActivity;
import com.example.demo.network.OkHttpHelper;
import com.example.demo.utils.BaseDialog;
import com.example.demo.utils.DefaultDialog;
import com.example.demo.utils.MyException;
import com.google.gson.Gson;
import com.yzq.zxinglibrary.android.CaptureActivity;
import com.yzq.zxinglibrary.bean.ZxingConfig;
import com.yzq.zxinglibrary.common.Constant;

public class DeviceCodeActivity extends BaseActivity implements View.OnClickListener, MyException, DefaultDialog.OnCenterItemClickListener {

    private final static String TAG = "DeviceCodeActivity";
    private final static int success = 0;
    private final static int fail = 1;
    private final static int code = 2;
    private ImageView mIvScanResults;
    private TextView mTvDeviceCode;
    private TextView mTvEntry;
    private TextView mTvRescan;
    private String mTerminalId;
    private OkHttpHelper mOkHttpHelper;
    private BaseDialog mBaseDialog;

    @Override
    public int getLayoutResId() {
        return R.layout.activity_device_code;
    }

    @Override
    public void initViews() {
        mIvScanResults = findViewById(R.id.iv_scan_results);
        mTvDeviceCode = findViewById(R.id.tv_device_code);
        mTvEntry = findViewById(R.id.tv_entry);
        mTvRescan = findViewById(R.id.tv_rescan);
        mTerminalId = getIntent().getExtras().getString(com.example.demo.network.Constant.TERMINAL_ID,"");
        mTvDeviceCode.setText(mTerminalId);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        mOkHttpHelper = OkHttpHelper.getInstance();
        mOkHttpHelper.setMyException(this);
        mBaseDialog = BaseDialog.showDialog(this);
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
                Toast.makeText(DeviceCodeActivity.this, "需要动态获取权限", Toast.LENGTH_SHORT);
                ActivityCompat.requestPermissions(DeviceCodeActivity.this, new String[]{Manifest.permission.CAMERA}, 0);
            } else {
                Toast.makeText(DeviceCodeActivity.this, "不需要动态获取权限", Toast.LENGTH_SHORT);
                Intent intent = new Intent(DeviceCodeActivity.this, CaptureActivity.class);
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
            Intent intent = new Intent(DeviceCodeActivity.this, CaptureActivity.class);
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

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 0 && grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(DeviceCodeActivity.this, CaptureActivity.class);
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
            }
        }
    }


    @Override
    public void initUsb() {
        if (!ismConnected() || !ismConfigured()) {
            Toast.makeText(DeviceCodeActivity.this, getResources().getString(R.string.please_usb_tip), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        int flag;
        switch (v.getId()) {
            case R.id.iv_scan_results:
                finish();
                break;
            case R.id.tv_entry:
                //todo
                if(mTerminalId.length() != 12){
                    flag = code;
                }
                Log.i(TAG, "entry");
                mBaseDialog.show();
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
                    mBaseDialog.dismiss();
                    flag = success;
                }else{
                    mBaseDialog.dismiss();
                    flag = fail;
                }
                initConfirm(flag);
                break;
            case R.id.tv_rescan:
                if (!ismConnected() || !ismConfigured()) {
                    Toast.makeText(DeviceCodeActivity.this, getResources().getString(R.string.please_usb_tip), Toast.LENGTH_SHORT).show();
                    return;
                }
                setCameraManifest();
                break;
        }
    }


    @Override
    public void show(String str) {
        Toast.makeText(DeviceCodeActivity.this, str, Toast.LENGTH_SHORT).show();
    }


    private void initConfirm(int flag){
        DefaultDialog defaultDialog = new DefaultDialog(DeviceCodeActivity.this);
        switch (flag){
            case success:
                defaultDialog.setTitle(getString(R.string.entry_success));
                break;
            case fail:
                defaultDialog.setTitle(String.format("%s%s", getString(R.string.entry_fail), getString(R.string.device_not_responding)));
                break;
            case code:
                defaultDialog.setTitle(String.format("%s%s", getString(R.string.entry_fail), getString(R.string.code_error)));
                break;
        }
        defaultDialog.setOnCenterItemClickListener(this);
        defaultDialog.show();
    }

    @Override
    public void OnCenterItemClick(DefaultDialog dialog, View view) {

    }
}

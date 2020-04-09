package com.example.demo.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.demo.Bean.ConfigBean;
import com.example.demo.Bean.DeviceIdBean;
import com.example.demo.Bean.ResponseBean;
import com.example.demo.Bean.SucceedBean;
import com.example.demo.R;
import com.example.demo.base.BaseActivity;
import com.example.demo.network.Constant;
import com.example.demo.network.OkHttpHelper;
import com.example.demo.utils.MyException;
import com.google.gson.Gson;

import java.text.DecimalFormat;

public class ResultActivity extends BaseActivity implements MyException {

    private TextView mTvThreeId;
    private TextView mTvDeviceId;
    private TextView mTvTerminalId;
    private TextView mTvProductId;
    private TextView mTvEntry;

    private String mProductId, mThreeCCode, mDeviceType, mTerminalId,mDate;
    private OkHttpHelper mOkHttpHelper;

    //USB
    private BroadcastReceiver mReceiver;
    private DeviceIdBean mDeviceIdBean;

    private boolean mConnected = false;
    private boolean mConfigured = false;

    @Override
    public int getLayoutResId() {
        return R.layout.activity_result;
    }

    @Override
    public void initViews() {
        initUSB();
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        mTvThreeId = findViewById(R.id.tv_three_id);
        mTvDeviceId = findViewById(R.id.tv_device_id);
        mTvTerminalId = findViewById(R.id.tv_terminal_id);
        mTvProductId = findViewById(R.id.tv_product_id);
        mTvEntry = findViewById(R.id.tv_entry);
        mOkHttpHelper = OkHttpHelper.getInstance();
        mOkHttpHelper.setMyException(this);
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
                switch (action) {
                    case Constant.ACTION_USB_STATE:
                        mConnected = intent.getBooleanExtra("connected", false);
                        mConfigured = intent.getBooleanExtra("configured", false);
                        break;
                }
            }
        };
        IntentFilter mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(Constant.ACTION_USB_STATE);
        registerReceiver(mReceiver, mIntentFilter);
    }

    private String getDeviceId(){
        String response = mOkHttpHelper.post(Constant.GET_DEVICE_ID, "");
        mDeviceIdBean = new Gson().fromJson(response, DeviceIdBean.class);
        if(mDeviceIdBean == null){finish();return "0800";}
        if(mDeviceIdBean.getStatuesCode() != 0){finish();return "0800";}
        if(mDeviceIdBean.getResult().getProdectKindCode() == null){finish();return "0800";}
        String productKindCode= mDeviceIdBean.getResult().getProdectKindCode().substring(2);
        DecimalFormat decimalFormat =new DecimalFormat("0000");
        String productCode=decimalFormat.format(Integer.parseInt(productKindCode));
        return productCode;
    }

    private void initData() {
        Bundle bundle = getIntent().getExtras();
        mThreeCCode = bundle.getString(Constant.THREE_ID);
        mDeviceType = bundle.getString(Constant.DEIVCE_ID);
        mTerminalId = bundle.getString(Constant.TERMINAL_ID);
        mProductId = bundle.getString(Constant.PRODUCT_ID);
        mDate = bundle.getString(Constant.DATE_CODE);
        String deviceId =  getDeviceId();
        mTvThreeId.setText(mThreeCCode);
        mTvDeviceId.setText(mDeviceType);
        mTvTerminalId.setText(String.format("%s%s", deviceId, mTerminalId));
        mTvProductId.setText(mProductId);

    }

    @Override
    public void setListener() {
        mTvEntry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTvEntry.setBackground(getResources().getDrawable(R.drawable.hollow_circle));
                mTvEntry.setTextColor(getResources().getColor(R.color.colorPrimary));
                if (!mConfigured || !mConnected) {
                    Toast.makeText(ResultActivity.this, getResources().getString(R.string.please_usb_tip), Toast.LENGTH_SHORT).show();
                    return;
                }
                String terminalId = mTvTerminalId.getText().toString().trim();
                ConfigBean configBean = new ConfigBean();
                configBean.setProducerID(mProductId);
                configBean.setTerminalModel(mDeviceType);
                configBean.setTerminalId(terminalId);
                configBean.setManufactureDate(mDate);
                String json = new Gson().toJson(configBean);
                String response = mOkHttpHelper.post(Constant.UPDATA_CONFIG, json);
                if(response == null)return;
                SucceedBean succeedBean = new Gson().fromJson(response, SucceedBean.class);
                if (succeedBean.getStatuesCode() == 0) {
                    Intent intent = new Intent(ResultActivity.this, QRCodeActivity.class);
                    intent.putExtra(Constant.PRODUCT_ID, mProductId);
                    intent.putExtra(Constant.DEIVCE_ID, mDeviceType);
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mReceiver);
        super.onDestroy();
    }

    @Override
    public void show(String str) {
        Toast.makeText(ResultActivity.this, str, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void initUsb() {
        if (!ismConnected() || !ismConfigured()) {
            Toast.makeText(ResultActivity.this, getResources().getString(R.string.please_usb_tip), Toast.LENGTH_SHORT).show();
        }
    }
}

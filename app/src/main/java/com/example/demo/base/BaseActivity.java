package com.example.demo.base;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.demo.Interface.IUSBStatus;

/**
 * Created by dell on 2019/12/6 18:40
 * Description:
 * Emain: 1187278976@qq.com
 */
public abstract class BaseActivity extends AppCompatActivity implements IUSBStatus {


    //USB
    private BroadcastReceiver mReceiver;

    public boolean ismConnected() {
        return mConnected;
    }

    public boolean ismConfigured() {
        return mConfigured;
    }

    private boolean mConnected = false;
    private boolean mConfigured = false;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResId());
        initViews();
        setListener();
        initUSB();
    }

    public abstract int getLayoutResId();

    public abstract void initViews();

    public abstract void setListener();

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
                        initUsb();
                        break;
                }
            }
        };
        IntentFilter mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(com.example.demo.network.Constant.ACTION_USB_STATE);
        registerReceiver(mReceiver, mIntentFilter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }


}

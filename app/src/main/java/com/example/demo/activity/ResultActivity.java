package com.example.demo.activity;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.demo.Bean.ConfigBean;
import com.example.demo.Bean.SucceedBean;
import com.example.demo.R;
import com.example.demo.base.BaseActivity;
import com.example.demo.network.Constant;
import com.example.demo.network.OkHttpHelper;
import com.google.gson.Gson;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

public class ResultActivity extends BaseActivity {

    private TextView mTvThreeId;
    private TextView mTvDeviceId;
    private TextView mTvTerminalId;
    private TextView mTvProductId;
    private TextView mTvEntry;

    private String mProductId, mThreeCCode, mDeviceType, mTerminalId;
    private OkHttpHelper mOkHttpHelper;

    @Override
    public int getLayoutResId() {
        return R.layout.activity_result;
    }

    @Override
    public void initViews() {
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        mTvThreeId = findViewById(R.id.tv_three_id);
        mTvDeviceId = findViewById(R.id.tv_device_id);
        mTvTerminalId = findViewById(R.id.tv_terminal_id);
        mTvProductId = findViewById(R.id.tv_product_id);
        mTvEntry = findViewById(R.id.tv_entry);
        mOkHttpHelper = OkHttpHelper.getInstance();
        initData();
    }


    private void initData() {
        Bundle bundle = getIntent().getExtras();
        mThreeCCode = bundle.getString(Constant.THREE_ID);
        mDeviceType = bundle.getString(Constant.DEIVCE_ID);
        mTerminalId = bundle.getString(Constant.TERMINAL_ID);
        mProductId = bundle.getString(Constant.PRODUCT_ID);
        mTvThreeId.setText(mThreeCCode);
        mTvDeviceId.setText(mDeviceType);
        mTvTerminalId.setText(mTerminalId);
        mTvProductId.setText(mProductId);
    }

    @Override
    public void setListener() {
        mTvEntry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConfigBean configBean = new ConfigBean();
                configBean.setProducerID(mProductId);
                configBean.setTerminalModel(mDeviceType);
                configBean.setTerminalId(mTerminalId);
                configBean.setThreeCCode(mProductId);

                String json = new Gson().toJson(configBean);
                String response = mOkHttpHelper.post(Constant.UPDATA_CONFIG, json);
                SucceedBean succeedBean = new Gson().fromJson(response, SucceedBean.class);
                switch (succeedBean.getStatuesCode()) {
                    case 0:
                        Intent intent = new Intent(ResultActivity.this, QRCodeActivity.class);
                        startActivity(intent);
                        break;
                }

            }
        });
    }

}

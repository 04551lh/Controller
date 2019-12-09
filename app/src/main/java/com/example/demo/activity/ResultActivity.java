package com.example.demo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.demo.Bean.ConfigBean;
import com.example.demo.Bean.SucceedBean;
import com.example.demo.R;
import com.example.demo.base.BaseActivity;
import com.example.demo.network.Constant;
import com.example.demo.network.OkHttpHelper;
import com.example.demo.utils.NetUtil;
import com.google.gson.Gson;

public class ResultActivity extends BaseActivity {

    private TextView mTvThreeId;
    private TextView mTvDeviceId;
    private EditText mTvTerminalId;
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
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
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
                if(NetUtil.getNetWorkStart(ResultActivity.this) == NetUtil.NETWORK_NONE){
                    return;
                }
                ConfigBean configBean = new ConfigBean();
                configBean.setProducerID(mProductId);
                configBean.setTerminalModel(mDeviceType);
                configBean.setTerminalId(mTerminalId);
//                configBean.setThreeCCode(mThreeCCode);

                String json = new Gson().toJson(configBean);
                String response = mOkHttpHelper.post(Constant.UPDATA_CONFIG, json);
                SucceedBean succeedBean = new Gson().fromJson(response, SucceedBean.class);
                if (succeedBean.getStatuesCode() == 0) {
                    Intent intent = new Intent(ResultActivity.this, QRCodeActivity.class);
                    intent.putExtra(Constant.PRODUCT_ID, mProductId);
                    startActivity(intent);
                }

            }
        });
    }

}

package com.example.demo.activity;

import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.demo.R;
import com.example.demo.base.BaseActivity;

public class SettingsActivity extends BaseActivity implements View.OnClickListener {

    private ImageView mIvSettingsBack;
    private ImageView mIvRefresh;
    private EditText mEtPlusSpeed;
    private TextView mTvSave;

    @Override
    public int getLayoutResId() {
        return R.layout.activity_settings;
    }

    @Override
    public void initViews() {
        mIvSettingsBack = findViewById(R.id.iv_settings_back);
        mIvRefresh = findViewById(R.id.iv_refresh);
        mEtPlusSpeed = findViewById(R.id.et_plus_speed);
        mTvSave = findViewById(R.id.tv_save);
        getNetwork();
    }

    @Override
    public void setListener() {
        mIvSettingsBack.setOnClickListener(this);
        mIvRefresh.setOnClickListener(this);
        mTvSave.setOnClickListener(this);
    }

    @Override
    public void initUsb() {
        if (!ismConnected() || !ismConfigured()) {
            Toast.makeText(SettingsActivity.this, getResources().getString(R.string.please_usb_tip), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_settings_back:
                finish();
                break;
            case R.id.iv_refresh:
                onRefresh();
                break;
            case R.id.tv_save:
                if(TextUtils.isEmpty(mEtPlusSpeed.getText())){
                    Toast.makeText(SettingsActivity.this,"请输入脉冲系数～",Toast.LENGTH_SHORT).show();
                    return;
                }
                int speed = Integer.parseInt(mEtPlusSpeed.getText().toString());
                if(speed < 0 || speed > 100){
                    Toast.makeText(SettingsActivity.this,"脉冲系数取值区间0-100～",Toast.LENGTH_SHORT).show();
                    return;
                }
                onSave();
                break;
        }
    }



    private void getNetwork(){
        //todo
    }

    private void  onRefresh(){
        //todo
        Toast.makeText(SettingsActivity.this,"刷新～",Toast.LENGTH_SHORT).show();
    }

    private void onSave(){
        //todo
        Toast.makeText(SettingsActivity.this,"保存～",Toast.LENGTH_SHORT).show();
    }
}

package com.example.demo.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
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
    private boolean isSave;

    private SharedPreferencesHelper sharedPreferencesHelper;
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
        isSave = false;
        String productId = (String)sharedPreferencesHelper.get(com.example.demo.network.Constant.PRODUCT_ID,null);
        String deviceId = (String)sharedPreferencesHelper.get(com.example.demo.network.Constant.DEIVCE_ID,null);
        mEtProductId.setText(productId);
        mEtProductType.setText(deviceId);
        if(productId != null &&deviceId != null){
            mEtProductId.setEnabled(false);
            mEtProductId.setClickable(false);
            mEtProductId.setTextColor(Color.DKGRAY);
            mEtProductType.setEnabled(false);
            mEtProductType.setClickable(false);
            mTvSave.setBackground(getResources().getDrawable(R.drawable.hollow_circle));
            mTvSave.setTextColor(getResources().getColor(R.color.colorPrimary));
            mEtProductType.setTextColor(Color.DKGRAY);
            isSave = true;
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
                if (content != null) terminalId = content.substring(content.length() - 11);
                String mProductId = mEtProductId.getText().toString().trim();
                String mProductType = mEtProductType.getText().toString().trim();
                Intent intent = new Intent(MainActivity.this, ResultActivity.class);
                intent.putExtra(com.example.demo.network.Constant.THREE_ID, content);
                intent.putExtra(com.example.demo.network.Constant.DEIVCE_ID, mProductType);
                intent.putExtra(com.example.demo.network.Constant.TERMINAL_ID, terminalId);
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
                if (!isSave) {
                    Toast.makeText(MainActivity.this,"请先保存~",Toast.LENGTH_SHORT).show();
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
                isSave = true;
                sharedPreferencesHelper.put(com.example.demo.network.Constant.PRODUCT_ID,mEtProductId.getText());
                sharedPreferencesHelper.put(com.example.demo.network.Constant.DEIVCE_ID,mEtProductType.getText());
                break;
            case R.id.tv_scan:
                if (!isSave) {
                    Toast.makeText(MainActivity.this,"请先保存~",Toast.LENGTH_SHORT).show();
                    return;
                }
                setCameraManifest();
                break;
        }
    }

    @SuppressLint("ShowToast")
    private void setCameraManifest() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(MainActivity.this, "需要动态获取权限", Toast.LENGTH_SHORT);
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, 0);
            } else {
                Toast.makeText(MainActivity.this, "不需要动态获取权限", Toast.LENGTH_SHORT);
                Intent openCameraIntent = new Intent(MainActivity.this, CaptureActivity.class);
                startActivityForResult(openCameraIntent, 0);
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 0 && grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                Intent openCameraIntent = new Intent(MainActivity.this, CaptureActivity.class);
                startActivityForResult(openCameraIntent, 0);
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
                    Toast.makeText(this, "再按一次返回键退出程序", Toast.LENGTH_SHORT).show();
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
        if(TextUtils.isEmpty(s)){
            isSave = false;
        }
    }
}

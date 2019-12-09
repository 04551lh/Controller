package com.example.demo.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.demo.R;
import com.example.demo.base.BaseActivity;
import com.yzq.zxinglibrary.android.CaptureActivity;
import com.yzq.zxinglibrary.common.Constant;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    private EditText mEtProductId;
    private TextView mTvSave;
    private TextView mTvEdit;
    private TextView mTvHint;
    private String mSave, mScanCode;

    @Override
    public int getLayoutResId() {
        return R.layout.activity_main;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mTvSave.setText(mSave);
        mTvEdit.setVisibility(View.GONE);
        mTvHint.setVisibility(View.VISIBLE);
    }

    @Override
    public void initViews() {
        mEtProductId = findViewById(R.id.et_product_id);
        mTvSave = findViewById(R.id.tv_save);
        mTvEdit = findViewById(R.id.tv_edit);
        mTvHint = findViewById(R.id.tv_hint);
        mSave = getResources().getString(R.string.save);
        mScanCode = getResources().getString(R.string.scan_code);
    }

    @Override
    public void setListener() {
        mTvSave.setOnClickListener(this);
        mTvEdit.setOnClickListener(this);
        mTvHint.setOnClickListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 扫描二维码/条码回传
        if (requestCode == 0 && resultCode == RESULT_OK) {
            if (data != null) {
                String content = data.getStringExtra(Constant.CODED_CONTENT);
//                String deviceId = content.substring(content.length()-17,content.length()-11);
                String deviceId = "KY-BJX";
                String terminalId = "";
                if (content != null) terminalId = content.substring(content.length() - 11);
                String mProductId = mEtProductId.getText().toString().trim();
                Intent intent = new Intent(MainActivity.this, ResultActivity.class);
                intent.putExtra(com.example.demo.network.Constant.THREE_ID, content);
                intent.putExtra(com.example.demo.network.Constant.DEIVCE_ID, deviceId);
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
            case R.id.tv_save:
                if (TextUtils.isEmpty(mEtProductId.getText())) {
                    Toast.makeText(MainActivity.this, getResources().getString(R.string.please_input_product_id_tip), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (mTvSave.getText().toString().trim().equals(mSave)) {
                    mTvSave.setText(mScanCode);
                    mTvEdit.setVisibility(View.VISIBLE);
                    mTvHint.setVisibility(View.GONE);
                } else if (mTvSave.getText().toString().trim().equals(mScanCode)) {
                    //打开扫描界面扫描条形码或二维码
                    setCameraManifest();
                }
                break;
            case R.id.tv_edit:
                mTvSave.setText(mSave);
                mTvEdit.setVisibility(View.GONE);
                mTvHint.setVisibility(View.VISIBLE);
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
}

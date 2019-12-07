package com.example.demo.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
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
                String mProductId = mEtProductId.getText().toString().trim();
                Intent intent = new Intent(MainActivity.this, ResultActivity.class);
                intent.putExtra(com.example.demo.network.Constant.THREE_ID, "C001143");
                intent.putExtra(com.example.demo.network.Constant.DEIVCE_ID, "KY-BJX");
                intent.putExtra(com.example.demo.network.Constant.TERMINAL_ID, "20191127001");
                intent.putExtra(com.example.demo.network.Constant.PRODUCT_ID, mProductId);
                startActivity(intent);
//                resultTextView.setText("扫描结果为：" + content);
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
            }else {
//                showWaringDialog();
            }

        }
    }

//    private void showWaringDialog() {
//        AlertDialog dialog = new AlertDialog.Builder(this)
//                .setTitle("警告！")
//                .setMessage("请前往设置->应用->PermissionDemo->权限中打开相关权限，否则功能无法正常运行！")
//                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        // 一般情况下如果用户不授权的话，功能是无法运行的，做退出处理
//                        finish();
//                    }
//                }).show();
//    }
}

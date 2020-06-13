package com.example.demo.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.demo.R;
import com.example.demo.base.BaseActivity;
import com.example.demo.network.OkHttpHelper;
import com.example.demo.utils.BaseDialog;
import com.example.demo.utils.CommonMethod;
import com.example.demo.utils.SharedPreferencesHelper;
import com.yzq.zxinglibrary.common.Constant;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

public class MainActivity extends BaseActivity implements View.OnClickListener, TextWatcher {

    private ImageView mIvKy800;
    private EditText mEtProductId;
    private TextView mTvSave;
    private TextView mTvEdit;
    private TextView mTvScan;
    private boolean mIsSave;
    private BaseDialog mBaseDialog;
    private SharedPreferencesHelper mSharedPreferencesHelper;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            mBaseDialog.dismiss();
            int flag = msg.what;
            switch (flag) {
                case 0:
                    Toast.makeText(MainActivity.this, "网络异常，请检查网络或者重新打开USB网络共享再试~", Toast.LENGTH_SHORT).show();
                    break;
                case 1:
                    setCameraManifest();
                    break;
            }
        }
    };
    private OkHttpHelper mOkHttpHelper;

    @Override
    public int getLayoutResId() {
        return R.layout.activity_main;
    }

    @Override
    public void initViews() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        mSharedPreferencesHelper = new SharedPreferencesHelper(this);
        mIvKy800 = findViewById(R.id.iv_ky_800);
        mEtProductId = findViewById(R.id.et_product_id);
        mTvSave = findViewById(R.id.tv_save);
        mTvEdit = findViewById(R.id.tv_edit);
        mTvScan = findViewById(R.id.tv_scan);
        mBaseDialog = BaseDialog.showDialog(MainActivity.this);
        mIsSave = false;
        mOkHttpHelper = OkHttpHelper.getInstance();
        String productId = (String) mSharedPreferencesHelper.get(com.example.demo.network.Constant.PRODUCT_ID, null);
        String product = productId == null ? "75208" : productId;
        mEtProductId.setText(product);
        if (productId != null) {
            mEtProductId.setEnabled(false);
            mEtProductId.setClickable(false);
            mEtProductId.setTextColor(Color.DKGRAY);
            mTvSave.setBackground(getResources().getDrawable(R.drawable.hollow_circle));
            mTvSave.setTextColor(getResources().getColor(R.color.colorPrimary));
            mIsSave = true;
        }
    }

    @Override
    public void setListener() {
        mIvKy800.setOnClickListener(this);
        mTvSave.setOnClickListener(this);
        mTvEdit.setOnClickListener(this);
        mTvScan.setOnClickListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 扫描二维码/条码回传
        if (requestCode == 0 && resultCode == RESULT_OK) {
            if (data != null) {
                String content = data.getStringExtra(Constant.CODED_CONTENT);
                if (content == null) return;
                if (content.length() < 14) return;
                int length = content.length();
                String terminalId = content.substring(length - 8);
                String code = content.substring(length - 11, length - 3);
                String mProductId = mEtProductId.getText().toString().trim();
                String productType = content.substring(7, 13);
                Intent intent = new Intent(MainActivity.this, ResultActivity.class);
                intent.putExtra(com.example.demo.network.Constant.THREE_ID, content.substring(0, 7));
                intent.putExtra(com.example.demo.network.Constant.DEIVCE_ID, productType);
                intent.putExtra(com.example.demo.network.Constant.TERMINAL_ID, terminalId);
                intent.putExtra(com.example.demo.network.Constant.DATE_CODE, code);
                intent.putExtra(com.example.demo.network.Constant.PRODUCT_ID, mProductId);
                startActivity(intent);
            }
        }
    }

    @SuppressLint("ShowToast")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_ky_800:
                finish();
                break;
            case R.id.tv_edit:
                mEtProductId.setEnabled(true);
                mEtProductId.setClickable(true);
                mTvSave.setBackground(getResources().getDrawable(R.drawable.solid_round));
                mTvSave.setTextColor(Color.WHITE);
                break;
            case R.id.tv_save:
                if (TextUtils.isEmpty(mEtProductId.getText())) {
                    Toast.makeText(MainActivity.this, getResources().getString(R.string.please_input_product_id_tip), Toast.LENGTH_SHORT).show();
                    mIsSave = false;
                    return;
                }
                if (5 != mEtProductId.getText().toString().trim().length()) {
                    mIsSave = false;
                    Toast.makeText(MainActivity.this, "请输入正确的厂商ID（5位数字）～", Toast.LENGTH_SHORT).show();
                    return;
                }
                mEtProductId.setEnabled(false);
                mEtProductId.setClickable(false);
                mEtProductId.setTextColor(Color.DKGRAY);
                mTvSave.setBackground(getResources().getDrawable(R.drawable.hollow_circle));
                mTvSave.setTextColor(getResources().getColor(R.color.colorPrimary));
                mIsSave = true;
                mSharedPreferencesHelper.put(com.example.demo.network.Constant.PRODUCT_ID, mEtProductId.getText());
                break;
            case R.id.tv_scan:
                mBaseDialog.show();
                if (5 != mEtProductId.getText().toString().trim().length()) {
                    mIsSave = false;
                    mBaseDialog.dismiss();
                    Toast.makeText(MainActivity.this, "请输入正确的厂商ID（5位数字）～", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!mIsSave) {
                    mBaseDialog.dismiss();
                    Toast.makeText(MainActivity.this, R.string.please_save, Toast.LENGTH_SHORT).show();
                    return;
                }
                getNetwork();
                break;
        }
    }


    private void getNetwork() {
        new Thread() {
            @Override
            public void run() {
                String response = mOkHttpHelper.post(com.example.demo.network.Constant.GET_DEVICE_ID, "");
                if (response == null) {
                    mHandler.sendEmptyMessage(0);
                } else {
                    mHandler.sendEmptyMessage(1);
                }
            }
        }.start();
    }

    private void setCameraManifest() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(MainActivity.this, "需要动态获取权限", Toast.LENGTH_SHORT).show();
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, 0);
            } else {
                CommonMethod.StartActivityForResultCapture(MainActivity.this);
            }
        } else {
            CommonMethod.StartActivityForResultCapture(MainActivity.this);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 0 && grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                CommonMethod.StartActivityForResultCapture(MainActivity.this);
            }
        }
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

}

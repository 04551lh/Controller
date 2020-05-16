package com.example.demo.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.core.app.ActivityCompat;
import com.example.demo.R;
import com.example.demo.base.BaseActivity;
import com.example.demo.utils.CommonMethod;
import com.yzq.zxinglibrary.common.Constant;

import org.jetbrains.annotations.NotNull;

public class HomePageActivity extends BaseActivity implements View.OnClickListener {

    private TextView mTvStandardMachine;
    private TextView mTvNonStandardMachine;
    private Drawable mSelectDrawable;
    private Drawable mNoSelectDrawable;
    private Drawable mArrowDrawable;
    private ImageView mIvSettings;
    // 退出时间
    private long currentBackPressedTime = 0;
    // 退出间隔
    private static final int BACK_PRESSED_INTERVAL = 2000;

    @Override
    public int getLayoutResId() {
        return R.layout.activity_home_page;
    }

    @Override
    public void initViews() {
        mTvStandardMachine = findViewById(R.id.tv_standard_machine);
        mTvNonStandardMachine = findViewById(R.id.tv_non_standard_machine);
        mIvSettings = findViewById(R.id.iv_settings);
        mSelectDrawable = getResources().getDrawable(R.mipmap.select_icon);
        mNoSelectDrawable = getResources().getDrawable(R.drawable.ic_check_box_black_24dp);
        mArrowDrawable = getResources().getDrawable(R.mipmap.arrow_right_icon);
        mSelectDrawable.setBounds(0, 0, mSelectDrawable.getMinimumWidth(), mSelectDrawable.getMinimumHeight());
        mNoSelectDrawable.setBounds(0, 0, mNoSelectDrawable.getMinimumWidth(), mNoSelectDrawable.getMinimumHeight());
        mArrowDrawable.setBounds(0, 0, mArrowDrawable.getMinimumWidth(), mArrowDrawable.getMinimumHeight());
    }

    @Override
    public void setListener() {
        mTvStandardMachine.setOnClickListener(this);
        mTvNonStandardMachine.setOnClickListener(this);
        mIvSettings.setOnClickListener(this);
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_standard_machine:
                Intent mainIntent = new Intent(HomePageActivity.this, MainActivity.class);
                mTvStandardMachine.setCompoundDrawables(mSelectDrawable, null, mArrowDrawable, null);
                mTvNonStandardMachine.setCompoundDrawables(mNoSelectDrawable, null, mArrowDrawable, null);
                startActivity(mainIntent);
                break;
            case R.id.tv_non_standard_machine:
                mTvStandardMachine.setCompoundDrawables(mNoSelectDrawable, null, mArrowDrawable, null);
                mTvNonStandardMachine.setCompoundDrawables(mSelectDrawable, null, mArrowDrawable, null);
                setCameraManifest();
                break;
            case R.id.iv_settings:
                Intent settingIntent = new Intent(HomePageActivity.this, SettingsActivity.class);
                startActivity(settingIntent);
                break;
        }
    }

    private void setCameraManifest() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(HomePageActivity.this, "需要动态获取权限", Toast.LENGTH_SHORT).show();
                ActivityCompat.requestPermissions(HomePageActivity.this, new String[]{Manifest.permission.CAMERA}, 0);
            } else {
                CommonMethod.StartActivityForResultCapture(HomePageActivity.this);
            }
        } else {
            CommonMethod.StartActivityForResultCapture(HomePageActivity.this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NotNull String[] permissions, @NotNull int[] grantResults) {
        if (requestCode == 0 && grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    CommonMethod.StartActivityForResultCapture(HomePageActivity.this);
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
                if (content == null) {
                    Toast.makeText(HomePageActivity.this, "由于网络波动，请退出重新扫描～", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(HomePageActivity.this, DeviceCodeActivity.class);
                intent.putExtra(com.example.demo.network.Constant.TERMINAL_ID, content);
                startActivity(intent);
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                if (System.currentTimeMillis() - currentBackPressedTime > BACK_PRESSED_INTERVAL) {
                    currentBackPressedTime = System.currentTimeMillis();
                    Toast.makeText(this, R.string.double_click_exit, Toast.LENGTH_SHORT).show();
                } else {
                    // 退出
                    finish();
                    return true;
                }
            }
        }
        return false;
    }
}

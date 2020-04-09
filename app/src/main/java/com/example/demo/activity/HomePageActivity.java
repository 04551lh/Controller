package com.example.demo.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.example.demo.R;
import com.example.demo.base.BaseActivity;
import com.yzq.zxinglibrary.android.CaptureActivity;
import com.yzq.zxinglibrary.bean.ZxingConfig;
import com.yzq.zxinglibrary.common.Constant;

public class HomePageActivity extends BaseActivity implements View.OnClickListener {

    private final static String TAG = "HomePageActivity";
    private TextView mTvStandardMachine;
    private TextView mTvNonStandardMachine;
    private Drawable mSelectDrawable;
    private Drawable mNoSelectDrawable;
    private Drawable mArrowDrawable;
    private Intent mIntent;
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
        mSelectDrawable = getResources().getDrawable(R.drawable.ic_check_black_24dp);
        mNoSelectDrawable = getResources().getDrawable(R.drawable.ic_check_box_black_24dp);
        mArrowDrawable = getResources().getDrawable(R.drawable.ic_chevron_right_black_24dp);
        mSelectDrawable.setBounds(0, 0, mSelectDrawable.getMinimumWidth(), mSelectDrawable.getMinimumHeight());
        mNoSelectDrawable.setBounds(0, 0, mNoSelectDrawable.getMinimumWidth(), mNoSelectDrawable.getMinimumHeight());
        mArrowDrawable.setBounds(0, 0, mArrowDrawable.getMinimumWidth(), mArrowDrawable.getMinimumHeight());
    }

    @Override
    public void setListener() {
        mTvStandardMachine.setOnClickListener(this);
        mTvNonStandardMachine.setOnClickListener(this);
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
                mIntent = new Intent(HomePageActivity.this, MainActivity.class);
                mTvStandardMachine.setCompoundDrawables(mSelectDrawable, null, mArrowDrawable, null);
                mTvNonStandardMachine.setCompoundDrawables(mNoSelectDrawable, null, mArrowDrawable, null);
                startActivity(mIntent);
                break;
            case R.id.tv_non_standard_machine:
                mTvStandardMachine.setCompoundDrawables(mNoSelectDrawable, null, mArrowDrawable, null);
                mTvNonStandardMachine.setCompoundDrawables(mSelectDrawable, null, mArrowDrawable, null);
                if (!ismConnected() || !ismConfigured()) {
                    Toast.makeText(HomePageActivity.this, getResources().getString(R.string.please_usb_tip), Toast.LENGTH_SHORT).show();
                    return;
                }
                setCameraManifest();
                break;
        }
    }

    private void setCameraManifest() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(HomePageActivity.this, "需要动态获取权限", Toast.LENGTH_SHORT);
                ActivityCompat.requestPermissions(HomePageActivity.this, new String[]{Manifest.permission.CAMERA}, 0);
            } else {
                Toast.makeText(HomePageActivity.this, "不需要动态获取权限", Toast.LENGTH_SHORT);
                Intent intent = new Intent(HomePageActivity.this, CaptureActivity.class);
                /*ZxingConfig是配置类
                 *可以设置是否显示底部布局，闪光灯，相册，
                 * 是否播放提示音  震动
                 * 设置扫描框颜色等
                 * 也可以不传这个参数
                 * */
                ZxingConfig config = new ZxingConfig();
                config.setPlayBeep(true);//是否播放扫描声音 默认为true
                config.setShake(true);//是否震动  默认为true
                config.setDecodeBarCode(true);//是否扫描条形码 默认为true
//                config.setReactColor(R.color.colorAccent);//设置扫描框四个角的颜色 默认为白色
                config.setFrameLineColor(R.color.colorAccent);//设置扫描框边框颜色 默认无色
                config.setScanLineColor(R.color.colorAccent);//设置扫描线的颜色 默认白色
                config.setFullScreenScan(true);//是否全屏扫描  默认为true  设为false则只会在扫描框中扫描
                intent.putExtra(Constant.INTENT_ZXING_CONFIG, config);
                startActivityForResult(intent, 0);
            }
        } else {
            Intent intent = new Intent(HomePageActivity.this, CaptureActivity.class);
            /*ZxingConfig是配置类
             *可以设置是否显示底部布局，闪光灯，相册，
             * 是否播放提示音  震动
             * 设置扫描框颜色等
             * 也可以不传这个参数
             * */
            ZxingConfig config = new ZxingConfig();
            config.setPlayBeep(true);//是否播放扫描声音 默认为true
            config.setShake(true);//是否震动  默认为true
            config.setDecodeBarCode(true);//是否扫描条形码 默认为true
//                config.setReactColor(R.color.colorAccent);//设置扫描框四个角的颜色 默认为白色
            config.setFrameLineColor(R.color.colorAccent);//设置扫描框边框颜色 默认无色
            config.setScanLineColor(R.color.colorAccent);//设置扫描线的颜色 默认白色
            config.setFullScreenScan(true);//是否全屏扫描  默认为true  设为false则只会在扫描框中扫描
            intent.putExtra(Constant.INTENT_ZXING_CONFIG, config);
            startActivityForResult(intent, 0);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 0 && grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(HomePageActivity.this, CaptureActivity.class);
                    ZxingConfig config = new ZxingConfig();
                    config.setPlayBeep(true);//是否播放扫描声音 默认为true
                    config.setShake(true);//是否震动  默认为true
                    config.setDecodeBarCode(true);//是否扫描条形码 默认为true
                    //                config.setReactColor(R.color.colorAccent);//设置扫描框四个角的颜色 默认为白色
                    config.setFrameLineColor(R.color.colorAccent);//设置扫描框边框颜色 默认无色
                    config.setScanLineColor(R.color.colorAccent);//设置扫描线的颜色 默认白色
                    config.setFullScreenScan(true);//是否全屏扫描  默认为true  设为false则只会在扫描框中扫描
                    intent.putExtra(Constant.INTENT_ZXING_CONFIG, config);
                    startActivityForResult(intent, 0);
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
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void initUsb() {
        //todo
        if (!ismConnected() || !ismConfigured()) {
            Toast.makeText(HomePageActivity.this, getResources().getString(R.string.please_usb_tip), Toast.LENGTH_SHORT).show();
        }
    }
}

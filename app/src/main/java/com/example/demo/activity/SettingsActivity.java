package com.example.demo.activity;

import android.content.Context;
import android.os.Build;
import android.os.StrictMode;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.RequiresApi;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.example.demo.Bean.PlusSpeedBean;
import com.example.demo.Bean.SucceedBean;
import com.example.demo.R;
import com.example.demo.base.BaseActivity;
import com.example.demo.network.Constant;
import com.example.demo.network.OkHttpHelper;
import com.example.demo.utils.MyException;
import com.google.gson.Gson;

public class SettingsActivity extends BaseActivity implements View.OnClickListener, MyException, SwipeRefreshLayout.OnRefreshListener {

    private final static String TAG = "SettingsActivity";
    private ImageView mIvSettingsBack;
    private ImageView mIvPlusSpeed;
    private ImageView mIvSimulationSpeed;
    private TextView mTvSave;
    private SwipeRefreshLayout mSRLSettings;
    private OkHttpHelper mOkHttpHelper;
    private PlusSpeedBean.ResultBean mResultBean;
    private int mSwitchClose;
    private int mSwitchOpen;
    private int mSpeedEnable;
    private int mSimulationEnable;
    private InputMethodManager mInputMethodManager;

    @Override
    public int getLayoutResId() {
        return R.layout.activity_settings;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void initViews() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        mIvSettingsBack = findViewById(R.id.iv_settings_back);
        mIvPlusSpeed = findViewById(R.id.iv_plus_switch);
        mIvSimulationSpeed = findViewById(R.id.iv_simulation_switch);
        mTvSave = findViewById(R.id.tv_save);
        mSRLSettings = findViewById(R.id.srl_settings);
        mOkHttpHelper = OkHttpHelper.getInstance();
        mOkHttpHelper.setMyException(this);
        initData();
        getNetwork();
    }

    @Override
    public void setListener() {
        mSRLSettings.setOnRefreshListener(this);
        mIvSettingsBack.setOnClickListener(this);
        mIvPlusSpeed.setOnClickListener(this);
        mIvSimulationSpeed.setOnClickListener(this);
        mTvSave.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        //键盘处理
        switch (v.getId()) {
            case R.id.iv_settings_back:
                if (mInputMethodManager.isActive()) {
                    mInputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);  //强制隐藏
                }
                finish();
                break;
            case R.id.iv_plus_switch:
                mSpeedEnable = 1;
                mIvPlusSpeed.setImageResource(mSwitchOpen);
                mSimulationEnable = 0;
                mIvSimulationSpeed.setImageResource(mSwitchClose);
                break;
            case R.id.iv_simulation_switch:
                mSpeedEnable = 0;
                mIvPlusSpeed.setImageResource(mSwitchClose);
                mSimulationEnable = 1;
                mIvSimulationSpeed.setImageResource(mSwitchOpen);
                break;
            case R.id.tv_save:
                if (mResultBean == null) {
                    mResultBean = new PlusSpeedBean.ResultBean();
                }
                if (mResultBean.getPulseSpeed() == null) {
                    mResultBean.setPulseSpeed(new PlusSpeedBean.ResultBean.PulseSpeedBean());
                }
                mResultBean.getPulseSpeed().setPulseCoefficient(3600);
                onSave();
                break;
        }
    }

    private void initData(){
        mInputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        mSRLSettings.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light, android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        mSRLSettings.setProgressBackgroundColorSchemeResource(android.R.color.white);
        mSwitchClose = R.drawable.switch_close_icon;
        mSwitchOpen = R.drawable.switch_open_icon;
        mSpeedEnable = 1;
        mIvPlusSpeed.setImageResource(mSwitchOpen);
        mSimulationEnable = 0;
        mIvSimulationSpeed.setImageResource(mSwitchClose);
    }

    private void getNetwork() {
        String response = mOkHttpHelper.post(com.example.demo.network.Constant.GET_SPEEDS_INFO, "");
        if (response == null) {
            Toast.makeText(SettingsActivity.this, getResources().getString(R.string.device_not_responding), Toast.LENGTH_SHORT).show();
            return;
        }
        PlusSpeedBean plusSpeedBean = new Gson().fromJson(response, PlusSpeedBean.class);
        mResultBean = plusSpeedBean.getResult();
        mResultBean.getPulseSpeed().setEnable(mSpeedEnable);
        mResultBean.getPulseSpeed().setAutoCalibration(0);
        mResultBean.getSimulateSpeed().setEnable(mSimulationEnable);
        mResultBean.getWithGPSSpeedEnable().setEnable(0);
    }

    private void onSave() {
        String json = new Gson().toJson(mResultBean);
        String response = mOkHttpHelper.post(Constant.POST_SPEEDS_DATA, json);
        if (response == null) return;
        SucceedBean succeedBean = new Gson().fromJson(response, SucceedBean.class);
        if (succeedBean.getStatuesCode() == 0) {
            Toast.makeText(SettingsActivity.this, "保存成功～", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    public void show(int flag, String str) {
        Toast.makeText(SettingsActivity.this, str, Toast.LENGTH_SHORT).show();
    }

    /**
     * Called when a swipe gesture triggers a refresh.
     */
    @Override
    public void onRefresh() {
        getNetwork();
    }
}

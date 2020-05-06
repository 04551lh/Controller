package com.example.demo.activity;

import android.os.Build;
import android.os.StrictMode;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.RequiresApi;
import com.example.demo.Bean.PlusSpeedBean;
import com.example.demo.Bean.SucceedBean;
import com.example.demo.R;
import com.example.demo.base.BaseActivity;
import com.example.demo.network.Constant;
import com.example.demo.network.OkHttpHelper;
import com.example.demo.utils.MyException;
import com.google.gson.Gson;
public class SettingsActivity extends BaseActivity implements View.OnClickListener, MyException {

    private final static String TAG = "SettingsActivity";
    private ImageView mIvSettingsBack;
    private ImageView mIvRefresh;
    private EditText mEtPlusSpeed;
    private TextView mTvSave;
    private OkHttpHelper mOkHttpHelper;
    private PlusSpeedBean.ResultBean mResultBean;

    @Override
    public int getLayoutResId() {
        return R.layout.activity_settings;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void initViews() {
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        mIvSettingsBack = findViewById(R.id.iv_settings_back);
        mIvRefresh = findViewById(R.id.iv_refresh);
        mEtPlusSpeed = findViewById(R.id.et_plus_speed);
        mTvSave = findViewById(R.id.tv_save);
        mOkHttpHelper = OkHttpHelper.getInstance();
        mOkHttpHelper.setMyException(this);
        getNetwork();
    }

    @Override
    public void setListener() {
        mIvSettingsBack.setOnClickListener(this);
        mIvRefresh.setOnClickListener(this);
        mTvSave.setOnClickListener(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_settings_back:
                finish();
                break;
            case R.id.iv_refresh:
                onRefresh();
                break;
            case R.id.tv_save:
                if (TextUtils.isEmpty(mEtPlusSpeed.getText())) {
                    Toast.makeText(SettingsActivity.this, "请输入脉冲系数～", Toast.LENGTH_SHORT).show();
                    return;
                }
                int speed = Integer.parseInt(mEtPlusSpeed.getText().toString());
                if (speed < 0 || speed > 100) {
                    Toast.makeText(SettingsActivity.this, "脉冲系数取值区间0-100～", Toast.LENGTH_SHORT).show();
                    return;
                }
                Log.i(TAG,"speed:"+speed);
                if(mResultBean ==null){
                    mResultBean = new PlusSpeedBean.ResultBean();
                }
                if(mResultBean.getPulseSpeed() == null){
                    mResultBean.setPulseSpeed(new PlusSpeedBean.ResultBean.PulseSpeedBean());
                }
                mResultBean.getPulseSpeed().setPulseCoefficient(speed * 100);
                onSave();
                break;
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void getNetwork() {
        String response = mOkHttpHelper.post(com.example.demo.network.Constant.GET_SPEEDS_INFO, "");
        if (response == null) {
            Toast.makeText(SettingsActivity.this, getResources().getString(R.string.device_not_responding), Toast.LENGTH_SHORT).show();
            return;
        }
        PlusSpeedBean plusSpeedBean = new Gson().fromJson(response, PlusSpeedBean.class);
        mResultBean = plusSpeedBean.getResult();
        int i = mResultBean.getPulseSpeed().getPulseCoefficient();
        mResultBean.getPulseSpeed().setEnable(1);
        mResultBean.getPulseSpeed().setAutoCalibration(0);
        mResultBean.getSimulateSpeed().setEnable(0);
        mResultBean.getWithGPSSpeedEnable().setEnable(0);
        mEtPlusSpeed.setText(String.format("%d", i / 100));
        mEtPlusSpeed.setSelection(String.valueOf(i / 100).length());
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void onRefresh() {
        getNetwork();
    }

    private void onSave() {
        String json = new Gson().toJson(mResultBean);
        String response  = mOkHttpHelper.post(Constant.POST_SPEEDS_DATA, json);
        if (response == null) return;
        SucceedBean succeedBean = new Gson().fromJson(response, SucceedBean.class);
        if (succeedBean.getStatuesCode() == 0) {
            Toast.makeText(SettingsActivity.this, "保存成功～", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    public void show(int flag,String str) {
        Toast.makeText(SettingsActivity.this, str, Toast.LENGTH_SHORT).show();
    }
}

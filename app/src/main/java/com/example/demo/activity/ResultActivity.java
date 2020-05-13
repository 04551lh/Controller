package com.example.demo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.example.demo.Bean.ConfigBean;
import com.example.demo.Bean.DeviceIdBean;
import com.example.demo.Bean.DeviceUniqueCodeBean;
import com.example.demo.Bean.QRBean;
import com.example.demo.Bean.SucceedBean;
import com.example.demo.R;
import com.example.demo.base.BaseActivity;
import com.example.demo.network.Constant;
import com.example.demo.network.OkHttpHelper;
import com.example.demo.utils.MyException;
import com.google.gson.Gson;

import java.security.MessageDigest;
import java.text.DecimalFormat;

public class ResultActivity extends BaseActivity implements MyException {

    private TextView mTvThreeId;
    private TextView mTvDeviceId;
    private TextView mTvTerminalId;
    private TextView mTvProductId;
    private TextView mTvEntry;
    private String mProductId, mThreeCCode, mDeviceType, mTerminalId,mDate;
    private OkHttpHelper mOkHttpHelper;
    private DeviceIdBean mDeviceIdBean;

    @Override
    public int getLayoutResId() {
        return R.layout.activity_result;
    }

    @Override
    public void initViews() {
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        mTvThreeId = findViewById(R.id.tv_three_id);
        mTvDeviceId = findViewById(R.id.tv_device_id);
        mTvTerminalId = findViewById(R.id.tv_terminal_id);
        mTvProductId = findViewById(R.id.tv_product_id);
        mTvEntry = findViewById(R.id.tv_entry);
        mOkHttpHelper = OkHttpHelper.getInstance();
        mOkHttpHelper.setMyException(this);
        initData();
    }

   private String getDeviceId(){
        String response = mOkHttpHelper.post(Constant.GET_DEVICE_ID, "");
        mDeviceIdBean = new Gson().fromJson(response, DeviceIdBean.class);
        if(mDeviceIdBean == null){finish();return "0800";}
        if(mDeviceIdBean.getStatuesCode() != 0){finish();return "0800";}
        if(mDeviceIdBean.getResult().getProdectKindCode() == null){finish();return "0800";}
        String productKindCode= mDeviceIdBean.getResult().getProdectKindCode().substring(2);
        DecimalFormat decimalFormat =new DecimalFormat("0000");
        String productCode=decimalFormat.format(Integer.parseInt(productKindCode));
        return productCode;
    }

    private void initData() {
        Bundle bundle = getIntent().getExtras();
        mThreeCCode = bundle.getString(Constant.THREE_ID);
        mDeviceType = bundle.getString(Constant.DEIVCE_ID);
        mTerminalId = bundle.getString(Constant.TERMINAL_ID);
        mProductId = bundle.getString(Constant.PRODUCT_ID);
        mDate = bundle.getString(Constant.DATE_CODE);
        String deviceId =  getDeviceId();
        mTvThreeId.setText(mThreeCCode);
        mTvDeviceId.setText(mDeviceType);
        mTvTerminalId.setText(String.format("%s%s", deviceId, mTerminalId));
        mTvProductId.setText(mProductId);
    }

    @Override
    public void setListener() {
        mTvEntry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String terminalId = mTvTerminalId.getText().toString().trim();
                ConfigBean configBean = new ConfigBean();
                configBean.setProducerID(mProductId);
                configBean.setTerminalModel(mDeviceType);
                configBean.setTerminalId(terminalId);
                configBean.setManufactureDate(mDate);
                if(judgeService(mTerminalId)){
                    mTvEntry.setBackground(getResources().getDrawable(R.drawable.hollow_circle));
                    mTvEntry.setTextColor(getResources().getColor(R.color.colorPrimary));
                    String json = new Gson().toJson(configBean);
                    String response = mOkHttpHelper.post(Constant.UPDATA_CONFIG, json);
                    if(response == null)return;
                    SucceedBean succeedBean = new Gson().fromJson(response, SucceedBean.class);
                    if (succeedBean.getStatuesCode() == 0) {
                        Intent intent = new Intent(ResultActivity.this, QRCodeActivity.class);
                        intent.putExtra(Constant.PRODUCT_ID, mProductId);
                        intent.putExtra(Constant.DEIVCE_ID, mDeviceType);
                        startActivity(intent);
                    }
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void show(int flag,String str) {
        Toast.makeText(ResultActivity.this, str, Toast.LENGTH_SHORT).show();
    }

    private boolean judgeService(String qr){
        boolean success = false;
        QRBean qrBean = new QRBean();
        //设备唯一码
        qrBean.setDevice_id(qr);
        //时间戳（UNIX时间戳)
        String timestamp = System.currentTimeMillis()+"";
        qrBean.setTimestamp(timestamp);
        qrBean.setSign(getStrMd5(qr+timestamp));
        //设备标识：0 非部标，1部标
        qrBean.setFlag("1");
        qrBean.setDevice_info(null);
        String json = new Gson().toJson(qrBean);
        String response = mOkHttpHelper.post(Constant.DEVICE_UNIQUE_CODE, json);
        //返回值 ，0：正常；-1:其他错误；-2：SIGN错误，-4：该设备已报备过
        DeviceUniqueCodeBean deviceUniqueCodeBean = new Gson().fromJson(response, DeviceUniqueCodeBean.class);
        if( 0 == deviceUniqueCodeBean.getRet()){
            success = true;
        }else if(-1 == deviceUniqueCodeBean.getRet()) {
            Toast.makeText(ResultActivity.this,"其他错误",Toast.LENGTH_SHORT).show();
        }else if(-2 == deviceUniqueCodeBean.getRet()){
            Toast.makeText(ResultActivity.this,"SIGN错误",Toast.LENGTH_SHORT).show();
        }else if(-4 == deviceUniqueCodeBean.getRet()){
            Toast.makeText(ResultActivity.this,"该设备已报备过",Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(ResultActivity.this,"未知错误",Toast.LENGTH_SHORT).show();
        }
        return success;
    }

    public static String getStrMd5(String msg) {
        char[] hexDigits = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

        try {
            byte[] btInput = msg.getBytes();
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(btInput);
            byte[] md = digest.digest();
            int j = md.length;
            char[] str = new char[j * 2];
            int k = 0;
            for(int i = 0; i < j; ++i) {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 15];
                str[k++] = hexDigits[byte0 & 15];
            }
            return new String(str);
        } catch (Exception var10) {
            var10.printStackTrace();
            return null;
        }
    }
}

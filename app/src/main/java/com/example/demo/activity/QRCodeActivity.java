package com.example.demo.activity;


import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.demo.Bean.ResponseBean;
import com.example.demo.R;
import com.example.demo.base.BaseActivity;
import com.example.demo.fragment.DateFragment;
import com.example.demo.fragment.ImeiFragment;
import com.example.demo.fragment.ProductFragment;
import com.example.demo.network.OkHttpHelper;
import com.google.gson.Gson;
import com.yzq.zxinglibrary.android.CaptureActivity;
import com.yzq.zxinglibrary.common.Constant;

import java.util.List;
import java.util.Objects;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class QRCodeActivity extends BaseActivity implements View.OnClickListener {

    private TextView mTvImeiCode;
    private TextView mTvProductCode;
    private TextView mTvDateCode;
    private TextView mTvHomePage;
    private TextView mTvNext;

    private ImeiFragment mImeiFragment;
    private ProductFragment mProductFragment;
    private DateFragment mDateFragment;

    private String mBackHomePage,mNextPage,mPreviousPage,mReScanning,mProductId;

    private OkHttpHelper mOkHttpHelper = OkHttpHelper.getInstance();
    private ResponseBean mResponseBean;

    @Override
    public int getLayoutResId() {
        return R.layout.activity_qrcode;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void initViews() {
        mImeiFragment = new ImeiFragment();
        mProductFragment = new ProductFragment();
        mDateFragment = new DateFragment();
        mTvImeiCode = findViewById(R.id.tv_imie_code);
        mTvProductCode = findViewById(R.id.tv_product_code);
        mTvDateCode = findViewById(R.id.tv_date_code);
        mTvHomePage = findViewById(R.id.tv_homepage);
        mTvNext = findViewById(R.id.tv_next);
        String response = mOkHttpHelper.post(com.example.demo.network.Constant.GET_CONFIG,"");
        mResponseBean = new Gson().fromJson(response,ResponseBean.class);
        initData();
        initFragment();
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void initData(){
        mBackHomePage = getResources().getString(R.string.back_homepage);
        mNextPage = getResources().getString(R.string.next_page);
        mPreviousPage = getResources().getString(R.string.previous_page);
        mReScanning = getResources().getString(R.string.scanning);
        mProductId = Objects.requireNonNull(getIntent().getExtras()).getString(com.example.demo.network.Constant.PRODUCT_ID);
    }

    @Override
    public void setListener() {
        mTvHomePage.setOnClickListener(this);
        mTvNext.setOnClickListener(this);
        mTvImeiCode.setOnClickListener(this);
        mTvProductCode.setOnClickListener(this);
        mTvDateCode.setOnClickListener(this);
    }

    private void initFragment() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if (!mImeiFragment.isAdded()) {
            Bundle bundle = new Bundle();
            bundle.putString(com.example.demo.network.Constant.IMEI_CODE,mResponseBean.getResult().getImei());
            mImeiFragment.setArguments(bundle);
            fragmentTransaction.add(R.id.fl_contain, mImeiFragment);
        }
        if (!mProductFragment.isAdded()) {
            Bundle bundle = new Bundle();
            bundle.putString(com.example.demo.network.Constant.PRODUCT_CODE,mResponseBean.getResult().getProductCoding());
            mProductFragment.setArguments(bundle);
            fragmentTransaction.add(R.id.fl_contain, mProductFragment);
        }
        if (!mDateFragment.isAdded()) {
            Bundle bundle = new Bundle();
            bundle.putString(com.example.demo.network.Constant.DATE_CODE,mResponseBean.getResult().getManufactureDate());
            mDateFragment.setArguments(bundle);
            fragmentTransaction.add(R.id.fl_contain, mDateFragment);
        }
        hideAllFragment(fragmentTransaction);
        fragmentTransaction.commit();
        clickTab(mImeiFragment);
    }

    private void hideAllFragment(FragmentTransaction fragmentTransaction) {
        fragmentTransaction.hide(mImeiFragment);
        fragmentTransaction.hide(mDateFragment);
        fragmentTransaction.hide(mProductFragment);
    }

    private void clickTab(Fragment fragment) {
        clearSelected();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        hideAllFragment(fragmentTransaction);
        fragmentTransaction.show(fragment);
        fragmentTransaction.commit();
        changeTabStyle(fragment);
    }

    private void changeTabStyle(Fragment fragment) {
        if (fragment instanceof ImeiFragment) {
            mTvImeiCode.setTextColor(Color.parseColor("#5677FC"));
        }

        if (fragment instanceof ProductFragment) {
            mTvProductCode.setTextColor(Color.parseColor("#5677FC"));
        }

        if (fragment instanceof DateFragment) {
            mTvDateCode.setTextColor(Color.parseColor("#5677FC"));
        }
    }

    private void clearSelected() {
        if (!mImeiFragment.isHidden()) {
            mTvImeiCode.setTextColor(Color.BLACK);
        }

        if (!mProductFragment.isHidden()) {
            mTvProductCode.setTextColor(Color.BLACK);
        }

        if (!mDateFragment.isHidden()) {
            mTvDateCode.setTextColor(Color.BLACK);
        }
    }

    private Fragment getCurrentFragment() {
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        for (int i = 0; i < fragments.size(); i++) {
            Fragment fragment = fragments.get(i);
            if (fragment != null && fragment.isVisible()) {
                return fragment;
            }
        }
        return null;
    }


    @Override
    public void onClick(View v) {
        Fragment currentFragment = getCurrentFragment();
        switch (v.getId()){
            case R.id.tv_imie_code:
                clickTab(mImeiFragment);
                mTvHomePage.setText(mBackHomePage);
                mTvNext.setText(mNextPage);
                break;
            case R.id.tv_product_code:
                clickTab(mProductFragment);
                mTvHomePage.setText(mPreviousPage);
                mTvNext.setText(mNextPage);
                break;
            case R.id.tv_date_code:
                clickTab(mDateFragment);
                mTvHomePage.setText(mPreviousPage);
                mTvNext.setText(mReScanning);
                break;

            case R.id.tv_homepage:
                if(mTvHomePage.getText().toString().trim().equals(mBackHomePage))
                startActivity( new Intent(QRCodeActivity.this,MainActivity.class));
                else{
                    if (currentFragment instanceof ProductFragment) {
                        clickTab(mImeiFragment);
                        mTvHomePage.setText(mBackHomePage);
                    }
                    else if (currentFragment instanceof DateFragment) {
                        clickTab(mProductFragment);
                        mTvNext.setText(mNextPage);
                    }
                }
                break;
             case R.id.tv_next:
                 if (currentFragment instanceof ImeiFragment) {
                    clickTab(mProductFragment);
                    mTvHomePage.setText(mPreviousPage);
                 }
                 else if (currentFragment instanceof ProductFragment) {
                     clickTab(mDateFragment);
                     mTvNext.setText(mReScanning);
                 }
                 else if (currentFragment instanceof DateFragment) {
                     Intent openCameraIntent = new Intent(QRCodeActivity.this, CaptureActivity.class);
                     startActivityForResult(openCameraIntent, 0);
                 }
                break;
        }
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
                if(content != null) terminalId = content.substring(content.length()-11);

                Intent intent = new Intent(QRCodeActivity.this, ResultActivity.class);
                intent.putExtra(com.example.demo.network.Constant.THREE_ID, content);
                intent.putExtra(com.example.demo.network.Constant.DEIVCE_ID, deviceId);
                intent.putExtra(com.example.demo.network.Constant.TERMINAL_ID, terminalId);
                intent.putExtra(com.example.demo.network.Constant.PRODUCT_ID, mProductId);
                startActivity(intent);
            }
        }
    }
}

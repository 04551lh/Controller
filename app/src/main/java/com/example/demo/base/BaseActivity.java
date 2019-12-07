package com.example.demo.base;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Created by dell on 2019/12/6 18:40
 * Description:
 * Emain: 1187278976@qq.com
 */
public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResId());
        initViews();
        setListener();
    }

    public abstract int getLayoutResId();

    public abstract void initViews();

    public abstract void setListener();

}

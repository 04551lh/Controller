package com.example.demo.base;

import android.app.Application;

import com.example.demo.network.OkHttpHelper;
import com.tencent.bugly.Bugly;
import com.tencent.bugly.crashreport.CrashReport;

import java.io.IOException;

import okhttp3.OkHttpClient;

/**
 * Created by dell on 2019/12/10 12:12
 * Description:
 * Emain: 1187278976@qq.com
 */
public class BaseAPP extends Application {

    private static BaseAPP instance = null;

    public static BaseAPP getInstance() {
        if (instance==null){
            synchronized (BaseAPP.class){
                if (instance==null){
                    instance=new BaseAPP();
                }
            }
        }
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Bugly.init(getApplicationContext(), "6015c90158", false);
    }
}

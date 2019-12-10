package com.example.demo.base;

import android.app.Application;

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

}

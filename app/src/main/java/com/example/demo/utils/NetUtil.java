package com.example.demo.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

/**
 * Created by dell on 2019/12/9 16:53
 * Description:
 * Emain: 1187278976@qq.com
 */
public class NetUtil {
    //没有网络
    public static final int NETWORK_NONE = 1;
    //移动网络
    private static final int NETWORK_MOBILE = 0;
    //无线网络
    private static final int NETWORW_WIFI = 2;

    //获取网络启动
    public static int getNetWorkStart(Context context) {
        //连接服务
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        //网络信息 NetworkInfo
         NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
            //判断是否是wifi
            if (activeNetworkInfo.getType() == (ConnectivityManager.TYPE_WIFI)) {
                //返回无线网络
//                Toast.makeText(context, "当前处于无线网络", Toast.LENGTH_SHORT).show();
                return NETWORW_WIFI;
                //判断是否移动网络
            } else if (activeNetworkInfo.getType() == (ConnectivityManager.TYPE_MOBILE)) {
//                Toast.makeText(context, "当前处于移动网络", Toast.LENGTH_SHORT).show();
                //返回移动网络
                return NETWORK_MOBILE;
            }
        } else {
            //没有网络
            Toast.makeText(context, "当前没有网络", Toast.LENGTH_SHORT).show();
            return NETWORK_NONE;
        }
        //默认返回  没有网络
        return NETWORK_NONE;
    }
}


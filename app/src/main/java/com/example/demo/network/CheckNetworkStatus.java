package com.example.demo.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * @ProjectName: Demo
 * @Package: com.example.demo.network
 * @ClassName: CheckNetworkStatus
 * @Description: java类作用描述
 * @Author: 作者名
 * @CreateDate: 2020/5/15 3:27 PM
 * @UpdateUser: 更新者：
 * @UpdateDate: 2020/5/15 3:27 PM
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public class CheckNetworkStatus {

    //网络是否可用
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity == null) {
        } else {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}

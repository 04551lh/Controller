package com.example.demo.utils;

import android.app.Activity;
import android.content.Intent;
import com.example.demo.R;
import com.yzq.zxinglibrary.android.CaptureActivity;
import com.yzq.zxinglibrary.bean.ZxingConfig;
import com.yzq.zxinglibrary.common.Constant;

/**
 * @ProjectName: Demo
 * @Package: com.example.demo.utils
 * @ClassName: CommonMethod
 * @Description: 公共方法
 * @Author: yzg
 * @CreateDate: 2020/4/30 10:52 AM
 * @UpdateUser: 更新者：
 * @UpdateDate: 2020/4/30 10:52 AM
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public class CommonMethod {

    //扫描
    public static void StartToCaptureActivity(Activity activity){
        Intent intent = new Intent(activity, CaptureActivity.class);
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
//      config.setReactColor(R.color.colorAccent);//设置扫描框四个角的颜色 默认为白色
        config.setFrameLineColor(R.color.colorAccent);//设置扫描框边框颜色 默认无色
        config.setScanLineColor(R.color.colorAccent);//设置扫描线的颜色 默认白色
        config.setFullScreenScan(true);//是否全屏扫描  默认为true  设为false则只会在扫描框中扫描
        intent.putExtra(Constant.INTENT_ZXING_CONFIG, config);
        activity.startActivityForResult(intent, 0);
    }
}

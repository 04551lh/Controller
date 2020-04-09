package com.example.demo.utils;

import android.app.Dialog;
import android.content.Context;
import com.example.demo.R;

/**
 * Created by dell on 2020/1/7 15:06
 * Description:
 * Emain: 1187278976@qq.com
 */
public class BaseDialog extends Dialog {

    private static BaseDialog mBaseDialog;

    public BaseDialog(Context context) {
        super(context);
    }

    public BaseDialog(Context context, int themeResId) {
        super(context, themeResId);

    }
    //显示dialog的方法
    public static BaseDialog showDialog(Context context){
        mBaseDialog = new BaseDialog(context, R.style.MyDialog1);//dialog样式
        mBaseDialog.setContentView(R.layout.dialog_load_progress);//dialog布局文件
        mBaseDialog.setCanceledOnTouchOutside(false);//点击外部不允许关闭dialog
        mBaseDialog.setCancelable(false);
        return mBaseDialog;
    }
}

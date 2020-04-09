package com.example.demo.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import com.example.demo.R;

/**
 * Created by dell on 2019/12/19 17:59
 * Description:
 * Emain: 1187278976@qq.com
 */
public class DefaultDialog extends Dialog implements View.OnClickListener{
    //在构造方法里提前加载了样式
    private Context context;//上下文
    private String title;
    private String confirm;

    public void setConfirm(String confirm) {
        this.confirm = confirm;
    }

    public DefaultDialog(Context context){
        super(context, R.style.MyDialog);//加载dialog的样式
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //提前设置Dialog的一些样式
//        Window dialogWindow = getWindow();
//        dialogWindow.setGravity(Gravity.CENTER);//设置dialog显示居中

        //dialogWindow.setWindowAnimations();设置动画效果
        setContentView(R.layout.dialog_default);
        WindowManager windowManager = ((Activity)context).getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.width = display.getWidth()*3/5;// 设置dialog宽度为屏幕的4/5
//        lp.width = display.getWidth();// 设置dialog宽度为屏幕的4/5
        getWindow().setAttributes(lp);
        setCanceledOnTouchOutside(false);//点击外部Dialog消失
        //控件id添加点击注册
        TextView tvTitle = findViewById(R.id.tv_title);
        TextView tvConfirm = findViewById(R.id.tv_confirm);
        if(title != null){
            tvTitle.setVisibility(View.VISIBLE);
            tvTitle.setText(title);}else{
            tvTitle.setVisibility(View.GONE);
        }
        if(confirm != null){ tvConfirm.setText(confirm);}
        tvConfirm.setOnClickListener(this);
    }
    private OnCenterItemClickListener listener;

    public void setTitle(String title) {
        this.title = title;
    }

    public interface OnCenterItemClickListener {
        void OnCenterItemClick(DefaultDialog dialog, View view);
    }
    //很明显我们要在这里面写个接口，然后添加一个方法
    public void setOnCenterItemClickListener(OnCenterItemClickListener listener) {
        this.listener = listener;
    }


    @Override
    public void onClick(View v) {
        dismiss();//注意：我在这里加了这句话，表示只要按任何一个控件的id,弹窗都会消失，不管是确定还是取消。
        listener.OnCenterItemClick(this,v);
    }
}
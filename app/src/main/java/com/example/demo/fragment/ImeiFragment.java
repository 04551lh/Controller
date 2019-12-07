package com.example.demo.fragment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.demo.R;
import com.example.demo.base.BaseFragment;
import com.example.demo.network.Constant;
import com.google.zxing.WriterException;
import com.yzq.zxinglibrary.encode.CodeCreator;

import java.util.Objects;

import androidx.annotation.RequiresApi;

public class ImeiFragment extends BaseFragment {

    private ImageView mQRcode;
    private String mQRData;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void findViewById(View view) {
        mQRcode = view.findViewById(R.id.iv_qr_imei);
        mQRData = Objects.requireNonNull(getArguments()).getString(Constant.IMEI_CODE);
    }

    @Override
    public void setViewData(View view) {
        try {
            if (!mQRData.equals("")) {
                //根据字符串生成二维码图片并显示在界面上，第二个参数为图片的大小（350*350）
                Bitmap logo = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
                Bitmap bitmap = CodeCreator.createQRCode(mQRData, 400, 400, logo);
//                Bitmap qrCodeBitmap = EncodingHandler.createQRCode(mQRData, 350);
                mQRcode.setImageBitmap(bitmap);
            } else {
                Toast.makeText(getActivity(), "Text can not be empty", Toast.LENGTH_SHORT).show();
            }
        } catch (WriterException e) {                    // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void setClickEvent(View view) {

    }

    @Override
    public int setLayoutResId() {
        return R.layout.fragment_imei;
    }

}

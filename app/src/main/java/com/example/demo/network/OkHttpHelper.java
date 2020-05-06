package com.example.demo.network;

import android.annotation.TargetApi;
import android.os.Build;
import android.util.Log;
import android.view.View;

import androidx.annotation.RequiresApi;

import com.example.demo.utils.MyException;

import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * Created by dell on 2019/12/2 17:43
 * Description:
 * Emain: 1187278976@qq.com
 */
public class OkHttpHelper {
    private final static String TAG = "YZG.OkHttpHelper";
    //网络请求日志打印
    private HttpLoggingInterceptor mLogging;
    //网络请求
    private OkHttpClient mOkHttpClient;

    private final static int CONNECT_TIMEOUT = 2;
    private final static int READ_TIMEOUT = 2;
    private final static int WRITE_TIMEOUT = 2;

    private static OkHttpHelper instance = null;

    private MyException myException;

    public void setMyException(MyException myException) {
        this.myException = myException;
    }

    private OkHttpHelper() {
        mLogging = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                Log.i(TAG, "--------> " + message);
            }
        });
        mLogging.setLevel(HttpLoggingInterceptor.Level.BODY);

        mOkHttpClient = new OkHttpClient.Builder()
                .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
                .addInterceptor(mLogging)
                .build();
    }

    public static OkHttpHelper getInstance() {
        if (instance == null) {
            instance = new OkHttpHelper();
        }
        return instance;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public String post(String url, String json) {
        Log.d(TAG, "make port --> " + url + " with json " + json);
        RequestBody body = RequestBody.create(Constant.JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        try (Response response = mOkHttpClient.newCall(request).execute()) {
            return response.body().string();
        }
        catch (SocketTimeoutException e) {
            Log.i(TAG, e.toString());
            myException.show(0,"网络异常，请检查网络或者重新打开USB网络共享再试~");
        } catch (SocketException e) {
            Log.i(TAG, e.toString());
            myException.show(1,"网络异常，请检查网络或者重新打开USB网络共享再试~");
        }
        catch (IOException e) {
            Log.i(TAG, e.toString());
            e.printStackTrace();
            myException.show(2,e.toString());
        }
        return null;
    }

}

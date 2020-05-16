package com.example.demo.network;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.os.Build;
import android.util.Log;

import com.example.demo.utils.MyException;

import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import okhttp3.FormBody;
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
    //网络请求
    private OkHttpClient mOkHttpClient;

    private final static int CONNECT_TIMEOUT = 2;
    private final static int READ_TIMEOUT = 2;
    private final static int WRITE_TIMEOUT = 2;
    private Map<String, String> params = null;
    private static OkHttpHelper instance = null;
    private MyException myException;
    X509TrustManager trustManager;

    public Map<String, String> getParams() {
        return params;
    }

    public void addParam(String key, String value) {
        this.params.put(new String(key), value);
    }

    public void setMyException(MyException myException) {
        this.myException = myException;
    }

    private OkHttpHelper() {
        //网络请求日志打印
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor(message -> Log.i(TAG, "--------> " + message));

        trustManager = new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {

            }

            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {

            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }
        };
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        mOkHttpClient = new OkHttpClient.Builder()
                .sslSocketFactory(createSSLSocketFactory(), trustManager)
                .hostnameVerifier(new TrustAllHostnameVerifier())
                .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
                .addInterceptor(logging)
                .build();

//        mOkHttpClient = new OkHttpClient();
//        mOkHttpClient.newBuilder().sslSocketFactory(createSSLSocketFactory());
//        mOkHttpClient.newBuilder().hostnameVerifier(new TrustAllHostnameVerifier());
//        mOkHttpClient.newBuilder().connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS);
//        mOkHttpClient.newBuilder().readTimeout(READ_TIMEOUT, TimeUnit.SECONDS);
//        mOkHttpClient.newBuilder().writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS);
//        mOkHttpClient.newBuilder().addInterceptor(logging);
//        mOkHttpClient.newBuilder().build();
        this.params = new HashMap<String, String>();
    }

    //okHttp3添加信任所有证书
    @SuppressLint("TrulyRandom")
    private static SSLSocketFactory createSSLSocketFactory() {

        SSLSocketFactory sSLSocketFactory = null;

        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, new TrustManager[]{new TrustAllManager()},
                    new SecureRandom());
            sSLSocketFactory = sc.getSocketFactory();
        } catch (Exception e) {
        }

        return sSLSocketFactory;
    }

    private static class TrustAllManager implements X509TrustManager {
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType)

                throws CertificateException {
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }

    private static class TrustAllHostnameVerifier implements HostnameVerifier {
        @Override
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
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
        } catch (SocketTimeoutException e) {
            Log.i(TAG, e.toString());
            myException.show(0, "网络异常，请检查网络或者重新打开USB网络共享再试~");
        } catch (SocketException e) {
            Log.i(TAG, e.toString());
            myException.show(1, "网络异常，请检查网络或者重新打开USB网络共享再试~");
        } catch (IOException e) {
            Log.i(TAG, e.toString());
            e.printStackTrace();
            myException.show(2, e.toString());
        }catch (NullPointerException e){

        }
        return null;
    }

    private RequestBody getFormatData(Map<String, String> params) {
        FormBody.Builder builder = new FormBody.Builder();
        if (params != null && params.size() > 0)
            for (Map.Entry<String, String> entry : params.entrySet()) {
                builder.add(entry.getKey(), entry.getValue());
            }
        return builder.build();
    }


    public String getParamWithString(String url) {
        if (params == null || params.size() < 1)
            return url;
        StringBuilder sb = new StringBuilder();
        if (url.indexOf("http://") == 0
                || url.indexOf("https://") == 0) {
            sb.append(url + "?");
        }

        for (Map.Entry<String, String> entry : params.entrySet()) {
            sb.append(entry.getKey()).append("=").append(entry.getValue())
                    .append("&");
        }
        return sb.toString().substring(0, (sb.toString().length() - 1));
    }
}

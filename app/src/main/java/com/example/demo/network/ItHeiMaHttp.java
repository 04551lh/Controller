package com.example.demo.network;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;

import java.io.IOException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * @ProjectName: Demo
 * @Package: com.example.demo.network
 * @ClassName: ItHeiMaHttp
 * @Description: java类作用描述
 * @Author: 作者名
 * @CreateDate: 2020/5/14 10:39 AM
 * @UpdateUser: 更新者：
 * @UpdateDate: 2020/5/14 10:39 AM
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public class ItHeiMaHttp {

    private final static String TAG = "ItHeiMaHttp";
    private final OkHttpClient okHttpClient;
    private Map<String, String> params = null;
    private Map<String, String> heads = null;

    private Gson gson;
    private final Handler handler;

    private ItHeiMaHttp() {
        okHttpClient = new OkHttpClient();
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                Log.i(TAG, "--------> " + message);
            }
        });
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        okHttpClient.newBuilder().connectTimeout(10, TimeUnit.SECONDS);
        okHttpClient.newBuilder().writeTimeout(10, TimeUnit.SECONDS);
        okHttpClient.newBuilder().readTimeout(10, TimeUnit.SECONDS);
        okHttpClient.newBuilder().addInterceptor(logging);
        okHttpClient.newBuilder().sslSocketFactory(getSSLSocketFactory());
        okHttpClient.newBuilder().hostnameVerifier(getHostnameVerifier());
        gson = new Gson();
        handler = new Handler(Looper.myLooper());
        this.params = new HashMap<String, String>();
        this.heads = new HashMap<String, String>();
    }


    public Map<String, String> getParams() {
        return params;
    }


    public ItHeiMaHttp addParam(String key, String value) {
        this.params.put(new String(key), value);
        return this;
    }

    public Map<String, String> getHeads() {
        return heads;
    }


    public ItHeiMaHttp addHeads(String key, String value) {
        this.heads.put(new String(key), value);
        return this;
    }


    public static ItHeiMaHttp httpManager;

    public static ItHeiMaHttp getInstance() {
        if (httpManager == null) {
            synchronized (ItHeiMaHttp.class) {
                httpManager = new ItHeiMaHttp();
            }
        }
        return httpManager;
    }

    public void get(String url, WSCallBack bcb) {
        Request request = buildRequest(url, RequestType.GET);
        doRequest(request, bcb);
    }

    public void post(String url, WSCallBack bcb) {
        Request request = buildRequest(url, RequestType.POST);
        doRequest(request, bcb);
    }


    private Request buildRequest(String url, RequestType type) {
        Request.Builder builder = new Request.Builder();
        if (type == RequestType.GET) {
            url = getParamWithString(url);
            builder.get();
        } else if (type == RequestType.POST) {
            RequestBody requestBody = getFormatData(params);
            builder.post(requestBody);
        }
        builder.url(url);
        addAllHeads(builder);
        return builder.build();
    }

    private void addAllHeads(Request.Builder builder) {
        if (heads.size() > 0) {
            for (Map.Entry<String, String> entry : heads.entrySet()) {
                builder.addHeader(entry.getKey(), entry.getValue());
            }
        }
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

    private RequestBody getFormatData(Map<String, String> params) {
        FormBody.Builder builder = new FormBody.Builder();
        if (params != null && params.size() > 0)
            for (Map.Entry<String, String> entry : params.entrySet()) {
                builder.add(entry.getKey(), entry.getValue());
            }
        return builder.build();
    }

    enum RequestType {
        GET,
        POST
    }

    private void doRequest(Request request, final WSCallBack baseCallBack) {

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                sendFaile(baseCallBack, call, e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String json = response.body().string();
                    sendSuccess(json, call, baseCallBack);
                } else {
                    sendFaile(baseCallBack, call, null);
                }
            }
        });

    }

    private void sendFaile(final WSCallBack bcb, final Call call, final Exception e) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                bcb.onFailure(call, e);
            }
        });
    }

    private void sendSuccess(final String json, final Call call, final WSCallBack bcb) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (bcb.type == String.class) {
                    bcb.onSuccess(json);
                } else {
                    try {
                        Object object = gson.fromJson(json, bcb.type);
                        bcb.onSuccess(object);
                    } catch (JsonParseException e) {
                        sendFaile(bcb, call, e);
                    }
                }
            }
        });
    }

    //okHttp3添加信任所有证书
    //获取这个SSLSocketFactory
    public static SSLSocketFactory getSSLSocketFactory() {
        try {
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, getTrustManager(), new SecureRandom());
            return sslContext.getSocketFactory();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    //获取TrustManager
    private static TrustManager[] getTrustManager() {
        TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    @Override
                    public void checkClientTrusted(X509Certificate[] chain, String authType) {
                    }

                    @Override
                    public void checkServerTrusted(X509Certificate[] chain, String authType) {
                    }

                    @Override
                    public X509Certificate[] getAcceptedIssuers() {
                        return new X509Certificate[0];
                    }
                }
        };
        return trustAllCerts;
    }

    //获取HostnameVerifier
    public static HostnameVerifier getHostnameVerifier() {
        HostnameVerifier hostnameVerifier = new HostnameVerifier() {
            @Override
            public boolean verify(String s, SSLSession sslSession) {
                return true;
            }
        };
        return hostnameVerifier;
    }
}

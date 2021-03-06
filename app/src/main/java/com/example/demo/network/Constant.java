package com.example.demo.network;

import okhttp3.MediaType;

/**
 * Created by dell on 2019/12/2 18:24
 * Description:
 * Emain: 1187278976@qq.com
 */
public class Constant {

    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    //3CId
    public final static String THREE_ID = "3C_Id";
    //设备型号
    public final static String DEIVCE_ID = "device_Id";
    //终端ID
    public final static String TERMINAL_ID = "terminal_Id";
    //厂商ID
    public final static String PRODUCT_ID = "product_Id";

    //IMEI
    public final static String IMEI_CODE = "imei";
    //产品编码
    public final static String PRODUCT_CODE = "product";
    //日期编码
    public final static String DATE_CODE = "date";

    //基础URL
    public static final String WIFI_SERVER_IP_ADDRESS = "192.168.1.1:8000";

    public static final String WIFI_SERVER_IP_ADDRESS1 = "172.16.0.192:8000";

    public static final String USB_SERVER_IP_ADDRESS = "192.168.42.254:8000";

    public static final String BASE_URL = "http://"+ USB_SERVER_IP_ADDRESS;

    public static final String UPDATA_CONFIG = BASE_URL + "/factoryTerminalInfoConfig";

    public static final String GET_CONFIG = BASE_URL + "/factoryTerminalInfoRequest";

    public static final String GET_DEVICE_ID = BASE_URL +"/factoryProdectKindRequest";

    public static final String ACTION_USB_STATE = "android.hardware.usb.action.USB_STATE";
    //后台服务验证
    public final static String  TEST_DEVICE_UNIQUE_CODE= "https://cloud.background.adasplus.com/device_info_report_insert";

//    public final static String  TEST_DEVICE_UNIQUE_CODE= "https://test.cloud.background.adasplus.com/device_info_report_insert";
    //速度信息获取
    public static final String GET_SPEEDS_INFO = BASE_URL +"/speedRequest";
    //速度信息配置
    public static final String POST_SPEEDS_DATA =BASE_URL+"/speedConfig";
}

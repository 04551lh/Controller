package com.example.demo.Bean;

/**
 * @ProjectName: Demo
 * @Package: com.example.demo.Bean
 * @ClassName: QRBean
 * @Description: java类作用描述
 * @Author: 作者名
 * @CreateDate: 2020/5/13 11:20 AM
 * @UpdateUser: 更新者：
 * @UpdateDate: 2020/5/13 11:20 AM
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public class QRBean {
    private String device_id;
    private String timestamp;
    private String sign;
    private String device_info;
    private String flag;

    public String getDevice_id() {
        return device_id;
    }

    public void setDevice_id(String device_id) {
        this.device_id = device_id;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getDevice_info() {
        return device_info;
    }

    public void setDevice_info(String device_info) {
        this.device_info = device_info;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }
}

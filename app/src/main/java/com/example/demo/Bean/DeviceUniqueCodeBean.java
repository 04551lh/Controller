package com.example.demo.Bean;

/**
 * @ProjectName: Demo
 * @Package: com.example.demo.Bean
 * @ClassName: DeviceUniqueCodeBean
 * @Description: java类作用描述
 * @Author: 作者名
 * @CreateDate: 2020/5/13 11:12 AM
 * @UpdateUser: 更新者：
 * @UpdateDate: 2020/5/13 11:12 AM
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public class DeviceUniqueCodeBean {

    /**
     * ret : 0
     * device_id : device_id
     * msg : 返回信息
     */

    private int ret;
    private String device_id;
    private String msg;

    public int getRet() {
        return ret;
    }

    public void setRet(int ret) {
        this.ret = ret;
    }

    @Override
    public String toString() {
        return "DeviceUniqueCodeBean{" +
                "ret=" + ret +
                ", device_id='" + device_id + '\'' +
                ", msg='" + msg + '\'' +
                '}';
    }

    public String getDevice_id() {
        return device_id;
    }

    public void setDevice_id(String device_id) {
        this.device_id = device_id;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}

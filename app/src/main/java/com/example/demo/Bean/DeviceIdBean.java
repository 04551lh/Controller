package com.example.demo.Bean;

public class DeviceIdBean {


    /**
     * StatuesCode : 0
     * Result : {"ProdectKindCode":"800"}
     */

    private int StatuesCode;
    private ResultBean Result;

    public int getStatuesCode() {
        return StatuesCode;
    }

    @Override
    public String toString() {
        return "DeviceIdBean{" +
                "StatuesCode=" + StatuesCode +
                ", Result=" + Result +
                '}';
    }

    public void setStatuesCode(int StatuesCode) {
        this.StatuesCode = StatuesCode;
    }

    public ResultBean getResult() {
        return Result;
    }

    public void setResult(ResultBean Result) {
        this.Result = Result;
    }

    public static class ResultBean {
        /**
         * ProdectKindCode : 800
         * "mac":""
         */

        private String ProdectKindCode;
        private String mac;

        public String getMac() {
            return mac;
        }

        public void setMac(String mac) {
            this.mac = mac;
        }

        public String getProdectKindCode() {
            return ProdectKindCode;
        }

        public void setProdectKindCode(String ProdectKindCode) {
            this.ProdectKindCode = ProdectKindCode;
        }
    }
}

package com.example.demo.Bean;

/**
 * Created by dell on 2019/12/7 12:18
 * Description:
 * Emain: 1187278976@qq.com
 */
public class ResponseBean {

    /**
     * StatuesCode : 0
     * Result : {"imei":"16546465145","productCoding":"1632210221","manufactureDate":"2010-10-10"}
     */

    private int StatuesCode;
    private ResultBean Result;

    public int getStatuesCode() {
        return StatuesCode;
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
         * imei : 16546465145
         * productCoding : 1632210221
         * manufactureDate : 2010-10-10
         */

        private String imei;
        private String productCoding;
        private String manufactureDate;

        public String getImei() {
            return imei;
        }

        public void setImei(String imei) {
            this.imei = imei;
        }

        public String getProductCoding() {
            return productCoding;
        }

        public void setProductCoding(String productCoding) {
            this.productCoding = productCoding;
        }

        public String getManufactureDate() {
            return manufactureDate;
        }

        public void setManufactureDate(String manufactureDate) {
            this.manufactureDate = manufactureDate;
        }
    }
}

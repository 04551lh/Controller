package com.example.demo.Bean;

/**
 * @ProjectName: Demo
 * @Package: com.example.demo.Bean
 * @ClassName: PlusSpeedBean
 * @Description: java类作用描述
 * @Author: 作者名
 * @CreateDate: 2020/4/24 4:40 PM
 * @UpdateUser: 更新者：
 * @UpdateDate: 2020/4/24 4:40 PM
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public class PlusSpeedBean {


    /**
     * StatusCode : 0
     * Result : {"pulseSpeed":{"enable":1,"pulseCoefficient":1000,"autoCalibration":0,"allowErrorValue":0},"simulateSpeed":{"enable":0,"value":0},"withGPSSpeedEnable":{"enable":0}}
     */

    private int StatusCode;
    private ResultBean Result;

    public int getStatusCode() {
        return StatusCode;
    }

    public void setStatusCode(int StatusCode) {
        this.StatusCode = StatusCode;
    }

    public ResultBean getResult() {
        return Result;
    }

    public void setResult(ResultBean Result) {
        this.Result = Result;
    }

    public static class ResultBean {
        /**
         * pulseSpeed : {"enable":1,"pulseCoefficient":1000,"autoCalibration":0,"allowErrorValue":0}
         * simulateSpeed : {"enable":0,"value":0}
         * withGPSSpeedEnable : {"enable":0}
         */

        private PulseSpeedBean pulseSpeed;
        private SimulateSpeedBean simulateSpeed;
        private WithGPSSpeedEnableBean withGPSSpeedEnable;

        public PulseSpeedBean getPulseSpeed() {
            return pulseSpeed;
        }

        public void setPulseSpeed(PulseSpeedBean pulseSpeed) {
            this.pulseSpeed = pulseSpeed;
        }

        public SimulateSpeedBean getSimulateSpeed() {
            return simulateSpeed;
        }

        public void setSimulateSpeed(SimulateSpeedBean simulateSpeed) {
            this.simulateSpeed = simulateSpeed;
        }

        public WithGPSSpeedEnableBean getWithGPSSpeedEnable() {
            return withGPSSpeedEnable;
        }

        public void setWithGPSSpeedEnable(WithGPSSpeedEnableBean withGPSSpeedEnable) {
            this.withGPSSpeedEnable = withGPSSpeedEnable;
        }

        public static class PulseSpeedBean {
            /**
             * enable : 1
             * pulseCoefficient : 1000
             * autoCalibration : 0
             * allowErrorValue : 0
             */

            private int enable;
            private int pulseCoefficient;
            private int autoCalibration;
            private int allowErrorValue;

            public int getEnable() {
                return enable;
            }

            public void setEnable(int enable) {
                this.enable = enable;
            }

            public int getPulseCoefficient() {
                return pulseCoefficient;
            }

            public void setPulseCoefficient(int pulseCoefficient) {
                this.pulseCoefficient = pulseCoefficient;
            }

            public int getAutoCalibration() {
                return autoCalibration;
            }

            public void setAutoCalibration(int autoCalibration) {
                this.autoCalibration = autoCalibration;
            }

            public int getAllowErrorValue() {
                return allowErrorValue;
            }

            public void setAllowErrorValue(int allowErrorValue) {
                this.allowErrorValue = allowErrorValue;
            }
        }

        public static class SimulateSpeedBean {
            /**
             * enable : 0
             * value : 0
             */

            private int enable;
            private int value;

            public int getEnable() {
                return enable;
            }

            public void setEnable(int enable) {
                this.enable = enable;
            }

            public int getValue() {
                return value;
            }

            public void setValue(int value) {
                this.value = value;
            }
        }

        public static class WithGPSSpeedEnableBean {
            /**
             * enable : 0
             */

            private int enable;

            public int getEnable() {
                return enable;
            }

            public void setEnable(int enable) {
                this.enable = enable;
            }
        }
    }
}

package com.sjx.serialhelperlibrary;

import com.hoho.android.usbserial.driver.UsbSerialPort;

public class SerialConfig {

    private int baudRate = 9600;
    private int dataBits = 8;
    private int stopBits = UsbSerialPort.STOPBITS_1;
    private int parity = UsbSerialPort.PARITY_NONE;
    private int dataMaxSize = 30000; // 未满足要求的最大字节数，超过就清空
    private int doubleBufferSize = 30; // 30个大小, 双缓冲的容器大小
    private Long readInterval = 10L; // 读取休眠间隔，单位ms，这个值不能太大，读取速度要大于写入速度

    private int timeout = 2000;

    private boolean dtr = true;
    private boolean rts = true;
    private boolean autoConnect = false;

    public int getBaudRate() {
        return baudRate;
    }

    public SerialConfig setBaudRate(int baudRate) {
        this.baudRate = baudRate;
        return this;
    }

    public int getDataBits() {
        return dataBits;
    }

    public SerialConfig setDataBits(int dataBits) {
        this.dataBits = dataBits;
        return this;
    }

    public int getStopBits() {
        return stopBits;
    }

    public SerialConfig setStopBits(int stopBits) {
        this.stopBits = stopBits;
        return this;
    }

    public int getParity() {
        return parity;
    }

    public SerialConfig setParity(int parity) {
        this.parity = parity;
        return this;
    }

    public boolean isDtr() {
        return dtr;
    }

    public SerialConfig setDtr(boolean dtr) {
        this.dtr = dtr;
        return this;
    }

    public boolean isRts() {
        return rts;
    }

    public SerialConfig setRts(boolean rts) {
        this.rts = rts;
        return this;
    }

    public boolean isAutoConnect() {
        return autoConnect;
    }

    public SerialConfig setAutoConnect(boolean autoConnect) {
        this.autoConnect = autoConnect;
        return this;
    }

    public int getTimeout() {
        return timeout;
    }

    public SerialConfig setTimeout(int timeout) {
        this.timeout = timeout;
        return this;
    }

    public int getDataMaxSize() {
        return dataMaxSize;
    }

    public void setDataMaxSize(int dataMaxSize) {
        this.dataMaxSize = dataMaxSize;
    }

    public int getDoubleBufferSize() {
        return doubleBufferSize;
    }

    public void setDoubleBufferSize(int doubleBufferSize) {
        this.doubleBufferSize = doubleBufferSize;
    }

    public Long getReadInterval() {
        return readInterval;
    }

    public void setReadInterval(Long readInterval) {
        this.readInterval = readInterval;
    }
}
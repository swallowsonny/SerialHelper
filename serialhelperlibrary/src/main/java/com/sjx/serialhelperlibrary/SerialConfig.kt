package com.sjx.serialhelperlibrary

import com.hoho.android.usbserial.driver.UsbSerialPort

class SerialConfig {

    var baudRate = 9600 // 波特率
    var dataBits = 8 // 数据位
    var stopBits = UsbSerialPort.STOPBITS_1 // 停止位
    var parity = UsbSerialPort.PARITY_NONE // 校验位
    var dataMaxSize = 30000 // 未满足要求的最大字节数，超过就清空
    var doubleBufferSize = 30 // 30个大小, 双缓冲的容器大小
    var readInterval: Long = 10L // 读取休眠间隔，单位ms，这个值不能太大，读取速度要大于写入速度

    var timeout = 2000

    var dtr = true
    var rts = true
    var autoConnect = false
}
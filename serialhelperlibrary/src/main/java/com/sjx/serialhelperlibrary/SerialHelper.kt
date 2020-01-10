package com.sjx.serialhelperlibrary

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbDeviceConnection
import android.hardware.usb.UsbManager
import android.os.Handler
import android.os.Parcelable
import com.hoho.android.usbserial.driver.UsbSerialPort
import com.hoho.android.usbserial.driver.UsbSerialProber
import com.hoho.android.usbserial.util.SerialInputOutputManager
import java.io.IOException
import java.lang.IllegalArgumentException
import java.util.concurrent.Executors

abstract class SerialHelper : CheckFullFrame {
//    private val WRITE_WAIT_MILLIS = 2000 // 0 blocked infinitely on unprogrammed arduino
    private val ACTION_USB_PERMISSION = "com.sjx.serialhelperlibrary.USB_PERMISSION"
    private val onUsbStatusChangeListeners = ArrayList<OnUsbStatusChangeListener>()
    private val onUsbDataListeners = ArrayList<OnUsbDataListener>()
    private var usbManager: UsbManager? = null
    private var serialConfig = SerialConfig()
    private var mContext: Context? = null
    private var usbSerialPort: UsbSerialPort? = null
    private var usbIoManager: SerialInputOutputManager? = null
    private var connection: UsbDeviceConnection? = null

    private val mUsbPermissionActionReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (UsbManager.ACTION_USB_DEVICE_ATTACHED == action) {
                onUsbStatusChangeListeners.forEach { it.onUsbDeviceAttached() }
                // 设备连接上，申请权限
                requestPermission()
            } else if (UsbManager.ACTION_USB_DEVICE_DETACHED == action) {
                onUsbStatusChangeListeners.forEach { it.onUsbDeviceDetached() }
                // 设备拔出，断开连接
                disconnectDevice();
            } else if (ACTION_USB_PERMISSION == action) {
                synchronized(this) {
                    val usbDevice =
                        intent.getParcelableExtra<Parcelable>(UsbManager.EXTRA_DEVICE) as UsbDevice
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        // 用户点击确定，拥有权限
                        onUsbStatusChangeListeners.forEach { it.onPermissionGranted() }
                        if (serialConfig.isAutoConnect) {
                            // 连接设备
                            connectDevice(usbDevice)
                        }
                    } else {
                        // 权限拒绝
                        onUsbStatusChangeListeners.forEach { it.onPermissionDenied() }
                    }
                }
            }
        }
    }

    fun initConfig(serialConfig: SerialConfig): SerialHelper {
        this.serialConfig = serialConfig
        return this
    }

    private fun requestPermission() {
        val mPermissionIntent =
            PendingIntent.getBroadcast(mContext, 0, Intent(ACTION_USB_PERMISSION), 0)
        usbManager?.let {
            for (usbDevice in it.deviceList.values) {
                if (it.hasPermission(usbDevice)) {
                    if (serialConfig.isAutoConnect) {
                        // 连接设备
                        connectDevice(usbDevice)
                    }
                } else {
                    it.requestPermission(usbDevice, mPermissionIntent)
                }
            }
        }
    }

    fun getAllDevices() = usbManager?.deviceList?.values

    // 连接设备
    fun connectDevice(usbDevice: UsbDevice) {
        // 判断是支持改设备
        val availableDrivers = UsbSerialProber.getDefaultProber().findAllDrivers(usbManager)
        if (availableDrivers.isEmpty()) {
            onUsbStatusChangeListeners.forEach { it.onDeviceNotSupport() }
            return
        }
        // 连接第一个可用的驱动
        val driver = availableDrivers.find { it.device == usbDevice }
        if (driver == null){
            onUsbStatusChangeListeners.forEach { it.onUsbConnectError(IllegalArgumentException("未找到该设备")) }
            return
        }
        try {
            connection = usbManager?.openDevice(driver.device)
            if (connection == null) {
                requestPermission()
                return
            }
            // 读取端口0数据
            usbSerialPort = driver.ports[0]
            try {
                usbSerialPort?.apply {
                    open(connection)
                    setParameters(
                        serialConfig.baudRate,
                        serialConfig.dataBits,
                        serialConfig.stopBits,
                        serialConfig.parity
                    )
                    dtr = serialConfig.isDtr
                    rts = serialConfig.isRts
                    onUsbStatusChangeListeners.forEach { it.onUsbConnect(usbDevice) }
                }
            } catch (e: IOException) {
                e.printStackTrace()
                onUsbStatusChangeListeners.forEach { it.onUsbConnectError(e) }
            }

            // 开启数据接收线程
            usbIoManager = SerialInputOutputManager(usbSerialPort, serialManagerListener)
            Executors.newSingleThreadExecutor().submit(usbIoManager!!);
        } catch (e: SecurityException) {
            e.printStackTrace()
            onUsbStatusChangeListeners.forEach { it.onUsbConnectError(e) }
        }
    }

    private var lastReceiveTime: Long = 0
    private val dataArray = ArrayList<Byte>()
    private val serialManagerListener = object : SerialInputOutputManager.Listener {
        override fun onRunError(e: Exception?) {
            onUsbDataListeners.forEach { it.onDataError(e) }
        }

        override fun onNewData(data: ByteArray?) {
            if (data == null) return;
            println(data.joinToString())
            if (System.currentTimeMillis() - lastReceiveTime > serialConfig.intervalFrame) {
                // 超时
                dataArray.clear()
            }
            lastReceiveTime = System.currentTimeMillis()
            dataArray.addAll(data.toList())
            if (isFullFrame(dataArray.toByteArray())){
                onUsbDataListeners.forEach { it.onDataReceived(dataArray.toByteArray()) }
            }
        }
    }

    fun write(byteArray: ByteArray){
        usbSerialPort?.write(byteArray, serialConfig.timeout)
    }

    // 断开连接
    fun disconnectDevice() {
        usbIoManager?.apply {
            listener = null
            stop()
            usbIoManager = null
        }
        usbSerialPort?.apply {
            try {
                close()
                usbSerialPort = null
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        connection?.apply {
            close()
            connection = null
        }
        onUsbStatusChangeListeners.forEach { it.onUsbDisconnect() }
    }

    // usb设备插入监听
    fun onCreate(context: Context) {
        mContext = context
        val usbFilter = IntentFilter()
        usbFilter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED)
        usbFilter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED)
        usbFilter.addAction(ACTION_USB_PERMISSION)
        context.registerReceiver(mUsbPermissionActionReceiver, usbFilter)
        usbManager = context.getSystemService(Context.USB_SERVICE) as UsbManager
        // 刚启动
        disconnectDevice()
        // 连接设备
        Handler().postDelayed({
            val devices = getAllDevices()
            if (devices != null && devices.isNotEmpty()){
                requestPermission()
            }
        }, 100)
    }

    fun onDestory() {
        mContext?.unregisterReceiver(mUsbPermissionActionReceiver)
        disconnectDevice()
        onUsbStatusChangeListeners.clear()
    }

    fun addOnUsbStatusChangeListener(onUsbStatusChangeListener: OnUsbStatusChangeListener) {
        onUsbStatusChangeListeners.add(onUsbStatusChangeListener)
    }

    fun removeOnUsbStatusChangeListener(onUsbStatusChangeListener: OnUsbStatusChangeListener) {
        val index = onUsbStatusChangeListeners.indexOf(onUsbStatusChangeListener)
        if(index != -1){
            onUsbStatusChangeListeners.removeAt(index)
        }
    }

    fun removeAllUsbStatusChangeListener() {
        onUsbStatusChangeListeners.clear()
    }

    fun addOnUsbDataListener(onUsbDataListener: OnUsbDataListener) {
        onUsbDataListeners.add(onUsbDataListener)
    }

    fun removeOnUsbDataListener(onUsbDataListener: OnUsbDataListener) {
        val index = onUsbDataListeners.indexOf(onUsbDataListener)
        if(index != -1){
            onUsbDataListeners.removeAt(index)
        }
    }

    fun removeAllUsbDataListener() {
        onUsbStatusChangeListeners.clear()
    }
}

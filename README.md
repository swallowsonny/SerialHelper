## SerialHelper

> 一个基于[usb-serial-for-android](https://github.com/mik3y/usb-serial-for-android "usb-serial-for-android")封装的Android串口通讯框架, 搭配[ConvertExt](https://github.com/swallowsonny/ConvertExt)实现ByteArray与基本数据类型的快速高效解析转换
[ ![Download](https://api.bintray.com/packages/swallowsonny/ext/serialhelper/images/download.svg?version=1.0.1) ](https://bintray.com/swallowsonny/ext/serialhelper/1.0.1/link)
### 功能简介
- 框架处理权限请求问题
- 波特率设置
- 数据位设置
- 停止位设置
- 校验位设置
- 支持DTS和RTS
- 超时时长设置
- 串口连接状态监听
- 串口数据发送及数据监听

### 快速开始
#### 基本配置
- 在项目的build.gradle中添加

```
maven {
	url  "https://dl.bintray.com/swallowsonny/ext"
}
```

- 在app的build.gradle中添加
```
implementation 'com.swallowsonny:serialhelper:1.0.1'
```

- 在res/xml目录中添加[device_filter.xml](https://github.com/swallowsonny/SerialHelper/blob/master/app/src/main/res/xml/device_filter.xml)

- 配置AndroidManifest.xml文件
```xml
<activity
    android:name="..."
    ...>
    <intent-filter>
        <action android:name="android.hardware.usb.action.USB_DEVICE_ATTACHED" />
    </intent-filter>
    <meta-data
        android:name="android.hardware.usb.action.USB_DEVICE_ATTACHED"
        android:resource="@xml/device_filter" />
</activity>
```
#### 快速使用
- 在onCreate() 方法中初始化SerialHelper
```kotlin
serialHelper = object : SerialHelper(){
    override fun isFullFrame(data: ArrayList<Byte>): Boolean {
        // 判断是否是完整帧
        return true
    }
}
// 配置串口参数
val serialConfig = SerialConfig()
serialConfig.baudRate = 9600
serialConfig.isAutoConnect = true // 自动连接第一个设备
// 初始化
serialHelper.initConfig(serialConfig).onCreate(this)
```

- 在onDestory()中销毁SerialHelper
```kotlin
serialHelper.onDestory()
```

- 状态与数据监听
```kotlin
// 状态监听
serialHelper.addOnUsbStatusChangeListener(object : OnUsbStatusChangeListener {
    override fun onUsbDeviceAttached() {
        Log.i("StatusChange", "onUsbDeviceAttached")
    }

    override fun onUsbDeviceDetached() {
        Log.i("StatusChange", "onUsbDeviceDetached")
    }

    override fun onPermissionGranted() {
        Log.i("StatusChange", "onPermissionGranted")
    }

    override fun onPermissionDenied() {
        Log.i("StatusChange", "onPermissionDenied")
    }

    override fun onDeviceNotSupport() {
        Log.i("StatusChange", "onDeviceNotSupport")
    }

    override fun onUsbConnect(usbDevice: UsbDevice) {
        Log.i("StatusChange", "onUsbConnect")
    }

    override fun onUsbConnectError(e: Exception) {
        Log.i("StatusChange", "onUsbConnectError")
    }

    override fun onUsbDisconnect() {
        Log.i("StatusChange", "onUsbDisconnect")
    }
})

// 数据监听
serialHelper.addOnUsbDataListener(object : OnUsbDataListener {
    override fun onDataError(e: Exception?) {
        // 数据异常
    }

    override fun onDataReceived(bytes: ByteArray) {
        runOnUiThread {
            // 处理返回的数据, 当前线程为子线程
        }
    }
})
```
- 数据发送
``` kotlin
serialHelper.write(sendBytes)
```



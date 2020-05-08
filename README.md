## SerialHelper

> 一个基于[usb-serial-for-android](https://github.com/mik3y/usb-serial-for-android "usb-serial-for-android")封装的Android串口通讯框架, 搭配[ConvertExt](https://github.com/swallowsonny/ConvertExt)实现ByteArray与基本数据类型的快速高效解析转换。该库配置简单，已用于某工业产品，波特率高达921600

[![Download](https://api.bintray.com/packages/swallowsonny/ext/serialhelper/images/download.svg?version=2.0.2) ](https://bintray.com/swallowsonny/ext/serialhelper/2.0.2/link)
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
- 数据双缓冲，解决高波特率，大数据量下的混乱问题
- 波特率高达921600, 有少数情况丢帧; 波特率115200稳定运行

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
implementation 'com.swallowsonny:serialhelper:2.0.2'
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
// 串口配置
val serialConfig = SerialConfig()
serialConfig.autoConnect = true // 默认连接第一个
serialConfig.baudRate = 9600 // 串口波特率
serialConfig.readInterval = 10 // ms，子线程读取，休眠间隔，双缓冲读取与写速率调整，默认10ms
serialConfig.doubleBufferSize = 20 // 双缓冲容量大小，循环覆盖缓存
serialConfig.dataMaxSize = 30000  // 当拼接数据未找到完整帧，长度大于30000清空，可根据实际情况适当调整
serialHelper =object : SerialHelper(serialConfig){
    override fun isFullFrame(data: ByteArray): IntArray {
        // 子线程 根据自己的完整帧判断方式 返回数据的起始索引和结束索引
        // 示例中有ByteUtils工具类，查找帧头帧尾的索引号 
        // ByteUtils.getIndexRange(data, startBytes, endBytes)
        return intArrayOf(0, data.size)
    }
}
serialHelper.onCreate(this)
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
        // 处理返回的数据, 当前线程为子线程
        runOnUiThread {
            
        }
    }
})
```
- 数据发送
``` kotlin
serialHelper.write(sendBytes)
```



package com.sjx.serialhelper

import android.hardware.usb.UsbDevice
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.sjx.serialhelperlibrary.OnUsbDataListener
import com.sjx.serialhelperlibrary.OnUsbStatusChangeListener
import com.sjx.serialhelperlibrary.SerialConfig
import com.sjx.serialhelperlibrary.SerialHelper
import com.swallowsonny.convertextlibrary.hex2ByteArray
import com.swallowsonny.convertextlibrary.toHexString
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    val startBytes = byteArrayOf(0xA4.toByte(), 0xA3.toByte(), 0xA2.toByte(), 0xA1.toByte())
    val endBytes = byteArrayOf(0xA1.toByte(), 0xA2.toByte(), 0xA3.toByte(), 0xA1.toByte())

    private lateinit var serialHelper: SerialHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ll_received.setOnLongClickListener {
            ll_received.removeAllViews()
            true
        }

        val serialConfig = SerialConfig()
        serialConfig.isAutoConnect = true
        serialHelper =object : SerialHelper(serialConfig){
            override fun isFullFrame(data: ByteArray): IntArray {
                // 子线程 返回数据的起始索引和结束索引
                return getIndexRange(data, startBytes, endBytes)
            }
        }
        serialHelper.addOnUsbDataListener(object : OnUsbDataListener {
            override fun onDataReceived(bytes: ByteArray) {
                runOnUiThread {
                    addText("onDataReceived: ${bytes.toHexString()}")
                }
            }

            override fun onDataError(e: Exception?) {
                runOnUiThread {
                    addText("onDataError: ${e?.message}")
                }
            }
        })

        serialHelper.addOnUsbStatusChangeListener(object : OnUsbStatusChangeListener {
            override fun onUsbDeviceAttached() {
                addText("onUsbDeviceAttached")
            }

            override fun onUsbDeviceDetached() {
                addText("onUsbDeviceDetached")
            }

            override fun onPermissionGranted() {
                addText("onPermissionGranted")
            }

            override fun onPermissionDenied() {
                addText("onPermissionDenied")
            }

            override fun onDeviceNotSupport() {
                addText("onDeviceNotSupport")
            }

            override fun onUsbConnect(usbDevice: UsbDevice) {
                addText("onUsbConnect")
            }

            override fun onUsbConnectError(e: Exception) {
                addText("onUsbConnectError")
            }

            override fun onUsbDisconnect() {
                addText("onUsbDisconnect")

            }
        })

        serialHelper.initConfig(serialConfig)
            .onCreate(this)

        btn_send.setOnClickListener {
            val bst = et_send.text.toString()
            serialHelper.write(bst.hex2ByteArray())
        }

    }

    private fun addText(text: String){
        val tv = TextView(this)
        tv.text = text
        ll_received.addView(tv)
    }


    override fun onDestroy() {
        super.onDestroy()
        serialHelper.onDestory()
    }

    /**
     * ByteArray中查找数组索引
     */
    fun getIndexRange(byteArray: ByteArray, startByteArray: ByteArray, endByteArray: ByteArray): IntArray{
        // 查找头
        var resultStartIndex = -1
        var resultEndIndex = -1
        var startI = 0
        while (startI < byteArray.size - startByteArray.size){
            // 查找
            var findStart = true
            for(i in startByteArray.indices){
                findStart = true
                if (byteArray[startI + i] != startByteArray[i]){
                    findStart = false
                    break
                }
            }
            if(findStart){
                resultStartIndex = startI
                break
            }
            startI ++
        }

        var endI = startI + startByteArray.size
        while (endI < byteArray.size - endByteArray.size){
            // 查找
            var findEnd = true
            for(i in endByteArray.indices){
                findEnd = true
                if (byteArray[endI + i] != endByteArray[i]){
                    findEnd = false
                    break
                }
            }
            if(findEnd){
                resultEndIndex = endI + endByteArray.size
                break
            }
            endI ++
        }

        return intArrayOf(resultStartIndex, resultEndIndex)
    }
}

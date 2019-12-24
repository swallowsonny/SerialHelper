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

    private lateinit var serialHelper: SerialHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ll_received.setOnLongClickListener {
            ll_received.removeAllViews()
            true
        }

        serialHelper =object : SerialHelper(){
            override fun isFullFrame(data: ArrayList<Byte>): Boolean {

                return true
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

        val serialConfig = SerialConfig()
        serialConfig.isAutoConnect = true
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
}

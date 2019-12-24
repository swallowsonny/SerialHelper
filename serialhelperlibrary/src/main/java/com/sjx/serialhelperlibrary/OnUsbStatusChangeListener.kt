package com.sjx.serialhelperlibrary

import android.hardware.usb.UsbDevice

interface OnUsbStatusChangeListener {
    fun onUsbDeviceAttached()
    fun onUsbDeviceDetached()
    fun onPermissionGranted()
    fun onPermissionDenied()
    fun onDeviceNotSupport()
    fun onUsbConnect(usbDevice: UsbDevice)
    fun onUsbConnectError(e: Exception)
    fun onUsbDisconnect()
}

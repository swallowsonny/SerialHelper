package com.sjx.serialhelperlibrary

interface OnUsbDataListener {
    fun onDataReceived(bytes: ByteArray)
    fun onDataError(e: Exception?)
}

package com.sjx.serialhelper

object Test {
    val startBytes = byteArrayOf(0xA4.toByte(), 0xA3.toByte(), 0xA2.toByte(), 0xA1.toByte())
    val endBytes = byteArrayOf(0xA1.toByte(), 0xA2.toByte(), 0xA3.toByte(), 0xA1.toByte())
    @JvmStatic
    fun main(args: Array<String>) {
        val s = byteArrayOf(0xA4.toByte(), 0xA3.toByte(), 0xA2.toByte(), 0xA1.toByte(), 0xA1.toByte(), 0xA2.toByte(), 0xA3.toByte(), 0xA1.toByte())
        val r = ByteUtils.getIndexRange(s, startBytes, endBytes)
        println("${r[0]} -- ${r[1]}")
    }
}

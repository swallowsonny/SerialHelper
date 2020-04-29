package com.sjx.serialhelperlibrary

interface CheckFullFrame {
    /**
     * @param data 需要校验的数据
     * @return 返回根据自己规则对应的起始索引与终止索引号， intArrayOf(0, data.size)
     */
    fun isFullFrame(data: ByteArray): IntArray
}
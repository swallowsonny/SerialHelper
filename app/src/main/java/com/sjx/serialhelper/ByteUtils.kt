package com.sjx.serialhelper

object ByteUtils {
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
        while (endI < byteArray.size - endByteArray.size + 1){
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
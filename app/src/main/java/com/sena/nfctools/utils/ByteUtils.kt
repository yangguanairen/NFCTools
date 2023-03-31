package com.sena.nfctools.utils


/**
 * FileName: ByteUtils
 * Author: JiaoCan
 * Date: 2023/3/28 11:38
 */

object ByteUtils {

    private val hex = arrayOf(
        "0", "1", "2", "3", "4", "5", "6", "7",
        "8", "9", "A", "B", "C", "D", "E", "F"
    )

    fun byteArrayToHexString(byteArray: ByteArray, isNeedHead: Boolean = false, separator: String = ""): String {
        val list = arrayListOf<String>()
        byteArray.forEach {
            list.add(byteToHexString(it, isNeedHead))
        }
        return list.joinToString(separator)
    }

    fun byteToHexString(byte: Byte, isNeedHead: Boolean = false): String {
        val num = byte.toInt() and 0xFF
        val high = (num shr 4) and 0x0F
        val low = num and 0x0F
        val highHex = hex[high]
        val lowHex = hex[low]
        return if (isNeedHead) {
            "0x$highHex$lowHex"
        } else {
            "$highHex$lowHex"
        }
    }

    // 只能接收无前缀，以及separator == " "的字符串
    fun hexStringToByteArray(s: String): ByteArray {
        val array = s.split(" ")
        val result = ByteArray(array.size)
        array.forEachIndexed { i, b ->
            result[i] = b.toByte()
        }
        return result
    }

    fun shortToHexString(short: Short): String {
        return byteToHexString(short.toByte(), true)
    }


}


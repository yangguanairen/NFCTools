package com.sena.nfctools.other

import android.nfc.Tag
import android.nfc.tech.MifareClassic
import com.sena.nfctools.utils.ByteUtils
import com.sena.nfctools.utils.nfcv.RF15693


/**
 * FileName: Test
 * Author: JiaoCan
 * Date: 2023/4/11 17:53
 */

object Test {

    /**
     * 暴力破解KeyA
     * https://www.cnblogs.com/strengthen/p/16083846.html
     */
    fun bruteForce (tag: Tag) {
        val mifareClassic = MifareClassic.get(tag)
        mifareClassic.connect()

        val keyList = arrayOf(
            byteArrayOf(0xFF.toByte(), 0xFF.toByte(), 0xFF.toByte(), 0xFF.toByte(), 0xFF.toByte(), 0xFF.toByte()),
            byteArrayOf(0xA0.toByte(), 0xA1.toByte(), 0xA2.toByte(), 0xA3.toByte(), 0xA4.toByte(), 0xA5.toByte()),
            byteArrayOf(0xD3.toByte(), 0xF7.toByte(), 0xD3.toByte(), 0xF7.toByte(), 0xD3.toByte(), 0xF7.toByte()),
            byteArrayOf(0x00.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte()),
            byteArrayOf(0xA0.toByte(), 0xB0.toByte(), 0xC0.toByte(), 0xD0.toByte(), 0xE0.toByte(), 0xF0.toByte()),
            byteArrayOf(0xA1.toByte(), 0xB1.toByte(), 0xC1.toByte(), 0xD1.toByte(), 0xE1.toByte(), 0xF1.toByte()),
            byteArrayOf(0xB0.toByte(), 0xB1.toByte(), 0xB2.toByte(), 0xB3.toByte(), 0xB4.toByte(), 0xB5.toByte()),
            byteArrayOf(0x4D.toByte(), 0x3A.toByte(), 0x99.toByte(), 0xC3.toByte(), 0x51.toByte(), 0xDD.toByte()),
            byteArrayOf(0x1A.toByte(), 0x98.toByte(), 0x2C.toByte(), 0x7E.toByte(), 0x45.toByte(), 0x9A.toByte()),
            byteArrayOf(0xAA.toByte(), 0xBB.toByte(), 0xCC.toByte(), 0xDD.toByte(), 0xEE.toByte(), 0xFF.toByte()),
            byteArrayOf(0xB5.toByte(), 0xFF.toByte(), 0x67.toByte(), 0xCB.toByte(), 0xA9.toByte(), 0x51.toByte()),
            byteArrayOf(0x71.toByte(), 0x4C.toByte(), 0x5C.toByte(), 0x88.toByte(), 0x6E.toByte(), 0x97.toByte()),
            byteArrayOf(0x58.toByte(), 0x7E.toByte(), 0xE5.toByte(), 0xF9.toByte(), 0x35.toByte(), 0x0F.toByte()),
            byteArrayOf(0xA0.toByte(), 0x47.toByte(), 0x8C.toByte(), 0xC3.toByte(), 0x90.toByte(), 0x91.toByte()),
            byteArrayOf(0x53.toByte(), 0x3C.toByte(), 0xB6.toByte(), 0xC7.toByte(), 0x23.toByte(), 0xF6.toByte()),
            byteArrayOf(0x24.toByte(), 0x02.toByte(), 0x00.toByte(), 0x00.toByte(), 0xDB.toByte(), 0xFD.toByte()),
            byteArrayOf(0x00.toByte(), 0x00.toByte(), 0x12.toByte(), 0xED.toByte(), 0x12.toByte(), 0xED.toByte()),
            byteArrayOf(0x8F.toByte(), 0xD0.toByte(), 0xA4.toByte(), 0xF2.toByte(), 0x56.toByte(), 0xE9.toByte()),
            byteArrayOf(0xEE.toByte(), 0x9B.toByte(), 0xD3.toByte(), 0x61.toByte(), 0xB0.toByte(), 0x1B.toByte()),
        )
        keyList.forEach {
            if (mifareClassic.authenticateSectorWithKeyA(0, it)) {
                println("破解0扇区成功: ${ByteUtils.byteArrayToHexString(it, separator = " ")}")
            }
        }

        mifareClassic.close()
    }

    fun readNfcV(tag: Tag) {

        val iCodeSliX = RF15693.get(tag)
        try {
            iCodeSliX.connect()
            val data = iCodeSliX.getSystemInfo()
//            println(ByteUtils.byteArrayToHexString(data ?: ByteArray(0), separator = "  "))
        } catch (e: Exception) {
            e.printStackTrace()
        }
        iCodeSliX.runCatching { close() }

//        val nfcV = NfcV.get(tag) ?: return
//        nfcV.connect()
//
//
//        // flag bit meaning
//        // bit7  子载波状态, 0 单载波, 1 双载波(不支持), 所以恒为0
//        // bit6  Data_rate_flag
//        // bit5 是否是命令库以外的命令 0 yes, 1 no
//        // bit4 是否扩展protocol
//
//
//        try {
//
//            val cmd = ByteArray(12)
//            cmd[0] = 0x42 // 0x60
//            cmd[1] = 0x23
//            System.arraycopy(tag.id, 0, cmd, 2, tag.id.size)
//            cmd[10] = 0 // offset
//            cmd[11] = 1 // count, 0代表一次, 一次递增
//            val res = nfcV.transceive(cmd)
//            if (res[0] == 0x00.toByte()) {
//                println("正确: " + ByteUtils.byteArrayToHexString(res, separator = " "))
//            } else {
//                println("错误: " + ByteUtils.byteArrayToHexString(res, separator = " "))
//            }
//        } catch (e: Exception) {
//            e.printStackTrace()
//        } finally {
//            nfcV.runCatching { close() }
//        }



    }
}


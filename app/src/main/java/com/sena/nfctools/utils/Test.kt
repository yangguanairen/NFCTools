package com.sena.nfctools.utils

import android.nfc.Tag
import android.nfc.tech.MifareClassic


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
}


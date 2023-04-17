package com.sena.nfctools.utils.m0

import android.nfc.Tag
import android.nfc.tech.MifareUltralight
import android.nfc.tech.Ndef
import android.nfc.tech.NfcA


/**
 * FileName: M1UltralightUtils
 * Author: JiaoCan
 * Date: 2023/4/13 17:21
 */

object M0Utils {

    private fun invoke(tag: Tag, function: (mifareUltralight: MifareUltralight) -> Unit): Boolean {


        val mifareUltralight = MifareUltralight.get(tag)
        val result = mifareUltralight?.runCatching {
            mifareUltralight.connect()
            function.invoke(this)
        }?.onFailure {
            it.printStackTrace()
        }
        mifareUltralight?.runCatching { close() }

        return result?.isSuccess ?: false
    }

    fun read(tag: Tag) {
        val result = invoke(tag) { mifareUltralight ->
            val type = mifareUltralight.type
            val a = NfcA.get(tag)

            val ndef = Ndef.get(tag)
            val size = ndef.maxSize
            val page = size / 4

            // 读取0-3页
            val tPage1 = mifareUltralight.readPages(0)

            // Page0-2
            val manufacturerData = tPage1.slice(0..11)
            // Page3
            val capabilityContainer = tPage1.slice(12..15)
            // Page130
            val dynamicLockBytes = mifareUltralight.readPages(130).slice(0..3)
            // Page131-134
            val configurationPages = mifareUltralight.readPages(131)


            // 序列号
            val id = byteArrayOf(manufacturerData[0], manufacturerData[1], manufacturerData[2],
                manufacturerData[4], manufacturerData[5], manufacturerData[6],manufacturerData[7])
            // 校验位1
            val checkByte1 = manufacturerData[3]
            // 校验位2
            val checkByte2 = manufacturerData[8]
            // 保留字
            val internal = manufacturerData[9]
            // 静态锁字节
            val lockByte1 = manufacturerData[10]
            val lockByte2 = manufacturerData[11]
            // 动态锁字节
            val lockByte3 = dynamicLockBytes[0]
            val lockByte4 = dynamicLockBytes[1]
            val lockByte5 = dynamicLockBytes[2]
            // 可用容量  => 可读写页数 cap / 4 = 124
            val capacity = when (capabilityContainer[2]) {
                0x12.toByte() -> 144 // NTAG213  => 总页数  45, 总容量  45 * 4 = 180
                0x3E.toByte() -> 496 // NTAG215  => 总页数 135, 总容量 135 * 4 = 540
                0x6D.toByte() -> 872 // NTAG216  => 总页数 231, 总容量 231 * 4 = 924
                else -> -1
            }
            val totalPage = when (capacity) {
                144 -> 45
                496 -> 135
                872 -> 231
                else -> -1
            }
//            val data = ByteArray(4)
//            for (i in 0 until totalPage + 1 step 4) {
//                val t = mifareUltralight.readPages(i)
//                val buffer = ByteBuffer.wrap(t)
//                for (j in 0 until 4) {
//                    if (i + j < totalPage) {
//                        buffer.get(data)
//                        println("页数: ${ByteUtils.byteToHexString((i + j).toByte(), isNeedHead = true)}, data: ${ByteUtils.byteArrayToHexString(data, separator = " ")}")
//                    }
//                }
//            }


//            for (i in 0 until 256 step 4) {
//                val data = mifareUltralight.readPages(i)
//                println("$i - ${i*4}, data: ${ByteUtils.byteArrayToHexString(data, separator = " ")}")
//            }
        }
    }

    private fun readLockByte1(lockByte1: Int) {
        // Lx 锁定Pagex只读, BLX锁定Lx只读
        // 锁定为不可逆操作
        val L7 = (lockByte1 shr 7) and 0x01
        val L6 = (lockByte1 shr 6) and 0x01
        val L5 = (lockByte1 shr 5) and 0x01
        val L4 = (lockByte1 shr 4) and 0x01
        val LCC = (lockByte1 shr 3) and 0x01  // CC, 即Page03H
        val BL15_10 = (lockByte1 shr 2) and 0x01
        val BL9_4 = (lockByte1 shr 1) and 0x01
        val BLCC = (lockByte1 shr 0) and 0x01 // 锁定LCC锁定位只读
    }

    private fun readLockByte2(lockByte2: Int) {
        val L15 = (lockByte2 shr 7) and 0x01
        val L14 = (lockByte2 shr 6) and 0x01
        val L13 = (lockByte2 shr 5) and 0x01
        val L12 = (lockByte2 shr 4) and 0x01
        val L11 = (lockByte2 shr 3) and 0x01
        val L10 = (lockByte2 shr 2) and 0x01
        val L9 = (lockByte2 shr 1) and 0x01
        val L8 = (lockByte2 shr 1) and 0x01
    }

    private fun readLockByte3(lockByte3: Int) {
        val lockPage128_129 = (lockByte3 shr 7) and 0x01
        val lockPage112_127 = (lockByte3 shr 6) and 0x01
        val lockPage96_111 = (lockByte3 shr 5) and 0x01
        val lockPage80_95 = (lockByte3 shr 4) and 0x01
        val lockPage64_79 = (lockByte3 shr 3) and 0x01
        val lockPage48_63 = (lockByte3 shr 2) and 0x01
        val lockPage32_47 = (lockByte3 shr 1) and 0x01
        val lockPage16_31 = (lockByte3 shr 0) and 0x01
    }

    private fun readLockByte4(lockByte4: Int) {
        // all bit is RFUI, it is 0B
    }

    private fun readLockByte5(lockByte5: Int) {
        // bit7 is RFUI, is is 0B
        val BL208_225 = (lockByte5 shr 6) and 0x01
        val BL176_207 = (lockByte5 shr 5) and 0x01
        val BL144_175 = (lockByte5 shr 4) and 0x01
        val BL112_143 = (lockByte5 shr 3) and 0x01
        val BL80_111 = (lockByte5 shr 2) and 0x01
        val BL48_79 = (lockByte5 shr 1) and 0x01
        val BL16_47 = (lockByte5 shr 0) and 0x01
    }

}


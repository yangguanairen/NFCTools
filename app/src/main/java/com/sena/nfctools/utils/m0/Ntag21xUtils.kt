package com.sena.nfctools.utils.m0

import android.nfc.Tag
import android.nfc.tech.MifareUltralight
import android.nfc.tech.Ndef
import android.nfc.tech.NfcA
import com.sena.nfctools.newBean.Ntag21xData
import com.sena.nfctools.newBean.Ntag21xPage
import com.sena.nfctools.utils.ByteUtils
import java.nio.ByteBuffer
import kotlin.experimental.xor


/**
 * FileName: Ntag21xUtils
 * Author: JiaoCan
 * Date: 2023/4/14 14:30
 */

object Ntag21xUtils {

    fun read(tag: Tag): Ntag21xData? {
        val m0 = MifareUltralight.get(tag) ?: return null
        try {
            m0.connect()

            val t = m0.readPages(0)
            val cc = t.slice(12..15)
            val type = when (m0.type) {
                MifareUltralight.TYPE_ULTRALIGHT_C -> "TYPE_ULTRALIGHT_C"
                MifareUltralight.TYPE_ULTRALIGHT -> "TYPE_ULTRALIGHT"
                else -> "TYPE_UNKNOWN"
            }
            when (cc[2].toInt()) {
                0x12 -> {
                    return Ntag21xData(
                        type, 144, 45 * 4,
                        144 / 4, 45, readPages(m0, 0, 45)
                    )
                }
                0x3E -> {
                    return Ntag21xData(
                        type, 496, 135 * 4,
                        496 / 4, 135, readPages(m0, 0, 135)
                    )
                }
                0x6D -> {
                    return Ntag21xData(
                        type, 872, 231 * 4,
                        872 / 4, 231, readPages(m0, 0, 231)
                    )
                }
                else -> {
                    println("非NTAG21x系列卡片")
                    return null
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        } finally {
            m0.runCatching { close() }
        }
    }

    // 闭包[start, end]
    private fun readPages(m0: MifareUltralight, start: Int, end: Int): List<Ntag21xPage> {
        val result = arrayListOf<Ntag21xPage>()
        val t = (end - start) % 4
        val finalEnd = if (t == 3) end else end + 3 - t

        var buffer: ByteBuffer
        val data = ByteArray(4)
        for (i in start until finalEnd step 4) {
            buffer = ByteBuffer.wrap(m0.readPages(i))
            for (j in 0 until 4) {
                if (i + j <= end) {
                    buffer.get(data)
                    println("readPage: ${i + j}, data: ${ByteUtils.byteArrayToHexString(data, separator = " ")}")
                    result.add(Ntag21xPage(i + j, data))
                }
            }
            buffer.clear()
        }
        return result
    }

//    fun read(tag: Tag): Map<String, String> {
//        val result = mutableMapOf<String, String>()
//
//        val m0 = MifareUltralight.get(tag)
//        m0?.runCatching {
//            connect()
//
//            val type = m0.type
//
//            val tP = m0.readPages(0)
//            val md = tP.slice(0..11)
//            val cc = tP.slice(12..15)
//
//            val id = byteArrayOf(md[0], md[1], md[2], md[4], md[5], md[6], md[7])
//            val checkByte1 = md[3]
//            if ((0x88 xor md[0].toInt() xor md[1].toInt() xor md[2].toInt()) != checkByte1.toInt()) {
//                println("校验位1错误")
//            }
//            val checkByte2 = md[8]
//            if ((md[4] xor md[5] xor md[6] xor md[7]) != checkByte2) {
//                println("校验位2错误")
//            }
//            var cardName = ""
//            var totalPage = -1
//            val canAccessSize = when (cc[2]) {
//                0x12.toByte() -> {
//                    cardName = "NTAG213"
//                    totalPage = 45
//                    144
//                }
//                0x3E.toByte() -> {
//                    cardName = "NTAG215"
//                    totalPage = 135
//                    496
//                }
//                0x6D.toByte() -> {
//                    cardName = "NTAG216"
//                    totalPage = 231
//                    872
//                }
//                else -> throw Exception("未知的卡片")
//            }
//            result["标签类型"] = cardName
//            result["序列号"] = ByteUtils.byteArrayToHexString(id, separator = ":")
//            result["现有技术"] = tag.techList.joinToString(separator = ", ")
//            result["内存信息"] = "${totalPage * 4} bytes, $totalPage 页 (4 bytes 每页)"
//            result["大小"] = "0 / $canAccessSize 字节"
//
//        }?.onFailure {
//            it.printStackTrace()
//            result.clear()
//        }
//        m0?.run { close() }
//
//        val nfcA = NfcA.get(tag)
//        nfcA?.runCatching {
//            connect()
//
//            result["ATQA"] = ByteUtils.byteArrayToHexString(atqa, isNeedHead = true)
//            result["SAK"] = ByteUtils.byteToHexString(sak.toByte(), isNeedHead = true)
//        }?.onFailure { it.printStackTrace() }
//        nfcA?.runCatching { close() }
//
//        val ndef = Ndef.get(tag)
//        ndef?.runCatching {
//            connect()
//
//            result["可写"] = if (isWritable) "是" else "否"
//            result["可为只读"] = if (canMakeReadOnly()) "是" else "否"
//
//            ndefMessage?.records?.forEachIndexed { index, ndefRecord ->
//                result["记录${index + 1}"] = String(ndefRecord.payload)
//            }
//
//        }?.onFailure {
//            it.printStackTrace()
//        }
//        ndef?.runCatching { close() }
//
//
//        return result
//    }

    fun format(tag: Tag): Boolean {
        return invoke(tag) { m0 ->

            val t = m0.readPages(0)
            val cc = t.slice(12..15)
            val type = cc[2].toInt()
            val fEndPage = when (type) {
                0x12 -> {
                    44 - 4
                }
                0x3E -> {
                    134 - 4
                }
                0x6D -> {
                    230 - 4
                }
                else -> -1
            }
            for (i in 4 until fEndPage) {
                println("格式化: $i")
                if (i == 4) {
                    m0.writePage(i, firstMemoryData)
                } else {
                    m0.writePage(i, emptyData)
                }
            }
        }

    }

    private val emptyData = byteArrayOf(0x00, 0x00, 0x00, 0x00)
    private val firstMemoryData = byteArrayOf(0x3E, 0x00, 0xFE.toByte(), 0x00)

    private fun invoke(tag: Tag, function: (m0: MifareUltralight) -> Unit): Boolean {
        val m = MifareUltralight.get(tag)
        val result = m?.runCatching {
            connect()
            function.invoke(this)
        }?.onFailure {
            it.printStackTrace()
        }
        m?.runCatching { close() }
        return result?.isSuccess == true
    }

}


package com.sena.nfctools.utils

import android.nfc.Tag
import android.nfc.tech.MifareUltralight
import android.nfc.tech.Ndef
import android.nfc.tech.NfcA
import kotlin.experimental.xor


/**
 * FileName: Ntag21xUtils
 * Author: JiaoCan
 * Date: 2023/4/14 14:30
 */

object Ntag21xUtils {

    fun read(tag: Tag): Map<String, String> {
        val result = mutableMapOf<String, String>()

        val m0 = MifareUltralight.get(tag)
        m0?.runCatching {
            connect()

            val type = m0.type

            val tP = m0.readPages(0)
            val md = tP.slice(0..11)
            val cc = tP.slice(12..15)

            val id = byteArrayOf(md[0], md[1], md[2], md[4], md[5], md[6], md[7])
            val checkByte1 = md[3]
            if ((0x88 xor md[0].toInt() xor md[1].toInt() xor md[2].toInt()) != checkByte1.toInt()) {
                println("校验位1错误")
            }
            val checkByte2 = md[8]
            if ((md[4] xor md[5] xor md[6] xor md[7]) != checkByte2) {
                println("校验位2错误")
            }
            var cardName = ""
            var totalPage = -1
            val canAccessSize = when (cc[2]) {
                0x12.toByte() -> {
                    cardName = "NTAG213"
                    totalPage = 45
                    144
                }
                0x3E.toByte() -> {
                    cardName = "NTAG215"
                    totalPage = 135
                    496
                }
                0x6D.toByte() -> {
                    cardName = "NTAG216"
                    totalPage = 231
                    872
                }
                else -> throw Exception("未知的卡片")
            }
            result["标签类型"] = cardName
            result["序列号"] = ByteUtils.byteArrayToHexString(id, separator = ":")
            result["现有技术"] = tag.techList.joinToString(separator = ", ")
            result["内存信息"] = "${totalPage * 4} bytes, $totalPage 页 (4 bytes 每页)"
            result["大小"] = "0 / $canAccessSize 字节"

        }?.onFailure {
            it.printStackTrace()
            result.clear()
        }
        m0?.run { close() }

        val nfcA = NfcA.get(tag)
        nfcA?.runCatching {
            connect()

            result["ATQA"] = ByteUtils.byteArrayToHexString(atqa, isNeedHead = true)
            result["SAK"] = ByteUtils.byteToHexString(sak.toByte(), isNeedHead = true)
        }?.onFailure { it.printStackTrace() }
        nfcA?.runCatching { close() }

        val ndef = Ndef.get(tag)
        ndef?.runCatching {
            connect()

            result["可写"] = if (isWritable) "是" else "否"
            result["可为只读"] = if (canMakeReadOnly()) "是" else "否"

            ndefMessage?.records?.forEachIndexed { index, ndefRecord ->
                result["记录${index + 1}"] = String(ndefRecord.payload)
            }

        }?.onFailure {
            it.printStackTrace()
        }
        ndef?.runCatching { close() }


        return result
    }

}


package com.sena.nfctools.utils.ndef

import com.sena.nfctools.other.Nfc_Wifi_Test
import java.nio.ByteBuffer
import java.nio.charset.Charset


/**
 * FileName: PayloadParseUtils
 * Author: JiaoCan
 * Date: 2023/4/6 17:08
 */

object RecordUtils {

    fun getTnfStr(tnf: Short): String {
        return when(tnf.toInt()) {
            0x00 -> "NFC Empty (0x00)"
            0x01 -> "NFC Well Known (0x01)"
            0x02 -> "NFC Mime Media (0x02)"
            0x03 -> "NFC Absolute Uri (0x03)"
            0x04 -> "NFC External Type (0x04)"
            0x05 -> "NFC Unknown (0x05)"
            0x06 -> "NFC Unchanged (0x06)"
            0x07 -> "NFC Reserved (0x07"
            else -> ""
        }
    }

    fun convertToUtf8(payload: ByteArray): String {
        return String(payload, charset = Charset.forName("UTF-8"))
    }

    fun convertToAscii(payload: ByteArray): String {
        return String(payload, charset = Charset.forName("ASCII"))
    }

    fun generateData(type: String, payload: ByteArray): String {
        return when (type) {
            "T" -> parseText(payload)
            "application/vnd.wfa.wsc" -> parseWifiInfo(payload)
            else -> ""
        }
    }

    private fun parseText(payload: ByteArray): String {
        // 0x02 0x65 0x6E 0x61 0x61 0x61 0x61
        // 0x02 编码长度 = 2
        // 0x65 0x64 编码 = en
        // 0x61 0x61 0x61 0x61 文本 = aaaa
        val buffer = ByteBuffer.wrap(payload)
        val languageLen = buffer.get().toInt()
        val languageCode = String(
            ByteArray(languageLen).apply { buffer.get(this) }
        )
        val content = String(
            ByteArray(payload.size - 1 - languageLen).apply { buffer.get(this) }
        )
        return content
    }

    private fun parseWifiInfo(payload: ByteArray): String {
        return Nfc_Wifi_Test.specialTest(payload)
    }

}


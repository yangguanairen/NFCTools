package com.sena.nfctools.other

import android.content.Context
import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.Tag
import android.nfc.tech.MifareClassic
import android.nfc.tech.Ndef
import android.os.Build
import com.sena.nfctools.utils.ByteUtils


/**
 * FileName: Nfc_Other_Test
 * Author: JiaoCan
 * Date: 2023/4/6 10:14
 */

object Nfc_Other_Test {

    fun writeApplication(tag: Tag) {
        // D4 0F 0B // 应该是什么操作/识别码之类的
        // 61 6E 64 72 6F 69 64 2E 63 6F 6D 3A 70 6B android.com:
        // 67 62 69 // pkg
        // 6E 2E 6D 74 2E 70 6C 75 73 // bin.mt.plus
        val appRecord = NdefRecord.createApplicationRecord("bin.mt.plus")
        val ndefMessage = NdefMessage(appRecord)
        println(ByteUtils.byteArrayToHexString(ndefMessage.toByteArray(), separator = " "))
        println(String(ndefMessage.toByteArray()))
        writeByNdef(tag, ndefMessage)
    }

    fun parse(tag: Tag) {
        val ndef = Ndef.get(tag)
        try {
            ndef.connect()
            println(ByteUtils.byteArrayToHexString(ndef.ndefMessage.toByteArray()))

            val tagId = tag.id
            val techList = tag.techList
            val ndefType = ndef.type
            val maxSize = ndef.maxSize
            val canMakeReadOnly = ndef.canMakeReadOnly()
            val isWritable = ndef.isWritable
            val ndefMessage = ndef.ndefMessage
            val ndefRecords = ndef.ndefMessage.records

            val m1 = MifareClassic.get(tag)
            val sectorCount = m1.sectorCount
//            ndef.


            ndef.ndefMessage.records.forEach { record: NdefRecord ->
                val id = ByteUtils.byteArrayToHexString(record.id, separator = ":")
                val tnf = ByteUtils.byteToHexString(record.tnf.toByte(), true)
                val type = record.type
                val typeStr = String(type)
                val payload = record.payload
                val payloadStr = String(payload)

                println("RecordId: ${ByteUtils.byteArrayToHexString(record.id, separator = ":")}")
                println("RecordTnf: ${ByteUtils.byteToHexString(record.tnf.toByte(), true)}")
                println("RecordType: ${String(record.type)}")
                println("RecordPayload: ${
                    ByteUtils.byteArrayToHexString(
                        record.payload,
                        separator = " "
                    )
                }")
                println("Sting: ${String(record.payload)}")
//                val payload = ByteBuffer.wrap(record.payload)

//                while (payload.hasRemaining()) {
//                    val field = payload.short
//                    val fieldSize = payload.short
//                    println("field: ${ByteUtils.byteToHexString(field.toByte(), true)}")
//                    println("size: $fieldSize")
//                    Nfc_Wifi_Test.parseCredential(payload, fieldSize)
//                }

            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                ndef.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun writeUrlOrUri(tag: Tag) {
        // 前缀决定的识别码的不同
        val urlRecord = NdefRecord.createUri("http://www.baidu.com")
        val ndefMessage = NdefMessage(urlRecord)
        println(ByteUtils.byteArrayToHexString(ndefMessage.toByteArray(), separator = " "))
        println(String(ndefMessage.toByteArray()))
        writeByNdef(tag, ndefMessage)
    }

    fun writeText(tag: Tag, context: Context) {
        // D1 01 07 54 02 7A 68 61 61 61 61
        // �Tzhaaaa
        val local = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            context.resources.configuration.locales[0]
        } else {
            context.resources.configuration.locale
        }
        val textRecord = NdefRecord.createTextRecord("zhi", "aaaa")
        val ndefMessage = NdefMessage(textRecord)
        println(ByteUtils.byteArrayToHexString(ndefMessage.toByteArray(), separator = " "))
        println(String(ndefMessage.toByteArray()))
        writeByNdef(tag, ndefMessage)
    }

    private fun writeByNdef(tag: Tag, message: NdefMessage) {
        val ndef = Ndef.get(tag)
        try {
            ndef.connect()
            ndef.writeNdefMessage(message)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                ndef.close()
                println("覆写完成")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

}


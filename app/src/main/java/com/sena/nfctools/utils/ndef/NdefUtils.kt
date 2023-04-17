package com.sena.nfctools.utils

import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.Tag
import android.nfc.tech.Ndef
import android.nfc.tech.NdefFormatable
import com.google.gson.Gson
import com.sena.nfctools.bean.*
import com.sena.nfctools.newBean.NdefData
import com.sena.nfctools.newBean.RecordData


/**
 * FileName: NfcParse
 * Author: JiaoCan
 * Date: 2023/4/6 14:39
 */

object NdefUtils {

    fun readNdef(tag: Tag): NdefData? {
        val ndef = Ndef.get(tag) ?: return null
        try {
            ndef.connect()
            return NdefData(
                ndef.type, ndef.maxSize, ndef.isWritable,
                ndef.canMakeReadOnly(), readNdefRecord(ndef.ndefMessage)
            )
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        } finally {
            ndef.runCatching { close() }
        }
    }

    fun writeAfterFormat(tag: Tag, writeDataList: List<WriteData>): Boolean {
        val message = createMessage(writeDataList) ?: return false
        val ndefFormatable = NdefFormatable.get(tag) ?: return false
        val result =  ndefFormatable.runCatching {
            connect()
            format(message)
        }.onFailure { it.printStackTrace() }
        ndefFormatable.runCatching { close() }
        return result.isSuccess
    }

    private fun createMessage(writeDataList: List<WriteData>): NdefMessage? {
        val records = arrayListOf<NdefRecord>().apply {
            writeDataList.forEach { writeData ->
                writeData.build()?.let { this.add(it) }
            }
        }
        if (records.isEmpty()) return null
        val message = if (1 == records.size) NdefMessage(records[0]) else NdefMessage(records.toTypedArray())
        println("测试: ${ByteUtils.byteArrayToHexString(message.toByteArray())}")
        return message
    }

    fun write(tag: Tag, writeDataList: List<WriteData>): Boolean {
        val message = createMessage(writeDataList) ?: return false
        val ndef = Ndef.get(tag) ?: return false
        val result = ndef.runCatching {
            connect()
            writeNdefMessage(message)
        }.onFailure { it.printStackTrace() }
        ndef.runCatching { close() }
        return result.isSuccess
    }

    private fun readNdefRecord(ndefMessage: NdefMessage?): List<RecordData> {
        val result = arrayListOf<RecordData>()
        ndefMessage?.records?.forEach {
            val tnf = when (it.tnf) {
                NdefRecord.TNF_EMPTY -> "TNF_EMPTY"
                NdefRecord.TNF_WELL_KNOWN -> "TNF_WELL_KNOWN"
                NdefRecord.TNF_MIME_MEDIA -> "TNF_MIME_MEDIA"
                NdefRecord.TNF_ABSOLUTE_URI -> "TNF_ABSOLUTE_URI"
                NdefRecord.TNF_UNKNOWN -> "TNF_UNKNOWN"
                NdefRecord.TNF_RESERVED -> "TNF_RESERVED"
                NdefRecord.TNF_UNCHANGED -> "TNF_UNCHANGED"
                else -> String.format("unexpected tnf value: 0x%02x", it.tnf)
            }
            result.add(RecordData(
                tnf, ByteUtils.byteArrayToHexString(it.type),
                it.payload))
        }
        return result
    }
}


enum class CardType {
    M1,
    UNKNOWN
}




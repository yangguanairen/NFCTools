package com.sena.nfctools.utils

import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.Tag
import android.nfc.tech.Ndef
import android.os.Build
import com.sena.nfctools.NfcApplication
import com.sena.nfctools.bean.CardData
import com.sena.nfctools.bean.RecordData


/**
 * FileName: NfcParse
 * Author: JiaoCan
 * Date: 2023/4/6 14:39
 */

object NdefUtils {

    fun parse(tag: Tag): CardData? {
        val ndef = Ndef.get(tag)
        val cardData = CardData()
        val result = runCatching {
            ndef.connect()
            cardData.apply {
                tagId = tag.id
                techList = tag.techList.toList()
                ndefType = ndef.type
                maxSize = ndef.maxSize
                canMakeReadOnly = ndef.canMakeReadOnly()
                isWritable = ndef.isWritable
                ndefRecords = getNdefRecord(ndef.ndefMessage.records)
            }
        }.onFailure {
            it.printStackTrace()
        }
        runCatching {
            ndef.close()
        }
        cardData.mifareClassicData = M1CardUtils.readM1Card(tag)
        return if (result.isSuccess) {
            cardData
        } else {
            null
        }
    }

    fun write(tag: Tag, opt: String, data: String): Boolean {
        val ndef = Ndef.get(tag)
        val result = ndef.runCatching {
            ndef.connect()

            val ndefRecord = when (opt) {
                "Text" -> createTextRecord(data)
                "App" -> createApplicationRecord(data)
                else -> throw IllegalArgumentException("未知的opt: $opt" )
            }
            val ndefMessage = NdefMessage(ndefRecord)
            ndef.writeNdefMessage(ndefMessage)
        }.onFailure {
            it.printStackTrace()
        }
        ndef.runCatching { close() }

        return result.isSuccess
    }

    fun format(tag: Tag): Boolean {
        val ndef = Ndef.get(tag)
        val result = ndef.runCatching {
            ndef.connect()
            val type = "M1"
            when (type) {
                "M1" -> M1CardUtils.format(tag)
            }
        }
        ndef.runCatching { close() }

        return result.isSuccess
    }

    private fun getNdefRecord(records: Array<NdefRecord>): List<RecordData> {
        val result = arrayListOf<RecordData>()
        records.forEach { record ->
            val tnf = record.tnf
            val type = record.type
            val payload = record.payload
            result.add(RecordData(
                tnf = tnf,
                type = type,
                payload = payload
            ))
        }
        return result
    }

    private fun createApplicationRecord(appName: String): NdefRecord {
        return NdefRecord.createApplicationRecord(appName)
    }

    private fun createTextRecord(text: String): NdefRecord {
        val context = NfcApplication.getContext()
        val code = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            context?.resources?.configuration?.locales?.get(0)
        } else {
            context?.resources?.configuration?.locale
        } ?: "en"
        return NdefRecord.createTextRecord("code", text)
    }

}


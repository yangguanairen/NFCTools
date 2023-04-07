package com.sena.nfctools.utils

import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.Tag
import android.nfc.tech.Ndef
import android.os.Build
import com.google.gson.Gson
import com.sena.nfctools.NfcApplication
import com.sena.nfctools.bean.CardData
import com.sena.nfctools.bean.OptType
import com.sena.nfctools.bean.RecordData
import com.sena.nfctools.bean.WriteData


/**
 * FileName: NfcParse
 * Author: JiaoCan
 * Date: 2023/4/6 14:39
 */

object NdefUtils {

    fun parse(tag: Tag): CardData? {
        val cardData = CardData()
        val isSuccess = parseBasicInfo(tag, cardData)
        println("测试: ${Gson().toJson(cardData)}")

        when (getCardTypeByTechList(tag.techList)) {
             CardType.M1 -> {
                 cardData.mifareClassicData = M1CardUtils.readM1Card(tag)
             }
            else -> {}
        }

        return if (isSuccess) {
            cardData
        } else {
            null
        }
    }

    fun write(tag: Tag, writeDataList: List<WriteData>): Boolean {

        val records = arrayListOf<NdefRecord>().apply {
            writeDataList.forEach { writeData ->
                writeData.build()?.let { this.add(it) }
            }
        }
        if (records.isEmpty()) return false
        val message = if (1 == records.size) NdefMessage(records[0]) else NdefMessage(records.toTypedArray())

        val ndef = Ndef.get(tag)
        val result = ndef.runCatching {
            ndef.connect()
            ndef.writeNdefMessage(message)
        }.onFailure {
            it.printStackTrace()
        }
        ndef.runCatching { close() }

        return result.isSuccess
    }

    fun format(tag: Tag): Boolean {
        val result = runCatching {
            when (getCardTypeByTechList(tag.techList)) {
                CardType.M1 -> {
                    M1CardUtils.format(tag)
                }
                else -> {

                }
            }
        }.onFailure {
            it.printStackTrace()
        }

        return result.isSuccess
    }

    fun getCardTypeByTechList(techList: Array<String>): CardType {
        techList.forEach {
            if ("mifareclassic" in it.lowercase()) {
                return CardType.M1
            }
        }
        return CardType.UNKNOWN
    }

    private fun parseBasicInfo(tag: Tag, cardData: CardData): Boolean {
        val ndef = Ndef.get(tag)
        val result = runCatching {
            ndef.connect()
            cardData.apply {
                tagId = tag.id
                techList = tag.techList.toList()
                ndefType = ndef.type
                maxSize = ndef.maxSize
                canMakeReadOnly = ndef.canMakeReadOnly()
                isWritable = ndef.isWritable
                ndefRecords = parseNdefRecord(ndef.ndefMessage.records)
            }
        }.onFailure {
            it.printStackTrace()
        }
        runCatching {
            ndef.close()
        }
        return result.isSuccess
    }
    private fun parseNdefRecord(records: Array<NdefRecord>): List<RecordData> {
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


}


enum class CardType {
    M1,
    UNKNOWN
}




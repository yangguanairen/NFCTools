package com.sena.nfctools.utils

import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.Tag
import android.nfc.tech.Ndef
import android.nfc.tech.NdefFormatable
import com.google.gson.Gson
import com.sena.nfctools.bean.*


/**
 * FileName: NfcParse
 * Author: JiaoCan
 * Date: 2023/4/6 14:39
 */

object NdefUtils {

    fun parse(tag: Tag): NdefData? {
        val ndefData = NdefData()
        val ndef = Ndef.get(tag)
        val result = ndef?.runCatching {
            connect()

            ndefData.type = type
            ndefData.maxSize = maxSize
            ndefData.canMakeReadOnly = canMakeReadOnly()
            ndefData.isWriteable = isWritable
            ndefData.recordList = readNdefRecord(ndefMessage)
        }?.onFailure {
            it.printStackTrace()
        }
        ndef?.runCatching { close() }

        return if (result?.isFailure == true) {
            null
        } else {
            ndefData
        }
    }

//    private fun invoke(tag: Tag): Boolean {
//
//    }

    fun write(tag: Tag, writeDataList: List<WriteData>): Boolean {

        val isNdefFormatable = tag.techList.contains("android.nfc.tech.NdefFormatable")

        val records = arrayListOf<NdefRecord>().apply {
            writeDataList.forEach { writeData ->
                writeData.build()?.let { this.add(it) }
            }
        }
        if (records.isEmpty()) return false
        val message = if (1 == records.size) NdefMessage(records[0]) else NdefMessage(records.toTypedArray())
        println("测试: ${ByteUtils.byteArrayToHexString(message.toByteArray())}")

        if (!isNdefFormatable) {
            val ndef = Ndef.get(tag)
            val result = ndef.runCatching {
                ndef.connect()
                ndef.writeNdefMessage(message)
            }.onFailure {
                it.printStackTrace()
            }
            ndef.runCatching { close() }

            return result.isSuccess
        } else {
            val ndefFormatable = NdefFormatable.get(tag)
            val result = ndefFormatable.runCatching {
                ndefFormatable.connect()
                ndefFormatable.format(message)
            }.onFailure {
                it.printStackTrace()
            }
            ndefFormatable.runCatching { close() }

            return result.isSuccess
        }


    }

//    fun copy(tag: Tag, copySource: CardData): Boolean {
//        val type = getCardTypeByTechList(tag.techList)
//        when (type) {
//            CardType.M1 -> {
//                val m1Data = copySource.mifareClassicData
//                return if (m1Data == null) false
//                else M1ClassicUtils.copy(tag, m1Data)
//            }
//
//
//            else -> {
//                return false
//            }
//        }
//    }

//    fun getCardTypeByTechList(techList: Array<String>): CardType {
//        techList.forEach {
//            if ("mifareclassic" in it.lowercase()) {
//                return CardType.M1
//            }
//        }
//        return CardType.UNKNOWN
//    }

    private fun readNdefRecord(ndefMessage: NdefMessage?): List<NdefRecordData> {
        val result = arrayListOf<NdefRecordData>()
        ndefMessage?.records?.forEach {
            result.add(NdefRecordData(it.tnf, it.type, it.payload))
        }
        return result
    }
}


enum class CardType {
    M1,
    UNKNOWN
}




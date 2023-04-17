package com.sena.nfctools.utils

import android.nfc.Tag
import android.nfc.tech.NfcA
import com.sena.nfctools.bean.WriteData
import com.sena.nfctools.newBean.*
import com.sena.nfctools.utils.m0.Ntag21xUtils
import com.sena.nfctools.utils.m1.M1Utils

// "android.nfc.tech.NfcA","android.nfc.tech.MifareUltralight","android.nfc.tech.Ndef"
object NfcUtils {
    fun read(tag: Tag): BaseCard? {
        val techList = tag.techList

        if (techList.contains("android.nfc.tech.MifareClassic")) {
            val nfcAData = readNfcA(tag) ?: return null
            val tagData = TagData(ByteUtils.byteArrayToHexString(tag.id, separator = ":"), tag.techList.toList())

            val m1Card = M1Card(tagData, nfcAData)
            m1Card.mifareClassicData = M1Utils.readM1Card(tag)
            if (techList.contains("android.nfc.tech.Ndef")) m1Card.ndefData = NdefUtils.readNdef(tag)
            return m1Card
        } else if (techList.contains("android.nfc.tech.MifareUltralight")) {
            val nfcAData = readNfcA(tag) ?: return null
            val tagData = TagData(ByteUtils.byteArrayToHexString(tag.id, separator = ":"), tag.techList.toList())

            val ntag215Card = Ntag215Card(tagData, nfcAData)
            ntag215Card.ntag21xData = Ntag21xUtils.read(tag)
            if (techList.contains("android.nfc.tech.Ndef")) ntag215Card.ndefData = NdefUtils.readNdef(tag)
            return ntag215Card
        }

        return null

    }

    fun write(tag: Tag, writeDataList: List<WriteData>): Boolean {
        val techList = tag.techList
        if (techList.contains("android.nfc.tech.Ndef")) {
            return NdefUtils.write(tag, writeDataList)
        } else if (techList.contains("android.nfc.tech.NdefFormatable")) {
            return NdefUtils.writeAfterFormat(tag, writeDataList)
        } else {
            println("不支持的数据格式")
            return false
        }
    }

    fun format(tag: Tag): Boolean {
        val techList = tag.techList
        if (techList.contains("android.nfc.tech.MifareClassic")) {
            return M1Utils.newFormat(tag)
        } else if (techList.contains("android.nfc.tech.MifareUltralight")) {
            return Ntag21xUtils.format(tag)
        } else {
            println("不支持的数据格式")
            return false
        }
    }

    private fun readNfcA(tag: Tag): NfcAData? {
        val nfcA = NfcA.get(tag) ?: return null
        try {
            nfcA.connect()
            return NfcAData("0x" + ByteUtils.byteArrayToHexString(nfcA.atqa),
                ByteUtils.byteToHexString(nfcA.sak.toByte(), true))
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        } finally {
            nfcA.runCatching { close() }
        }
    }


}
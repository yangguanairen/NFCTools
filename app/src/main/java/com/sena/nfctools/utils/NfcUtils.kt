package com.sena.nfctools.utils

import android.nfc.Tag
import com.sena.nfctools.bean.WriteData
import com.sena.nfctools.newBean.*
import com.sena.nfctools.utils.nfca.M0Tools
import com.sena.nfctools.utils.nfca.M1Tools
import com.sena.nfctools.utils.nfcv.NfcVTools

// "android.nfc.tech.NfcA","android.nfc.tech.MifareUltralight","android.nfc.tech.Ndef"
object NfcUtils {
    fun read(tag: Tag): BaseCard? {
        val techList = tag.techList

        var baseCard: BaseCard? = null
        when {
            techList.contains("android.nfc.tech.MifareClassic") -> {
                val m1Data = M1Tools.readM1Card(tag) ?: return null
                val tagData = TagData(ByteUtils.byteArrayToHexString(tag.id, separator = ":"), tag.techList.toList())

                baseCard = M1Card(tagData, m1Data)
            }
            techList.contains("android.nfc.tech.MifareUltralight") -> {
                val m0Data = M0Tools.read(tag) ?: return null
                val tagData = TagData(ByteUtils.byteArrayToHexString(tag.id, separator = ":"), tag.techList.toList())

                baseCard = M0Card(tagData, m0Data)
            }
            techList.contains("android.nfc.tech.NfcA") -> {

            }
            techList.contains("android.nfc.tech.NfcV") -> {
                val nfcVData = NfcVTools.read(tag) ?: return null
                val tagData = TagData(ByteUtils.byteArrayToHexString(tag.id, separator = ":"), tag.techList.toList())

                baseCard = NfcVCard(tagData, nfcVData)
            }
        }

        if (techList.contains("android.nfc.tech.Ndef")) {
            baseCard?.setNdefData(NdefUtils.readNdef(tag))
        }

        return baseCard

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
            return M1Tools.newFormat(tag)
        } else if (techList.contains("android.nfc.tech.MifareUltralight")) {
            return M0Tools.format(tag)
        } else {
            println("不支持的数据格式")
            return false
        }
    }



}
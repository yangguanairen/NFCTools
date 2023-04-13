package com.sena.nfctools.utils

import android.nfc.Tag
import android.nfc.tech.NfcA
import com.sena.nfctools.bean.NfcAData
import com.sena.nfctools.bean.TagData
import com.sena.nfctools.bean.WriteData

// "android.nfc.tech.NfcA","android.nfc.tech.MifareUltralight","android.nfc.tech.Ndef"
object NfcUtils {
    fun read(tag: Tag): TagData {
        val tagData = TagData(tag.id, tag.techList)

        if (tag.techList.contains("android.nfc.tech.NfcA")) {
            tagData.nfcAData = readNfcA(tag)
        }

        return tagData
    }

    fun write(tag: Tag, writeDataList: List<WriteData>): Boolean {
        val techList = tag.techList
        if (techList.contains("android.nfc.tech.Ndef")) {
            return NdefUtils.write(tag, writeDataList)
        } else {
            println("不支持的数据格式")
            return false
        }
    }

    fun format(tag: Tag): Boolean {
        val techList = tag.techList
        if (techList.contains("android.nfc.tech.MifareClassic")) {
            return M1ClassicUtils.newFormat(tag)
        } else {
            println("不支持的数据格式")
            return false
        }
    }

    private fun readNfcA(tag: Tag): NfcAData? {
        val nfcAData = NfcAData()
        val nfcA = NfcA.get(tag)
        val result = nfcA?.runCatching {
            connect()
            nfcAData.atqa = nfcA.atqa
            nfcAData.sak = nfcA.sak
        }?.onFailure {
            it.printStackTrace()
        }
        nfcA?.runCatching { close() }
        if (result?.isFailure == true) {
            return null
        }
        nfcAData.m1ClassicData = M1ClassicUtils.readM1Card(tag)
//        val m1UltraData =
        return nfcAData
    }


}
package com.sena.nfctools.utils

import android.content.Intent
import android.nfc.NdefMessage
import android.nfc.NfcAdapter
import android.nfc.Tag
import com.google.gson.Gson
import java.nio.charset.Charset

object NfcUtils {


    fun readNFCId(intent: Intent): String? {
        val tag = intent.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG)
        if (tag == null) {
            println("tag is null")
            return null
        }
        val id = tag.id
        return if (id == null) null
        else byteArrayToHexString(id)
    }

    fun readNFCTechList(intent: Intent): String? {
        val tag = intent.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG) ?: return null
        val techList = tag.techList ?: return null

        var result = ""
        techList.forEach {
            result += "*${Gson().toJson(it)}*"
        }
        return result
    }


    fun readNFCRawArray(intent: Intent): String? {
        val rawArray = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES) ?: return null
        val ndefMsg = rawArray[0] as NdefMessage
        val ndefRecord = ndefMsg.records[0] ?: return null
        val result = String(ndefRecord.payload, Charset.forName("UTF-8"))
        return result
    }


    fun byteArrayToHexString(byteArray: ByteArray): String {
        var byte: Int
        var high: Int
        var low: Int
        var result = ""
        val hex = arrayOf("0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F")

        byteArray.forEach {
            byte = it.toInt() and 0xFF
            high = (byte shr 4) and 0x0F
            low = byte and 0x0F
            result += "${hex[high]}${hex[low]}"
        }
        return result
    }

}
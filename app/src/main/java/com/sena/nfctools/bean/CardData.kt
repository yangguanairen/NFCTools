package com.sena.nfctools.bean

import android.nfc.NdefRecord
import com.sena.nfctools.utils.ByteUtils
import java.nio.charset.Charset


/**
 * FileName: CardData
 * Author: JiaoCan
 * Date: 2023/4/6 16:14
 */

data class CardData(
    var tagId: ByteArray = byteArrayOf(),
    var techList: List<String> = arrayListOf(),
    var ndefType: String = "",
    var maxSize: Int = 0,
    var canMakeReadOnly: Boolean = false,
    var isWritable: Boolean = false,
    var ndefRecords: List<RecordData> = arrayListOf(),
    var mifareClassicData: MifareClassicData? = null
) {
    override fun equals(other: Any?): Boolean {
        if (other !is CardData) return false
        return tagId.contentEquals(other.tagId)
    }

    override fun hashCode(): Int {
        return tagId.contentHashCode()
    }
}

data class RecordData(
    val tnf: Short,
    val type: ByteArray,
    val payload: ByteArray,
) {

    override fun equals(other: Any?): Boolean {
        if (other !is RecordData) return false
        return type.contentEquals(other.type) &&
                payload.contentEquals(other.payload)
    }

    override fun hashCode(): Int {
        var result = type.contentHashCode()
        result = 31 * result + payload.contentHashCode()
        return result
    }

}


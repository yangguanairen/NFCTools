package com.sena.nfctools.newBean

import com.sena.nfctools.utils.ByteUtils


/**
 * FileName: Card
 * Author: JiaoCan
 * Date: 2023/4/14 15:47
 */

const val CARD_TYPE_NFCA = "NfcACard"
const val CARD_TYPE_M1 = "M1Card"
const val CARD_TYPE_M0 = "M0Card"
const val CARD_TYPE_NFCV = "NfcVCard"

 abstract class BaseCard(private val id: String, private val cardType: String) {

     protected var ndefData: NdefData? = null
     private set

     fun setNdefData(d: NdefData?) {
         ndefData = d
     }
    fun getId(): String = id

    fun getCardType(): String = cardType

    abstract fun buildCardDetail(): List<Pair<String, String>>

    abstract fun buildMemoryDetail(): List<Pair<String, String>>
}

class NfcACard(private val tagData: TagData, private val nfcAData: NfcAData) : BaseCard(tagData.id, CARD_TYPE_NFCA) {

    override fun buildCardDetail(): List<Pair<String, String>> {
        return emptyList()
    }

    override fun buildMemoryDetail(): List<Pair<String, String>> {
        return emptyList()
    }
}



class M1Card(private val tagData: TagData, private val m1Data: M1Data) : BaseCard(tagData.id, CARD_TYPE_M1) {

    override fun buildCardDetail(): List<Pair<String, String>> {
        return emptyList()
    }

    override fun buildMemoryDetail(): List<Pair<String, String>> {
        return emptyList()
    }

}

class M0Card(private val tagData: TagData, private val m0Data: M0Data) : BaseCard(tagData.id, CARD_TYPE_M0) {
    override fun buildCardDetail(): List<Pair<String, String>> {
        return emptyList()
    }

    override fun buildMemoryDetail(): List<Pair<String, String>> {
        return emptyList()
    }
}

//class M0Card(tagData: TagData,  m0Data: M0Data) : BaseCard(name) {
//
//    var mTagData: TagData = tagData
//    var mNfcAData: NfcAData = nfcAData
//
//    var ntag21xData: Ntag21xData? = null
//
//    var ndefData: NdefData? = null
//
//    override fun buildMap(): Map<String, String> {
//        val result = mutableMapOf<String, String>()
//
//        return result
//    }
//
//    override fun getId(): String {
//        return mTagData.id
//    }
//}

class NfcVCard(private val tagData: TagData, private val nfcVData: NfcVData) : BaseCard(tagData.id, CARD_TYPE_NFCV) {



    override fun buildCardDetail(): List<Pair<String, String>> {
        val result = mutableMapOf<String, String>()

        result["RF技术"] = "ISO 15693"
        result["序列号"] = tagData.id
        result["技术"] = tagData.techList.joinToString(", ")
        result["生产商"] = "NXP-I CODE SLIX"

        result["DSFID"] = ByteUtils.byteToHexString(nfcVData.dsfid, true)
        result["ResponseFlags"] = ByteUtils.byteToHexString(nfcVData.responseFlags, true)

        result["内存信息"] = "${nfcVData.blockCount * nfcVData.blockSize} bytes, ${nfcVData.blockCount}块区(${nfcVData.blockSize} bytes 每块区)"

        ndefData?.let {
            result["数据格式"] = it.formType
            result["大小"] = it.maxSize.toString()
            result["是否可写"] = if (it.isWritable) "是" else "否"
            result["可为只读"] = if (it.canMakeReadOnly) "是" else "否"
            result["记录1"] = "暂且不写"
        }
        return result.toList()
    }

    override fun buildMemoryDetail(): List<Pair<String, String>> {
        val map = mutableMapOf<String, String>()
        nfcVData.blocks.forEach {
            val addr = ByteUtils.byteToHexString(it.index.toByte(), true)
            map["Addr $addr, Block ${it.index}"] = ByteUtils.byteArrayToHexString(it.data, separator = " ")
        }
        return map.toList()
    }

}


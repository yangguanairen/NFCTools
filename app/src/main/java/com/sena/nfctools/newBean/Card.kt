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
        val map = mutableMapOf<String, String>()
        map["RF技术"] = "14443-3A"
        map["序列号"] = tagData.id
        map["技术"] = tagData.techList.joinToString(", ")
        map["生产商"] = m1Data.manufacturer

        map["SAK"] = m1Data.sak
        map["ATQA"] = m1Data.atqa
        map["内存信息"] = "可读写大小: ${m1Data.size} bytes \n" +
                "${m1Data.sectorCount} 扇区, ${m1Data.blockCount} 块区(16 bytes 每块区)"

        ndefData?.let {
            map["数据格式"] = it.formType
            map["大小"] = it.maxSize.toString()
            map["是否可写"] = if (it.isWritable) "是" else "否"
            map["可为只读"] = if (it.canMakeReadOnly) "是" else "否"
            map["记录1"] = "暂且不写"
        }
        return map.toList()
    }

    override fun buildMemoryDetail(): List<Pair<String, String>> {
        val map = mutableMapOf<String, String>()
        m1Data.sectors.forEach { s ->
            println("测试blocksize: ${s.blocks.size}")
            s.blocks.forEach { b ->
                val title = "Sector ${s.index}, Block ${b.index}"
                map[title] = ByteUtils.byteArrayToHexString(b.data, separator = " ")
            }
        }
        println("测试size: ${map.size}")
        return map.toList()
    }

}

class M0Card(private val tagData: TagData, private val m0Data: M0Data) : BaseCard(tagData.id, CARD_TYPE_M0) {
    override fun buildCardDetail(): List<Pair<String, String>> {
        val map = mutableMapOf<String, String>()
        map["RF技术"] = "14443-3A"
        map["序列号"] = tagData.id
        map["技术"] = tagData.techList.joinToString(", ")
        map["生产商"] = m0Data.manufacturer

        map["SAK"] = m0Data.sak
        map["ATQA"] = m0Data.atqa
        map["内存信息"] = "总大小: ${m0Data.maxSize} bytes, 可用大小: ${m0Data.dataSize} bytes\n" +
                "总页数: ${m0Data.maxPage}, 可用页数: ${m0Data.dataPage}, $4 bytes 每页"

        ndefData?.let {
            map["数据格式"] = it.formType
            map["大小"] = it.maxSize.toString()
            map["是否可写"] = if (it.isWritable) "是" else "否"
            map["可为只读"] = if (it.canMakeReadOnly) "是" else "否"
            map["记录1"] = "暂且不写"
        }

        return map.toList()
    }

    override fun buildMemoryDetail(): List<Pair<String, String>> {
        val map = mutableMapOf<String, String>()
        m0Data.pages.forEach {
            val addr = ByteUtils.byteToHexString(it.index.toByte(), true)
            map["Addr $addr, Page ${it.index}"] = ByteUtils.byteArrayToHexString(it.data, separator = " ")
        }
        return map.toList()
    }
}


class NfcVCard(private val tagData: TagData, private val nfcVData: NfcVData) : BaseCard(tagData.id, CARD_TYPE_NFCV) {

    override fun buildCardDetail(): List<Pair<String, String>> {
        val result = mutableMapOf<String, String>()

        result["RF技术"] = "ISO 15693"
        result["序列号"] = tagData.id
        result["技术"] = tagData.techList.joinToString(", ")
        result["生产商"] = nfcVData.manufacturer

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


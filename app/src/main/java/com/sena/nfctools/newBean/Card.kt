package com.sena.nfctools.newBean

import com.sena.nfctools.utils.ByteUtils


/**
 * FileName: Card
 * Author: JiaoCan
 * Date: 2023/4/14 15:47
 */

abstract class BaseCard(val name: String) {

    abstract fun buildMap(): Map<String, String>

    abstract fun getId(): String
}

class M1Card(tagData: TagData, nfcAData: NfcAData) : BaseCard("M1") {

    var mTagData: TagData = tagData
    var mNfcAData: NfcAData = nfcAData

    var mifareClassicData: MifareClassicData? = null

    var ndefData: NdefData? = null

    override fun buildMap(): Map<String, String> {
        val result = mutableMapOf<String, String>()
        return result
    }

    override fun getId(): String {
        return mTagData.id
    }


}

class Ntag21xCard(name: String, tagData: TagData, nfcAData: NfcAData) : BaseCard(name) {

    var mTagData: TagData = tagData
    var mNfcAData: NfcAData = nfcAData

    var ntag21xData: Ntag21xData? = null

    var ndefData: NdefData? = null

    override fun buildMap(): Map<String, String> {
        val result = mutableMapOf<String, String>()

        return result
    }

    override fun getId(): String {
        return mTagData.id
    }
}

class NfcVCard(name: String, tagData: TagData, nfcVData: NfcVData) : BaseCard(name) {

    var mTagData: TagData = tagData
    var mNfcAData: NfcVData = nfcVData

    var icodeSlixData: T15693Data? = null

    var ndefData: NdefData? = null

    override fun buildMap(): Map<String, String> {
        val result = mutableMapOf<String, String>()

        result["RF技术"] = "ISO 15693"
        result["序列号"] = mTagData.id
        result["技术"] = mTagData.techList.joinToString(", ")
        result["生产商"] = "NXP-I CODE SLIX"

        result["DSFID"] = ByteUtils.byteToHexString(mNfcAData.dsfid, true)
        result["ResponseFlags"] = ByteUtils.byteToHexString(mNfcAData.responseFlags, true)

        icodeSlixData?.let {
            result["内存信息"] = "${it.blockCount * it.blockSize} bytes, ${it.blockCount}块区(${it.blockSize} bytes 每块区)"
        }

        ndefData?.let {
            result["数据格式"] = it.formType
            result["大小"] = it.maxSize.toString()
            result["是否可写"] = if (it.isWritable) "是" else "否"
            result["可为只读"] = if (it.canMakeReadOnly) "是" else "否"
            result["记录1"] = "暂且不写"
        }

        return result
    }

    override fun getId(): String {
        return mTagData.id
    }

}


package com.sena.nfctools.newBean



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

        return result
    }

    override fun getId(): String {
        return mTagData.id
    }

}


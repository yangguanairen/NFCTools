package com.sena.nfctools.newBean


/**
 * FileName: BaseData
 * Author: JiaoCan
 * Date: 2023/4/14 15:28
 */



data class TagData(
    val id: String,
    val techList: List<String>
)

data class NfcAData(
    val atqa: String,
    val sak: String
)

data class NfcVData(
    val responseFlags: Byte,
    val dsfid: Byte
)

data class NdefData(
    val formType: String,
    val maxSize: Int,
    val isWritable: Boolean,
    val canMakeReadOnly: Boolean,
    val records: List<RecordData>
)

data class RecordData(
    val tnf: String,  // Record类型, 比如MIME, URI...
    val type: String,
    val payload: ByteArray
)

data class MifareClassicData(
    val type: String,
    val size: Int,
    val sectorCount: Int,
    val blockCount: Int,
    val sectors: List<MifareClassicSector>
)

data class MifareClassicSector(
    val index: Int,
    val bStartIndex: Int,
    val bCount: Int,
    val keyA: String,
    val keyB: String,
    val blocks: List<MifareClassicBlock>
)

data class MifareClassicBlock(
    val index: Int,
    val data: ByteArray
)

data class Ntag21xData(
    val cardName: String,
    val type: String,
    val dataSize: Int, // 可以读写的大小
    val maxSize: Int,  // 卡片总大小
    val dataPage: Int,
    val maxPage: Int,
    val pages: List<Ntag21xPage>
)

data class Ntag21xPage(
    val index: Int,
    val data: ByteArray
)

data class T15693Data(
    val blockCount: Int,
    val blockSize: Int,
    val blocks: List<T15693Block>
)

data class T15693Block(
    val index: Int,
    val data: ByteArray
)

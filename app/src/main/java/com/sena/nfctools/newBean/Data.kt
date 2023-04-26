package com.sena.nfctools.newBean


/**
 * FileName: BaseData
 * Author: JiaoCan
 * Date: 2023/4/14 15:28
 */

private const val UNKNOWN_TEXT = "unknown_text"
private const val UNKNOWN_INT = -1

data class TagData(
    val id: String,
    val techList: List<String>
)

data class NfcAData(
    var manufacturer: String = UNKNOWN_TEXT,
    var atqa: String = UNKNOWN_TEXT,
    var sak: String = UNKNOWN_TEXT
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

data class Block(
    val index: Int,
    val data: ByteArray
)

data class M1Data(
    var type: String = UNKNOWN_TEXT,
    var atqa: String = UNKNOWN_TEXT,
    var sak: String = UNKNOWN_TEXT,
    var manufacturer: String = UNKNOWN_TEXT,
    var size: Int = UNKNOWN_INT, // 实际可使用的大小
    var sectorCount: Int = UNKNOWN_INT,
    var blockCount: Int = UNKNOWN_INT,
    var sectors: List<M1Sector> = emptyList()
)

data class M1Sector(
    val index: Int,
    val bStartIndex: Int,
    val bCount: Int,
    val keyA: String,
    val keyB: String,
    val blocks: List<Block>
)


data class M0Data(
    var manufacturer: String = UNKNOWN_TEXT,
    var type: String = UNKNOWN_TEXT,
    var atqa: String = UNKNOWN_TEXT,
    var sak: String = UNKNOWN_TEXT,
    var dataSize: Int = UNKNOWN_INT, // 可以读写的大小
    var maxSize: Int = UNKNOWN_INT,  // 卡片总大小
    var dataPage: Int = UNKNOWN_INT,
    var maxPage: Int = UNKNOWN_INT,
    var pages: List<Block> = emptyList()
)

data class NfcVData(
    var manufacturer: String = UNKNOWN_TEXT,
    var blockCount: Int = UNKNOWN_INT,
    var blockSize: Int = UNKNOWN_INT,
    var responseFlags: Byte = -1,
    var dsfid: Byte = -1,
    var blocks: List<Block> = emptyList()
)



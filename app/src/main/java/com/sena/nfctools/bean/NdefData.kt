package com.sena.nfctools.bean


/**
 * FileName: NdefData
 * Author: JiaoCan
 * Date: 2023/4/13 13:54
 */

data class NdefData(
    var type: String = "",
    var maxSize: Int = -1,
    var canMakeReadOnly: Boolean = false,
    var isWriteable: Boolean = false,
    var recordList: List<NdefRecordData> = emptyList()
)

data class NdefRecordData(
    var tnf: Short = -1,
    var type: ByteArray = ByteArray(0),
    var payload: ByteArray = ByteArray(0)
)


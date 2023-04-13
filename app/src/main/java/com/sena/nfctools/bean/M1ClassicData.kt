package com.sena.nfctools.bean

import android.nfc.tech.MifareClassic


/**
 * FileName: M1ClassicData
 * Author: JiaoCan
 * Date: 2023/4/13 13:59
 */

data class M1ClassicData(
    val type: Int,
    val size: Int,
    var sectorList: List<M1ClassicSector> = emptyList()
) {
    val blockCount: Int = size / MifareClassic.BLOCK_SIZE
    val sector: Int = when (size) {
        MifareClassic.SIZE_1K -> 16
        MifareClassic.SIZE_2K -> 32
        MifareClassic.SIZE_4K -> 40
        MifareClassic.SIZE_MINI -> 5
        else -> 0
    }
    val typeStr: String = when (type) { // 类型
        MifareClassic.TYPE_CLASSIC -> "TYPE_CLASSIC"
        MifareClassic.TYPE_PLUS -> "TYPE_PLUS"
        MifareClassic.TYPE_PRO -> "TYPE_PRO"
        // 这个不可能执行，MifareClassic的构造中，
        // 如果没找到上述三个类型，自动抛出运行时异常
        else -> "TYPE_UNKNOWN"
    }
}

data class M1ClassicSector(
    val sectorIndex: Int,
    var keyA: ByteArray? = null,
    var keyB: ByteArray? = null,
    var blockList: List<M1ClassicBlock> = emptyList()
) {
    val bCount = if (sectorIndex < 32) 4 else 16
    val bIndex = if (sectorIndex < 32) sectorIndex * 4 else 32 * 4 + (sectorIndex - 32) * 16
}

data class M1ClassicBlock(
    val blockIndex: Int,
    val data: ByteArray? = null
)


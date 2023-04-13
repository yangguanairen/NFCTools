package com.sena.nfctools.bean


/**
 * FileName: MifareClassicData
 * Author: JiaoCan
 * Date: 2023/4/6 14:40
 */

data class MifareClassicData(
    var sectorCount: Int = -1,
    var blockCount: Int = -1,
    var size: Int = -1,
    var type: Int = -1,
    var typeStr: String = "",
    var atqa: ByteArray = ByteArray(0),
    var sak: Short = 0,
    var sectorList: List<MifareClassicSector> = emptyList()
) {

    override fun equals(other: Any?): Boolean {
        if (other !is MifareClassicData) return false
        return this.sectorCount == other.sectorCount &&
                this.blockCount == other.blockCount &&
                this.size == other.size &&
                this.atqa.contentEquals(other.atqa) &&
                this.sak == other.sak &&
                this.sectorList == other.sectorList
    }

    override fun hashCode(): Int {
        var result = sectorCount
        result = 31 * result + blockCount
        result = 31 * result + size
        result = 31 * result + atqa.contentHashCode()
        result = 31 * result + sak
        result = 31 * result + sectorList.hashCode()
        return result
    }
}

data class MifareClassicSector(
    val sectorIndex: Int,
    val blockStartIndex: Int,
    val blockCount: Int,
    val keyA: ByteArray?,
    val keyB: ByteArray?,
    val blockList: List<MifareClassicBlock>
) {
    override fun equals(other: Any?): Boolean {
        if (other !is MifareClassicSector) return false
        return this.sectorIndex == other.sectorIndex &&
                this.blockStartIndex == other.blockStartIndex &&
                this.blockCount == other.blockCount &&
                this.keyA.contentEquals(other.keyA) &&
                this.keyB.contentEquals(other.keyB) &&
                this.blockList == other.blockList
    }

    override fun hashCode(): Int {
        var result = sectorIndex
        result = 31 * result + blockStartIndex
        result = 31 * result + blockCount
        result = 31 * result + keyA.contentHashCode()
        result = 31 * result + keyB.contentHashCode()
        result = 31 * result + blockList.hashCode()
        return result
    }
}

data class MifareClassicBlock(
    val blockIndex: Int,
    val data: ByteArray
) {
    override fun equals(other: Any?): Boolean {
        if (other !is MifareClassicBlock) return false
        return this.blockIndex == other.blockIndex &&
                this.data.contentEquals(other.data)
    }

    override fun hashCode(): Int {
        var result = blockIndex
        result = 31 * result + data.contentHashCode()
        return result
    }
}


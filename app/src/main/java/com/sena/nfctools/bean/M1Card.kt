package com.sena.nfctools.bean

import android.nfc.Tag
import com.sena.nfctools.utils.ByteUtils
import java.io.Serializable


/**
 * FileName: M1Card
 * Author: JiaoCan
 * Date: 2023/3/28 13:38
 */

data class M1Card(
    val id: ByteArray,
    val techList: List<String>,
    val sectorCount: Int,
    val blockCount: Int,
    val size: Int, // byte(字节)
    val atqa: ByteArray,
    val sak: Short,
    val sectorList: List<M1Sector>
) : BaseCard(id, "M1") {


    fun changeSector(sIndex: Int, bIndex: Int, data: String) {
        val byteArray = ByteUtils.hexStringToByteArray(data)
        changeBlock(sIndex, bIndex, byteArray)

    }

    fun changeBlock(sIndex: Int, bIndex: Int, data: ByteArray) {
        if (sIndex < 0 || sIndex >= sectorCount) {
            println("M1Card 非法的sIndex: $sIndex, sectorCount: $sectorCount")
            return
        }
        val sector = sectorList[sIndex]
        val bSIndex = sector.blockStartIndex
        val bEIndex = sector.blockEndIndex
        if (bIndex < bSIndex || bIndex > bEIndex) {
            println("M1Card 非法的bIndex: $bIndex, startIndex: $bSIndex, endIndex: $bEIndex")
            return
        }
        val block = sector.blockList[bIndex]
        block.data = data
    }



}

data class M1Sector(
    val sectorIndex: Int,
    val blockStartIndex: Int,
    val blockEndIndex: Int,
    val key: ByteArray,
    val blockList: List<M1Block>
)

data class M1Block(
    val blockIndex: Int,
    var data: ByteArray
)




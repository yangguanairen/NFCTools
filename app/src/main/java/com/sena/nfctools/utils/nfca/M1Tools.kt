package com.sena.nfctools.utils.nfca

import android.nfc.Tag
import android.nfc.tech.MifareClassic
import android.nfc.tech.NfcA
import android.nfc.tech.TagTechnology
import com.sena.nfctools.newBean.*
import com.sena.nfctools.utils.ByteUtils


/**
 * FileName: M1CardUtils
 * Author: JiaoCan
 * Date: 2023/3/27 18:29
 */

object M1Tools {

    val keyList = arrayOf(
        byteArrayOf(0xFF.toByte(), 0xFF.toByte(), 0xFF.toByte(), 0xFF.toByte(), 0xFF.toByte(), 0xFF.toByte()),
        byteArrayOf(0xA0.toByte(), 0xA1.toByte(), 0xA2.toByte(), 0xA3.toByte(), 0xA4.toByte(), 0xA5.toByte()),
        byteArrayOf(0xD3.toByte(), 0xF7.toByte(), 0xD3.toByte(), 0xF7.toByte(), 0xD3.toByte(), 0xF7.toByte()),
        byteArrayOf(0x00.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte()),
        byteArrayOf(0xA0.toByte(), 0xB0.toByte(), 0xC0.toByte(), 0xD0.toByte(), 0xE0.toByte(), 0xF0.toByte()),
        byteArrayOf(0xA1.toByte(), 0xB1.toByte(), 0xC1.toByte(), 0xD1.toByte(), 0xE1.toByte(), 0xF1.toByte()),
        byteArrayOf(0xB0.toByte(), 0xB1.toByte(), 0xB2.toByte(), 0xB3.toByte(), 0xB4.toByte(), 0xB5.toByte()),
        byteArrayOf(0x4D.toByte(), 0x3A.toByte(), 0x99.toByte(), 0xC3.toByte(), 0x51.toByte(), 0xDD.toByte()),
        byteArrayOf(0x1A.toByte(), 0x98.toByte(), 0x2C.toByte(), 0x7E.toByte(), 0x45.toByte(), 0x9A.toByte()),
        byteArrayOf(0xAA.toByte(), 0xBB.toByte(), 0xCC.toByte(), 0xDD.toByte(), 0xEE.toByte(), 0xFF.toByte()),
        byteArrayOf(0xB5.toByte(), 0xFF.toByte(), 0x67.toByte(), 0xCB.toByte(), 0xA9.toByte(), 0x51.toByte()),
        byteArrayOf(0x71.toByte(), 0x4C.toByte(), 0x5C.toByte(), 0x88.toByte(), 0x6E.toByte(), 0x97.toByte()),
        byteArrayOf(0x58.toByte(), 0x7E.toByte(), 0xE5.toByte(), 0xF9.toByte(), 0x35.toByte(), 0x0F.toByte()),
        byteArrayOf(0xA0.toByte(), 0x47.toByte(), 0x8C.toByte(), 0xC3.toByte(), 0x90.toByte(), 0x91.toByte()),
        byteArrayOf(0x53.toByte(), 0x3C.toByte(), 0xB6.toByte(), 0xC7.toByte(), 0x23.toByte(), 0xF6.toByte()),
        byteArrayOf(0x24.toByte(), 0x02.toByte(), 0x00.toByte(), 0x00.toByte(), 0xDB.toByte(), 0xFD.toByte()),
        byteArrayOf(0x00.toByte(), 0x00.toByte(), 0x12.toByte(), 0xED.toByte(), 0x12.toByte(), 0xED.toByte()),
        byteArrayOf(0x8F.toByte(), 0xD0.toByte(), 0xA4.toByte(), 0xF2.toByte(), 0x56.toByte(), 0xE9.toByte()),
        byteArrayOf(0xEE.toByte(), 0x9B.toByte(), 0xD3.toByte(), 0x61.toByte(), 0xB0.toByte(), 0x1B.toByte()),
    )

    private val defaultControl = byteArrayOf(
        0xFF.toByte(), 0x07, 0x80.toByte(), 0x69
    )
    private val defaultKeyA = byteArrayOf(
        0xFF.toByte(), 0xFF.toByte(), 0xFF.toByte(),
        0xFF.toByte(), 0xFF.toByte(), 0xFF.toByte()
    )
    private val defaultKeyB = byteArrayOf(
        0xFF.toByte(), 0xFF.toByte(), 0xFF.toByte(),
        0xFF.toByte(), 0xFF.toByte(), 0xFF.toByte()
    )

    private val emptyData = byteArrayOf(
        0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
        0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00
    )
    private val controlData = defaultKeyA + defaultControl + defaultKeyB


    fun readM1Card(tag: Tag): M1Data? {
        var m1Data: M1Data = M1Data()
        val isSuccess = invoke(tag) { mifareClassic ->

            val nfcA = NfcA.get(tag)
            val sak = nfcA.sak
            val atqa = nfcA.atqa

            m1Data.manufacturer = "NXP MifareClassic 1K"
            m1Data.sak = ByteUtils.byteToHexString(sak.toByte(), true)
            m1Data.atqa = "0x" + ByteUtils.byteArrayToHexString(atqa)
            m1Data.type = when (mifareClassic.type) { // 类型
                MifareClassic.TYPE_CLASSIC -> "TYPE_CLASSIC"
                MifareClassic.TYPE_PLUS -> "TYPE_PLUS"
                MifareClassic.TYPE_PRO -> "TYPE_PRO"
                // 这个不可能执行，MifareClassic的构造中，
                // 如果没找到上述三个类型，自动抛出运行时异常
                else -> "TYPE_UNKNOWN"
            }
            m1Data.size = mifareClassic.size
            m1Data.sectorCount = mifareClassic.sectorCount
            m1Data.blockCount = mifareClassic.blockCount
            m1Data.sectors = readBlock(mifareClassic)
        }
        return if (isSuccess) {
            m1Data
        } else {
            null
        }
    }

    private fun invoke(tag: Tag, function: (mifareClassic: MifareClassic) -> Unit): Boolean {
        val mifareClassic: MifareClassic? = MifareClassic.get(tag)
        val result = mifareClassic?.runCatching {
            connect()
            function.invoke(this)
        }?.onFailure {
            it.printStackTrace()
        }
        mifareClassic?.runCatching { close() }
        return result?.isSuccess ?: false
    }


    /**
     * 出厂初始值一般为 0xFF 0x07 0x80 0x69
     * 密码一般为 FF FF FF FF FF FF
     *
     * M1存取控制字节解析
     * https://blog.csdn.net/qq_39772670/article/details/119912468
     */
    fun newFormat(tag: Tag): Boolean = invoke(tag) { mifareClassic ->
        for (i in 0 until mifareClassic.sectorCount) {
            val keyA: ByteArray? = keyList.firstOrNull {
                mifareClassic.authenticateSectorWithKeyA(i, it)
            }
            var keyB: ByteArray? = keyList.firstOrNull {
                mifareClassic.authenticateSectorWithKeyB(i, it)
            }
            if (keyA == null && keyB == null) {
                println("无法破解$i 扇区")
                continue
            }

            val bIndex = mifareClassic.sectorToBlock(i)
            val block3 = mifareClassic.readBlock(bIndex + 3)
            if (!M1AccessControlUtils.canRewriteControlCodeByKeyB(block3[6], block3[7], block3[8])) {
                // filter [0B000, 0B010, 0B100, 0B110, 0B111] = 控制字不可写
                println("$i 扇区, ${ByteUtils.byteArrayToHexString(block3, separator = " ")} 无法被修改")
                continue
            }

            if (keyB != null) { // [0B001, 0B011, 0B101] = 验证KeyB控制字可写，但不一定是 FF:07:80:69
                mifareClassic.writeBlock(bIndex + 3, (keyA ?: defaultKeyA) + defaultControl + keyB)
            } else if (keyA != null && M1AccessControlUtils.canRewriteControlCodeByKeyA(block3[6], block3[7], block3[8])) { // [0B001] = 验证KeyA|B控制字可写，但不一定是 FF:07:80:69
                // 这里KeyA一定不为Null，前面已经过滤了KeyA == null 的情况
                keyB = byteArrayOf(block3[10], block3[11], block3[12], block3[13], block3[14], block3[15])
                mifareClassic.writeBlock(bIndex + 3, keyA + defaultControl + keyB)
            } else { // [0B011, 0B101] = 验证KeyB可写，但KeyB不可读
                println("KeyA != null, KeyB == null, block3 = ${ByteUtils.byteArrayToHexString(block3, separator = " ")}")
                continue
            }

            println("$i 扇区, KeyA: ${keyA?.let { ByteUtils.byteArrayToHexString(it, separator = ":") }}\nKeyB: ${ByteUtils.byteArrayToHexString(keyB, separator = ":")}")

            // 这一步，KeyB一定不能为null，且控制字为FF:07:80:69, KeyA可能为null
            if (i != 0) { // 0扇区0块，出厂信息，不可修改
                mifareClassic.writeBlock(bIndex + 0, emptyData)
            }
            mifareClassic.writeBlock(bIndex + 1, emptyData)
            mifareClassic.writeBlock(bIndex + 2, emptyData)
            mifareClassic.writeBlock(bIndex + 3, controlData) // 修改KeyA/KeyB 为Default
        }
    }


//    fun copy(tag: Tag, copySource: MifareClassicData): Boolean {
//        val mifareClassic = MifareClassic.get(tag)
//        val result = runCatching {
//            mifareClassic.connect()
//
//            // TODO: 校验内存大小的匹配性
//            val sCount = mifareClassic.sectorCount
//            val allBCount = mifareClassic.blockCount
//            if (sCount != copySource.sectorCount || allBCount != copySource.blockCount) {
//                throw Exception("被复制卡片与数据源不匹配!!")
//            }
//
//
//            // TODO: 暂且不复制密钥块和00块
//            for (i in 0 until sCount) {
//                if (!m1Auth(mifareClassic, i, MifareClassic.KEY_DEFAULT)) {
////                    continue
//                    throw Exception("复制失败")
//                }
//                val bCount = mifareClassic.getBlockCountInSector(i)
//                var bIndex = mifareClassic.sectorToBlock(i)
//                val t = if (i == 0) {
//                    bIndex++
//                    1
//                } else {
//                    0
//                }
//
//                for (j in t until bCount) {
//                    val data = copySource.sectorList[i].blockList[j].data
//                    println(
//                        "块号: $bIndex, 复制数据: ${
//                            ByteUtils.byteArrayToHexString(
//                                data,
//                                separator = " "
//                            )
//                        }"
//                    )
//                    mifareClassic.writeBlock(bIndex++, data)
//                }
//            }
//        }.onFailure {
//            it.printStackTrace()
//        }
//        mifareClassic.runCatching { close() }
//
//        return result.isSuccess
//    }

    private fun readBlock(mifareClassic: MifareClassic): List<M1Sector> {
        val sectorList = arrayListOf<M1Sector>()
        for (i in 0 until mifareClassic.sectorCount) {

            val bCount = mifareClassic.getBlockCountInSector(i) // //获得当前扇区的所包含块的数量
            val bIndex = mifareClassic.sectorToBlock(i) //当前扇区的第1块的块号

            // 暴力破解密钥A|B
            val keyA = keyList.firstOrNull {
                mifareClassic.authenticateSectorWithKeyA(i, it)
            }
            var keyB = keyList.firstOrNull {
                mifareClassic.authenticateSectorWithKeyB(i, it)
            }
            if (keyA == null && keyB == null) {
                sectorList.add(M1Sector(i, bIndex, bCount, "", "", emptyList()))
                println("$i 扇区, 无法读取")
                continue
            }
            val block3 = mifareClassic.readBlock(bIndex + 3)
            if (keyA != null && keyB == null && M1AccessControlUtils.canReadKeyBByKeyA(
                    block3[6],
                    block3[7],
                    block3[8]
                )
            ) {
                keyB = byteArrayOf(block3[10], block3[11], block3[12], block3[13], block3[14], block3[15])
            }

            // 读取块信息
            val blockList = arrayListOf<Block>()
            for (j in 0 until bCount - 1) {
                val canRead = if (keyB != null) M1AccessControlUtils.canReadDataBlockByKeyB(
                    j,
                    block3[6],
                    block3[7],
                    block3[8]
                )
                else M1AccessControlUtils.canReadDataBlockByKeyA(j, block3[6], block3[7], block3[8])
                if (canRead) {
                    val block = mifareClassic.readBlock(bIndex + j)
                    blockList.add(Block(bIndex + j, block))
                    println("$i 扇区, $j 块区, ${ByteUtils.byteArrayToHexString(block, separator = " ")}")
                } else {
                    blockList.add(Block(bIndex + j, ByteArray(0)))
                    println("$i 扇区, $j 块区, 无法读取")
                }
            }
            val decBlock = (keyA ?: defaultKeyA) + byteArrayOf(block3[6], block3[7], block3[8], block3[9]) + (keyB ?: defaultKeyB)
            blockList.add(Block(bIndex + 3, decBlock))
            println("$i 扇区, 4 块区, ${ByteUtils.byteArrayToHexString(decBlock, separator = " ")}")

            // 保存信息
            sectorList.add(
                M1Sector(i, bIndex, bCount,
                ByteUtils.byteArrayToHexString(keyA ?: ByteArray(0), separator = " "),
                ByteUtils.byteArrayToHexString(keyB ?: ByteArray(0), separator = " "), blockList)
            )
        }
        return sectorList
    }

}


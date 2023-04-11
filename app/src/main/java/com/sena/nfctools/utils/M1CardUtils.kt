package com.sena.nfctools.utils

import android.nfc.Tag
import android.nfc.tech.MifareClassic
import android.nfc.tech.NfcA
import com.sena.nfctools.bean.*
import java.nio.ByteBuffer
import java.nio.charset.Charset


/**
 * FileName: M1CardUtils
 * Author: JiaoCan
 * Date: 2023/3/27 18:29
 */

object M1CardUtils {

    private val keyList = arrayOf(
        MifareClassic.KEY_DEFAULT,
        byteArrayOf(0x00, 0x00, 0x00, 0x00, 0x00, 0x00)
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


    fun readM1Card(tag: Tag): MifareClassicData? {

        val keyList = arrayOf(
            MifareClassic.KEY_DEFAULT,
            byteArrayOf(0x00, 0x00, 0x00, 0x00, 0x00, 0x00)
        )

        val mifareClassic = MifareClassic.get(tag)
        try {
            mifareClassic.connect()

            val sectorCount = mifareClassic.sectorCount // 扇区数
            val blockCont = mifareClassic.blockCount // 总块数
            val size = mifareClassic.size // 内存大小
            val type = mifareClassic.type
            val typeStr = when (mifareClassic.type) { // 类型
                MifareClassic.TYPE_CLASSIC -> "TYPE_CLASSIC"
                MifareClassic.TYPE_PLUS -> "TYPE_PLUS"
                MifareClassic.TYPE_PRO -> "TYPE_PRO"
                // 这个不可能执行，MifareClassic的构造中，
                // 如果没找到上述三个类型，自动抛出运行时异常
                else -> "TYPE_UNKNOWN"
            }
            val sectorList = readBlock(mifareClassic, sectorCount)

            val a = NfcA.get(tag)
            val atqa = a.atqa
            atqa.forEach {
                println("atqa: $it")
            }
            val atqaHex = ByteUtils.byteArrayToHexString(atqa)
            val sak = a.sak
            val sakHex = ByteUtils.shortToHexString(sak)
            println("Info\n扇区数: $sectorCount\n总块数: $blockCont\n内存大小: $size\n类型: $type")
            println("ATAQ: $atqaHex\nSAK: $sakHex")

            return MifareClassicData(
                sectorCount = sectorCount,
                blockCount = blockCont,
                size = size,
                type = type,
                typeStr = typeStr,
                atqa = atqa,
                sak = sak,
                sectorList = sectorList
            )
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        } finally {
            try {
                mifareClassic.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    /**
     * 出厂初始值一般为 0xFF 0x07 0x80 0x69
     * 密码一般为 FF FF FF FF FF FF
     *
     * M1存取控制字节解析
     * https://blog.csdn.net/qq_39772670/article/details/119912468
     */
    fun newFormat(tag: Tag) {
        val mifareClassic = MifareClassic.get(tag)
        mifareClassic.connect()

        val sCount = mifareClassic.sectorCount
        var bIndex: Int
        val keyA = ByteArray(6)
        val keyB = ByteArray(6)
        val control = ByteArray(4)
        for (i in 0 until sCount) {
            B@ for (j in keyList.indices) {
                if (!m1Auth(mifareClassic, i, keyList[j])) continue@B
                bIndex = mifareClassic.sectorToBlock(i)
                var block3 = mifareClassic.readBlock(bIndex + 3)
                val oldControl = byteArrayOf(block3[6], block3[7], block3[8], block3[9])

                if (M1AccessControlUtils.canRewriteAccessCode(oldControl)) {
                    mifareClassic.writeBlock(bIndex + 3, keyA + defaultControl + keyB) // 修改访问控制码为Default，以提权达到修改

//                    val r = mifareClassic.authenticateSectorWithKeyB(i, defaultKeyB) // 必定成功
                    block3 = mifareClassic.readBlock(bIndex + 3)


                    val buffer = ByteBuffer.wrap(block3)
                    buffer.get(keyA)  // KeyA 永远不可读
                    buffer.get(control) //
                    buffer.get(keyB)
                    buffer.clear()

//                    println("校验结果: $r\n" +
//                            "KeyA: ${ByteUtils.byteArrayToHexString(keyA)}\n" +
//                            "keyB: ${ByteUtils.byteArrayToHexString(keyB)}\n控制块: ${ByteUtils.byteArrayToHexString(control)}")
//                    println("Bindex: $bIndex")

                    if (i != 0) { // 0扇区0块，出厂信息，不可修改
                        mifareClassic.writeBlock(bIndex + 0, emptyData)
                    }
                    mifareClassic.writeBlock(bIndex + 1, emptyData)
                    mifareClassic.writeBlock(bIndex + 2, emptyData)
                    mifareClassic.writeBlock(bIndex + 3, controlData) // 修改KeyA/KeyB 为Default

                } else {
                    println("覆写$i 扇区${bIndex + 3} 块, 访问控制层失败, ${ByteUtils.byteArrayToHexString(
                        controlData, separator = ", ")}")
                }
                break@B
            }
        }

        mifareClassic.close()
    }

    fun writeBlock(
        tag: Tag,
        sectorIndex: Int,
        blockIndex: Int,
        blockData: ByteArray,
        key: ByteArray = MifareClassic.KEY_DEFAULT
    ) {
        val mifareClassic = MifareClassic.get(tag)

        try {
            mifareClassic.connect()

            val tSize = mifareClassic.maxTransceiveLength
            println("tSize: $tSize")
            val i = mifareClassic.sectorToBlock(sectorIndex)
            println(i)
            if (m1Auth(mifareClassic, sectorIndex, key) && mifareClassic.isConnected) {
                mifareClassic.writeBlock(blockIndex, blockData)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                mifareClassic.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun copy(tag: Tag, copySource: MifareClassicData): Boolean {
        val mifareClassic = MifareClassic.get(tag)
        val result = runCatching {
            mifareClassic.connect()

            // TODO: 校验内存大小的匹配性
            val sCount = mifareClassic.sectorCount
            val allBCount = mifareClassic.blockCount
            if (sCount != copySource.sectorCount || allBCount != copySource.blockCount) {
                throw Exception("被复制卡片与数据源不匹配!!")
            }


            // TODO: 暂且不复制密钥块和00块
            for (i in 0 until sCount) {
                if (!m1Auth(mifareClassic, i, MifareClassic.KEY_DEFAULT)) {
//                    continue
                    throw Exception("复制失败")
                }
                val bCount = mifareClassic.getBlockCountInSector(i)
                var bIndex = mifareClassic.sectorToBlock(i)
                val t = if (i == 0) {
                    bIndex++
                    1
                } else {
                    0
                }

                for (j in t until bCount) {
                    val data = copySource.sectorList[i].blockList[j].data
                    println(
                        "块号: $bIndex, 复制数据: ${
                            ByteUtils.byteArrayToHexString(
                                data,
                                separator = " "
                            )
                        }"
                    )
                    mifareClassic.writeBlock(bIndex++, data)
                }
            }
        }.onFailure {
            it.printStackTrace()
        }
        mifareClassic.runCatching { close() }

        return result.isSuccess
    }

    private fun m1Auth(m1: MifareClassic, pos: Int, key: ByteArray): Boolean {
        return if (m1.authenticateSectorWithKeyA(pos, key)) {
            println("使用密钥A对$pos 扇区校验成功")
            true
        } else if (m1.authenticateSectorWithKeyB(pos, key)) {
            println("使用密钥B ${ByteUtils.byteArrayToHexString(key)}对$pos 扇区校验成功")
            true
        } else {
            println("$pos 扇区校验失败")
            false
        }
    }

    private fun readBlock(
        mifareClassic: MifareClassic,
        sectorCount: Int,
        key: ByteArray = MifareClassic.KEY_DEFAULT
    ): List<MifareClassicSector> {
        val sectorList = arrayListOf<MifareClassicSector>()
        for (i in 0 until sectorCount) {
            if (!m1Auth(mifareClassic, i, key)) {
                continue
            }
            val blockList = arrayListOf<MifareClassicBlock>()
            val bCount = mifareClassic.getBlockCountInSector(i) // //获得当前扇区的所包含块的数量
            var bIndex = mifareClassic.sectorToBlock(i) //当前扇区的第1块的块号
            for (j in 0 until bCount) {
                val data = mifareClassic.readBlock(bIndex)
                blockList.add(MifareClassicBlock(bIndex, data))
                val hexStr = ByteUtils.byteArrayToHexString(data, separator = " ")
                println("$i 扇区 $j 块: $hexStr")
                println("对应文本: ${String(data, Charset.forName("US-ASCII"))}")
                bIndex++
            }

            sectorList.add(
                MifareClassicSector(
                    sectorIndex = i,
                    blockStartIndex = bIndex - bCount,
                    blockCount = bCount,
                    key = key,
                    blockList = blockList
                )
            )
        }
        return sectorList
    }

}


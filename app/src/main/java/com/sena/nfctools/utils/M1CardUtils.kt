package com.sena.nfctools.utils

import android.content.Context
import android.net.Uri
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiManager
import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.MifareClassic
import android.nfc.tech.MifareUltralight
import android.nfc.tech.Ndef
import android.nfc.tech.NfcA
import com.sena.nfctools.BuildConfig
import com.sena.nfctools.bean.*
import java.nio.ByteBuffer
import java.nio.charset.Charset


/**
 * FileName: M1CardUtils
 * Author: JiaoCan
 * Date: 2023/3/27 18:29
 */

object M1CardUtils {

    fun readM1Card(tag: Tag): MifareClassicData? {

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

    // TODO: 重置访问密码为Default
    fun format(tag: Tag) {
        val blockData = ByteArray(16)
        for (i in 0  until 16) {
            blockData[i] = 0x00.toByte()
        }

        val mifareClassic = MifareClassic.get(tag)
        try {
            if (!mifareClassic.isConnected) mifareClassic.connect()
            val sCount = mifareClassic.sectorCount

            for (i in 1 until sCount) {
                if (!m1Auth(mifareClassic, i, MifareClassic.KEY_DEFAULT)) continue
                val bCount = mifareClassic.getBlockCountInSector(i)
                var bIndex = mifareClassic.sectorToBlock(i)

                for (j in 0 until bCount - 1) { // 循环次数, 不重置最后一个控制区块
                    println("i: $i, j: $bIndex")
                    mifareClassic.writeBlock(bIndex, blockData)
                    bIndex++
                }
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

    fun writeBlock(tag: Tag, sectorIndex: Int, blockIndex: Int, blockData: ByteArray, key: ByteArray = MifareClassic.KEY_DEFAULT) {
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

    private fun m1Auth(m1: MifareClassic, pos: Int, key: ByteArray): Boolean {
        return if (m1.authenticateSectorWithKeyA(pos, key)) {
            println("使用密钥A对$pos 扇区校验成功")
            true
        } else if (m1.authenticateSectorWithKeyB(pos, key)) {
            println("使用密钥B对$pos 扇区校验成功")
            true
        } else {
            println("$pos 扇区校验失败")
            false
        }
    }

    private fun readBlock(mifareClassic: MifareClassic, sectorCount: Int, key: ByteArray = MifareClassic.KEY_DEFAULT): List<MifareClassicSector> {
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

            sectorList.add(MifareClassicSector(
                sectorIndex = i,
                blockStartIndex = bIndex - bCount,
                blockCount = bCount,
                key = key,
                blockList = blockList
            ))
        }
        return sectorList
    }



}


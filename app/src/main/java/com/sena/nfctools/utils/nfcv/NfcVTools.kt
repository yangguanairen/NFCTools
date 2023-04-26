package com.sena.nfctools.utils.nfcv

import android.nfc.Tag
import android.nfc.tech.NfcV
import android.nfc.tech.TagTechnology
import com.sena.nfctools.newBean.Block
import com.sena.nfctools.newBean.NfcVData
import com.sena.nfctools.utils.ByteUtils


/**
 * FileName: NfcVUtils
 * Author: JiaoCan
 * Date: 2023/4/19 17:10
 */



object NfcVTools {

    fun read(tag: Tag): NfcVData? {
        val rf15693 = RF15693.get(tag) ?: return null
        try {
            rf15693.connect()
            val nfcVData = NfcVData()
            nfcVData.manufacturer = "NXP I CODE SLIX"  // 暂时定死值，这里需要根据id进行判定
            nfcVData.blockCount = rf15693.getBlockCount()
            nfcVData.blockSize = rf15693.getBlockSize()

            val list = arrayListOf<Block>()
            for (i in 0 until rf15693.getBlockCount()) {
                val data = rf15693.readSingleBlock(i)
                println("read: $i, data: ${ByteUtils.byteArrayToHexString(data ?: ByteArray(0), separator = " ")}")
                list.add(Block(i, data ?: ByteArray(0)))
            }
            nfcVData.blocks = list

            val nfcV = NfcV.get(tag)
            nfcVData.dsfid = nfcV.dsfId
            nfcVData.responseFlags = nfcV.responseFlags


//            val extras = tag.getTechExtras(TagTechnology.NFC_V)
//            nfcVData.dsfid = extras.getByte(NfcV.EXTRA_DSFID)
//            nfcVData.responseFlags = extras.getByte(NfcV.EXTRA_RESP_FLAGS)

            return nfcVData;

        } catch (e: Exception) {
            e.printStackTrace()
            return null
        } finally {
            rf15693.runCatching { close() }
        }

    }

    private val emptyData = byteArrayOf(0x00, 0x00, 0x00, 0x00)

    fun format(tag: Tag): Boolean {
        val t15693 = RF15693.get(tag)
        val result = t15693.runCatching {
            connect()
            for (i in 0 until getBlockCount()) {
                println("写入: Block Index: $i")
                writeSingleBlock(i, emptyData)
            }
        }.onFailure { it.printStackTrace() }
        t15693.runCatching { close() }
        return result.isSuccess
    }

}


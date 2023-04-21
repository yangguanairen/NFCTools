package com.sena.nfctools.utils.nfcv

import android.nfc.Tag
import com.sena.nfctools.newBean.T15693Block
import com.sena.nfctools.newBean.T15693Data
import com.sena.nfctools.utils.ByteUtils


/**
 * FileName: NfcVUtils
 * Author: JiaoCan
 * Date: 2023/4/19 17:10
 */



object NfcVUtils {

    fun read15693(tag: Tag): T15693Data? {
        val t15693 = T15693.get(tag) ?: return null
        try {
            t15693.connect()
            val blockCount = t15693.getBlockCount()
            val blockSize = t15693.getBlockSize()
            val list = arrayListOf<T15693Block>()
            for (i in 0 until blockCount) {
                val data = t15693.readSingleBlock(i)
                println("read: $i, data: ${ByteUtils.byteArrayToHexString(data ?: ByteArray(0), separator = " ")}")
                list.add(T15693Block(i, data ?: ByteArray(0)))
            }
            return T15693Data(blockCount, blockSize, list)

        } catch (e: Exception) {
            e.printStackTrace()
            return null
        } finally {
            t15693.runCatching { close() }
        }

    }

    private val emptyData = byteArrayOf(0x00, 0x00, 0x00, 0x00)

    fun format(tag: Tag): Boolean {
        val t15693 = T15693.get(tag)
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


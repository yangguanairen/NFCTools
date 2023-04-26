package com.sena.nfctools.utils.nfcv

import android.nfc.Tag
import android.nfc.tech.NfcV
import com.sena.nfctools.utils.ByteUtils


/**
 * FileName: ICodeSliX
 * Author: JiaoCan
 * Date: 2023/4/20 11:11
 */

// 已经编写好的NfcV读写工具类
// https://blog.csdn.net/black03077/article/details/125365629

// ISO 15693 协议 手册
// https://pdf1.alldatasheet.com/datasheet-pdf/download/145969/FUJITSU/ISO15693.html

// Stack Overflow, 无法读取的问题
// https://stackoverflow.com/questions/28405558/android-nfc-read-iso15693-rfid-tag

class RF15693 {

    private val mid: ByteArray
    private var mNfcV: NfcV

    private constructor(id: ByteArray, nfcV: NfcV) {
        mid = id
        mNfcV = nfcV
    }

    private var blockSize = -1
    private var blockCount = -1


    companion object {

        @JvmStatic
        fun get(tag: Tag): RF15693 {
            val nfcV = NfcV.get(tag)
            return RF15693(tag.id, nfcV)
        }
    }

    /**
     * bit1 高1位, 也就是手册里的bit7
     * 1: add blockstatus, 1byte
     * 0: direct return data
     */
    /**
     * request: Flag(8bit) | Command(8bit) | UID(64bit) | BlockIndex(8bit) | CRC(16bit)
     * correctResponse: Flag(8bit) | BlockStatus(option, 8bit) | Data(64bit) | CRC(16bit)
     */


    fun readSingleBlock(offset: Int): ByteArray? {
        val cmd = ByteArray(11)
        cmd[0] = 0b01100010
        cmd[1] = 0x20
        System.arraycopy(mid, 0, cmd, 2, mid.size)
        cmd[10] = offset.toByte()

        val response = mNfcV.transceive(cmd)
        val status = response[0]
        if (status != 0x00.toByte()) return null
        val blockStatus = response[1]

        return response.sliceArray(2 until response.size)
    }

    fun writeSingleBlock(offset: Int, data: ByteArray) {
        val cmd = ByteArray(15)
        cmd[0] = 0x22
        cmd[1] = 0x21
        System.arraycopy(mid, 0, cmd, 2, mid.size)
        cmd[10] = offset.toByte()
        System.arraycopy(data, 0, cmd, 11, data.size)

        val response = mNfcV.transceive(cmd)
        val resFlag = response[0]
        if (resFlag != 0x00.toByte()) {
            val errorCode = response[1]
            println("写入失败, ${ByteUtils.byteToHexString(errorCode, true)}")
            return
        } else {
            println("写入成功")
        }
    }



    fun getSystemInfo() {
        val cmd = ByteArray(10)
        cmd[0] = 0x22
        cmd[1] = 0x2B
        System.arraycopy(mid, 0, cmd, 2, mid.size)

        val response = mNfcV.transceive(cmd)
        val resFlag = response[0]
        if (resFlag != 0x00.toByte()) {
            val errorCode = response[1]
            println("写入失败, ${ByteUtils.byteToHexString(errorCode, true)}")
            return
        } else {
            println("写入成功")
            val infoFlag = response[1].toInt()
            val id = response.sliceArray(2..9)
            val dsfid = response[10]
            val afi = response[11]
            val memorySize = response.sliceArray(12..13)
            val icReference = response[14]

            val isSupportDsfid = (infoFlag shr 0) and 0x01
            val isSupportAfi = (infoFlag shr 1) and 0x01
            val isSupportIcReference = (infoFlag shr 3) and 0x01

            val isSupportMemorySize = (infoFlag shr 2) and 0x01
            if (isSupportMemorySize == 1) {
                blockCount = (memorySize[0].toInt() and 0B00011111) + 1
                blockSize = memorySize[1].toInt() + 1

                println("blockSize: $blockSize, blockCount: $blockCount")
            }


        }

    }

    fun connect() {
        mNfcV.connect()
        getSystemInfo()
    }

    fun close() {
        mNfcV.close()
    }

    fun getBlockCount(): Int = blockCount

    fun getBlockSize(): Int = blockSize


}


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
import com.sena.nfctools.bean.M1Block
import com.sena.nfctools.bean.M1Card
import com.sena.nfctools.bean.M1Sector
import java.nio.ByteBuffer
import java.nio.charset.Charset


/**
 * FileName: M1CardUtils
 * Author: JiaoCan
 * Date: 2023/3/27 18:29
 */

object M1CardUtils {

    fun readM1Card(tag: Tag): M1Card? {

        val mifareClassic = MifareClassic.get(tag)
        try {
            mifareClassic.connect()
            val id = tag.id
            val techList = arrayListOf<String>().apply {
                tag.techList?.forEach {
                    this.add(it)
                }
            }
            val sectorCount = mifareClassic.sectorCount // 扇区数
            val blockCont = mifareClassic.blockCount // 总块数
            val size = mifareClassic.size // 内存大小
            val type = when (mifareClassic.type) { // 类型
                MifareClassic.TYPE_CLASSIC -> "TYPE_CLASSIC"
                MifareClassic.TYPE_PLUS -> "TYPE_PLUS"
                MifareClassic.TYPE_PRO -> "TYPE_PRO"
                // 这个不可能执行，MifareClassic的构造中，
                // 如果没找到上述三个类型，自动抛出运行时异常
                else -> "TYPE_UNKNOWN"
            }

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

            val sectorList = readBlock(mifareClassic, sectorCount)

            val m1Card = M1Card(
                id, techList,
                sectorCount, blockCont, size,
                atqa, sak, sectorList
            )
            return m1Card

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
            mifareClassic.connect()
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

    fun testWriteWifi(tag: Tag, context: Context) {

        // http://androidxref.com/5.1.0_r1/xref/packages/apps/Settings/src/com/android/settings/wifi/WriteWifiConfigToNfcDialog.java
        // https://github.com/bparmentier/WiFiKeyShare/blob/ec251136264d011528d859399d38eb3cf5ebe0a8/app/src/main/java/be/brunoparmentier/wifikeyshare/utils/NfcUtils.java#L174

        val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
//        val list = wifiManager.configuredNetworks
//        list.forEach {
//            it.SSID
//        }

        val ssid = "Aitmed-ECOS"
        val passwd = "aitmed123"
        val authType: Short = 0x0020

        val credential_field_id: Short = 0x100E
        val ssid_field_id: Short = 0x1405
        val auth_type_field_id: Short = 0x1003
        val network_index_field_Id: Short = 0x1026

        val ssidSize = ssid.toByteArray().size.toShort()
        val passwdSize = passwd.toByteArray().size.toShort()


        val bufferSize = 18 + ssidSize + passwdSize
        val buffer = ByteBuffer.allocate(bufferSize)
        buffer.putShort(credential_field_id)
        buffer.putShort((bufferSize - 4).toShort())

        buffer.putShort(ssid_field_id)
        buffer.putShort(ssidSize)
        buffer.put(ssid.toByteArray())

        buffer.putShort(auth_type_field_id)
        buffer.putShort(2.toShort())
        buffer.putShort(authType)

        buffer.putShort(network_index_field_Id)
        buffer.putShort(passwdSize)
        buffer.put(passwd.toByteArray())
        val array = buffer.array()
        println(ByteUtils.byteArrayToHexString(array))

        val token_mime_type = "application/vnd.wfa.wsc"

//        val mimeRecord = NdefRecord(
//            NdefRecord.TNF_MIME_MEDIA,
//            token_mime_type.toByteArray(Charset.forName("US-ASCII")),
//            ByteArray(0),
//            buffer.array()
//        )
        val mimeRecord = NdefRecord.createMime(
            token_mime_type,
            array
        )
        val aarRecord: NdefRecord = NdefRecord.createApplicationRecord(BuildConfig.APPLICATION_ID)
        val ndefMessage = NdefMessage(mimeRecord)


        val data = ndefMessage.toByteArray()
        var readIndex = 0

        val mifareClassic = MifareClassic.get(tag)
        mifareClassic.connect()
        val sCount = mifareClassic.sectorCount

        for (i in 1 until sCount) {
            if (!m1Auth(mifareClassic, i, MifareClassic.KEY_DEFAULT)) continue
            val bCount = mifareClassic.getBlockCountInSector(i)
            var bIndex = mifareClassic.sectorToBlock(i)

            for (j in 0 until bCount - 1) {
                val writeData = ByteArray(16)
                for (z in 0 until 16) {
                    writeData[z] = if (readIndex >= data.size) 0 else data[readIndex]
                    readIndex++
                }
                println("区块: $bIndex, 写入数据: ${ByteUtils.byteArrayToHexString(writeData)}")
                mifareClassic.writeBlock(bIndex, writeData)

                bIndex++
            }
        }
        mifareClassic.close()

//        val ndef = Ndef.get(tag)
//        ndef.connect()
//        ndef.writeNdefMessage(ndefMessage)



//        val wifiConfig = WifiConfiguration()
//        wifiConfig.SSID =

        // ssid, passwd, encryptMethod, Authentication
    }


    fun test(tag: Tag) {


        val blockData = ByteArray(16)
        for (i in 0  until 16) {
            blockData[i] = 0x00.toByte()
        }
        for (i in 1 until 16) {
            for (j in 4 until 5) {
                println("i: $i, j: $j")
                writeBlock(tag, i, j, blockData)
            }
        }


        val appRecord = NdefRecord.createApplicationRecord("")
        val uriRecord = NdefRecord.createUri(Uri.parse(""))
        val textRecord = NdefRecord.createTextRecord("", "")
        val mimeRecord = NdefRecord.createMime("", byteArrayOf())
        val externRecord = NdefRecord.createExternal("", "", byteArrayOf())

//        val mifareClassic = MifareClassic.get(tag)
//        mifareClassic.connect()
////        for (i in 0 until mifareClassic.sectorCount) {
////            val bCount = mifareClassic.getBlockCountInSector(i)
////            val bIndex = mifareClassic.sectorToBlock(i)
////            for (j in bIndex until bIndex + bCount - 1) {
////                mifareClassic.writeBlock(j, ByteArray(16))
////            }
////        }
//
//        val ndefRecord = NdefRecord.createApplicationRecord("bin.nt.plus")
//        val ndefMessage = NdefMessage(ndefRecord)
//        val data = ndefMessage.toByteArray()
//        println("Ndef write test: ${ByteUtils.byteArrayToHexString(data, separator = ", ")}")
//
//
////        for (i in data.indices) {
////            val sIndex = i / 64
////            val bIndex = (data.size - i) / 16
////            val t = data.
////        }
//
//        for (i in 1 until mifareClassic.sectorCount) {
//            val bCount = mifareClassic.getBlockCountInSector(i)
//            var bIndex = mifareClassic.sectorToBlock(i)
//            println("bCount: $bCount, bIndex: $bIndex")
//            val writeData = ByteArray(16)
//
//            val sIndex = (i - 1) * 64 + bIndex * 16
//
////            for (z in 0 until 16) {
////                val tIndex = sIndex + z
////                writeData[z] = if (tIndex > data.size) 0 else data[tIndex]
////            }
//            for (j in 0 until 3) {
//                println("j: $j, data: ${ByteUtils.byteArrayToHexString(writeData)}")
//                if (m1Auth(mifareClassic, j, MifareClassic.KEY_DEFAULT)) {
//                    mifareClassic.writeBlock(j, writeData)
//                }
//                bIndex++
//            }
//        }


//        val ndef = Ndef.get(tag)
////
////
////
//        ndef.connect()
//        val ndefRecord = NdefRecord.createApplicationRecord("bin.mt.plus")
//        val ndefMessage = NdefMessage(ndefRecord)
//        val data = ndefMessage.toByteArray()
//        println("Ndef write test: ${ByteUtils.byteArrayToHexString(data, separator = ", ")}")
//        ndef.writeNdefMessage(ndefMessage)
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

    fun m1Auth(m1: MifareClassic, pos: Int, key: ByteArray): Boolean {
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

    private fun readBlock(mifareClassic: MifareClassic, sectorCount: Int, key: ByteArray = MifareClassic.KEY_DEFAULT): List<M1Sector> {
        val sectorList = arrayListOf<M1Sector>()
        for (i in 0 until sectorCount) {
            if (!m1Auth(mifareClassic, i, key)) {
                continue
            }
            val blockList = arrayListOf<M1Block>()
            val bCount = mifareClassic.getBlockCountInSector(i) // //获得当前扇区的所包含块的数量
            var bIndex = mifareClassic.sectorToBlock(i) //当前扇区的第1块的块号
            for (j in 0 until bCount) {
                val data = mifareClassic.readBlock(bIndex)
                blockList.add(M1Block(bIndex, data))
                val hexStr = ByteUtils.byteArrayToHexString(data, separator = " ")
                println("$i 扇区 $j 块: $hexStr")
                println("对应文本: ${String(data, Charset.forName("US-ASCII"))}")
                bIndex++
            }

            sectorList.add(
                M1Sector(
                sectorIndex =  i,
                blockStartIndex =  bIndex,
                blockEndIndex =  bIndex + bCount,
                key = key,
                blockList = blockList
            )
            )
        }
        return sectorList
    }



}


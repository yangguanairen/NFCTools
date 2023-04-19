package com.sena.nfctools.other

import android.net.wifi.WifiConfiguration
import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.Tag
import android.nfc.tech.Ndef
import com.sena.nfctools.utils.ByteUtils
import java.nio.ByteBuffer


/**
 * FileName: Nfc_Wifi_Test
 * Author: JiaoCan
 * Date: 2023/4/4 17:03
 */

/**
 * NFC录入Wifi数据
 * https://github.com/bparmentier/WiFiKeyShare/blob/ec251136264d011528d859399d38eb3cf5ebe0a8/app/src/main/java/be/brunoparmentier/wifikeyshare/ui/activities/WifiListActivity.java
 * Android Wifi 详解
 * https://www.jianshu.com/p/ca13c4d40710
 *
 *  http://androidxref.com/5.1.0_r1/xref/packages/apps/Settings/src/com/android/settings/wifi/WriteWifiConfigToNfcDialog.java
 *
 * 下面那几个ID，不知道是怎么的出来的，
 * 没找的出处
 */

object Nfc_Wifi_Test {


    fun write(tag: Tag) {

        val ssid = "Aitmed-ECOS"
        val passwd = "aitmed123"
        val authType = AUTH_TYPE_WPA2_PSK
        val ecyType = ENC_TYPE_NONE

        val mimeRecord = NdefRecord.createMime(
            NFC_TOKEN_MIME_TYPE,
            generatePayload(ssid, passwd, authType, ecyType)
        )
        val ndefMessage = NdefMessage(mimeRecord)
        val data = ndefMessage.toByteArray()
        println("NedfMessage: ${ByteUtils.byteArrayToHexString(data, separator = " ")}")

        val ndef = Ndef.get(tag)
        try {
            ndef.connect()
            ndef.writeNdefMessage(ndefMessage)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                ndef.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun generatePayload(ssid: String, key: String, authType: Short, ecyType: Short): ByteArray {

        val ssidSize = ssid.toByteArray().size.toShort()
        val passwdSize = key.toByteArray().size.toShort()

        val bufferSize = 18 + ssidSize + passwdSize
        val buffer = ByteBuffer.allocate(bufferSize)
        buffer.putShort(CREDENTIAL_FIELD_ID)
        buffer.putShort((bufferSize - 4).toShort())

        buffer.putShort(SSID_FIELD_ID)
        buffer.putShort(ssidSize)
        buffer.put(ssid.toByteArray())

        buffer.putShort(AUTH_TYPE_FIELD_ID)
        buffer.putShort(2.toShort())
        buffer.putShort(authType)
        buffer.putShort(NETWORK_KEY_FIELD)
        buffer.putShort(passwdSize)
        buffer.put(key.toByteArray())

        buffer.putShort(ENC_TYPE_FIELD_ID)
        buffer.putShort(2.toShort())
        buffer.putShort(ecyType)

        val array = buffer.array()
        println("GeneratePayload: "+ ByteUtils.byteArrayToHexString(array, separator = " "))
        return array
    }

    fun parse(tag: Tag) {
        val ndef = Ndef.get(tag)
        ndef.connect()
        val message = ndef.ndefMessage
        val records = message.records
        records.forEach { record ->
            if ("application/vnd.wfa.wsc" == String(record.type)) {
                val payload = ByteBuffer.wrap(record.payload)
                while (payload.hasRemaining()) {
                    val fieldId = payload.short
                    val fieldSize = payload.short
                    if (CREDENTIAL_FIELD_ID == fieldId) {
                        parseCredential(payload, fieldSize)
                    } else {
                        payload.position(payload.position() + fieldSize)
                    }
                }

            }
        }
        ndef.close()
    }

     fun parseCredential(payload: ByteBuffer, size: Short): WifiConfiguration? {
        val startPos = payload.position()
        val config = WifiConfiguration()
        while (payload.position() < startPos + size) {
            val fieldId = payload.short
            val fieldSize = payload.short

            if (payload.position() + fieldSize > startPos + size) {
                return null
            }

            when (fieldId) {
                SSID_FIELD_ID -> {
                    val ssid = ByteArray(fieldSize.toInt())
                    payload.get(ssid)
                    println("SSID: ${ByteUtils.byteArrayToHexString(ssid, separator = " ")}")
                    println("SSID: ${String(ssid)}")
                    config.SSID = "\"${String(ssid)}\""
                }
                NETWORK_KEY_FIELD -> {
                    val key = ByteArray(fieldSize.toInt())
                    payload.get(key)
                    println("KEY: ${ByteUtils.byteArrayToHexString(key, separator = " ")}")
                    println("KEY: ${String(key)}")
                    config.preSharedKey = "\"${String(key)}\""
                }
                AUTH_TYPE_FIELD_ID -> {
                    val authType = payload.short
                    println("AUTH_TYPE: $authType")
                }
                else -> {
                    val unknown = ByteArray(fieldSize.toInt())
                    payload.get(unknown)
                    println("UNKNOWN_ID: $fieldId")
                    println("UNKNOWN_CONTENT: ${
                        ByteUtils.byteArrayToHexString(
                            unknown,
                            separator = " "
                        )
                    }")
                }
            }
        }
        return config
    }


    fun specialTest(payload: ByteArray): String{
        val buffer = ByteBuffer.wrap(payload)
        var result: String = ""
        while (buffer.hasRemaining()) {
            val fieldId = buffer.short
            val fieldSize = buffer.short
            if (CREDENTIAL_FIELD_ID == fieldId) {
                result += parseCredential(buffer, fieldSize)
            } else {
                buffer.position(buffer.position() + fieldSize)
            }
        }
        return result
    }

    private fun specialTestLow(payload: ByteBuffer, size: Short): String {


        return ""

        var ssidStr = ""
        var authTypeStr = ""
        var encTypeStr = ""
        var passwdStr = ""

        val startPos = payload.position()
        while (payload.position() < startPos + size) {
            val fieldId = payload.short
            val fieldSize = payload.short

            if (payload.position() + fieldSize > startPos + size) {
                return ""
            }

            when (fieldId) {
                SSID_FIELD_ID -> {
                    val ssid = ByteArray(fieldSize.toInt())
                    payload.get(ssid)
                    ssidStr = String(ssid)
                    println("SSID: ${ByteUtils.byteArrayToHexString(ssid, separator = " ")}")
                    println("SSID: ${String(ssid)}")
                }
                NETWORK_KEY_FIELD -> {
                    val key = ByteArray(fieldSize.toInt())
                    payload.get(key)
                    passwdStr = String(key)
                    println("KEY: ${ByteUtils.byteArrayToHexString(key, separator = " ")}")
                    println("KEY: ${String(key)}")
                }
                AUTH_TYPE_FIELD_ID -> {
                    val authType = payload.short
                    println("AUTH_TYPE: $authType")
                }
                else -> {
                    val unknown = ByteArray(fieldSize.toInt())
                    payload.get(unknown)
                    println("UNKNOWN_ID: $fieldId")
                    println("UNKNOWN_CONTENT: ${
                        ByteUtils.byteArrayToHexString(
                            unknown,
                            separator = " "
                        )
                    }")
                }
            }
        }
    }

    fun getAuthTypeStr(type: Short): String {
        return when (type) {
            AUTH_TYPE_OPEN -> "打开"
            AUTH_TYPE_WPA_EAP -> "WPA-Enterprise"
            AUTH_TYPE_WPA_PSK -> "WPA-Personal"

            else -> ""
        }
    }


    const val NFC_TOKEN_MIME_TYPE = "application/vnd.wfa.wsc"
    const val CREDENTIAL_FIELD_ID: Short = 0x100e
    const val SSID_FIELD_ID: Short = 0x1045
    const val NETWORK_KEY_FIELD: Short = 0x1027

    const val AUTH_TYPE_FIELD_ID: Short = 0x1003
    const val AUTH_TYPE_EXPECTED_SIZE: Short = 2
    const val AUTH_TYPE_OPEN: Short = 0x0001
    const val AUTH_TYPE_WPA_PSK: Short = 0x0002
    const val AUTH_TYPE_WPA_EAP: Short = 0x0008
    const val AUTH_TYPE_WPA2_EAP: Short = 0x0010
    const val AUTH_TYPE_WPA2_PSK: Short = 0x0020

    const val ENC_TYPE_FIELD_ID: Short = 0x100f
    const val ENC_TYPE_NONE: Short = 0x0001
    const val ENC_TYPE_WEP: Short = 0x0002 // deprecated
    const val ENC_TYPE_TKIP: Short = 0x0004 // deprecated -> only with mixed mode (0x000c)
    const val ENC_TYPE_AES: Short = 0x0008 // includes CCMP and GCMP
    const val ENC_TYPE_AES_TKIP: Short = 0x000c // mixed mode

    // 不知道干啥用的
    const val NETWORK_INDEX_FIELD_ID: Short = 0x1026
    const val NETWORK_INDEX_DEFAULT_VALUE: Byte = 0x01;

}


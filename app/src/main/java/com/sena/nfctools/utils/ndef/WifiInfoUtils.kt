package com.sena.nfctools.utils.ndef

import android.nfc.NdefMessage
import android.nfc.NdefRecord
import com.sena.nfctools.bean.DataKey
import com.sena.nfctools.bean.WriteData
import com.sena.nfctools.other.Nfc_Wifi_Test
import com.sena.nfctools.utils.ByteUtils
import java.nio.ByteBuffer


/**
 * FileName: WifiInfoUtils
 * Author: JiaoCan
 * Date: 2023/4/18 15:41
 */

object WifiInfoUtils {

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

    fun createWifiRecord(data: WriteData): NdefRecord? {
        val ssid = data.data[DataKey.KEY_WIFI_SSID] ?: return null
        val passwd = data.data[DataKey.KEY_WIFI_PASSWD] ?: return null
        val authType: Short = data.data[DataKey.KEY_WIFI_AUTH_TYPE]?.toShort() ?: AUTH_TYPE_OPEN
        val encType: Short = data.data[DataKey.KEY_WIFI_ENC_TYPE]?.toShort() ?: ENC_TYPE_NONE

        return NdefRecord.createMime(
            NFC_TOKEN_MIME_TYPE, generatePayload(ssid, passwd, authType, encType)
        )
    }

    private fun generatePayload(ssid: String, key: String, authType: Short, ecyType: Short): ByteArray {

        val ssidSize = ssid.toByteArray().size.toShort()
        val passwdSize = key.toByteArray().size.toShort()

        val bufferSize = 24 + ssidSize + passwdSize
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

}


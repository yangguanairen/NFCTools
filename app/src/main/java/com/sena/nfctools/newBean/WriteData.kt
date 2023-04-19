package com.sena.nfctools.bean

import android.nfc.NdefRecord
import android.os.Build
import com.sena.nfctools.NfcApplication
import com.sena.nfctools.utils.ndef.WifiInfoUtils


/**
 * FileName: WriteData
 * Author: JiaoCan
 * Date: 2023/4/7 16:13
 */

data class WriteData(
    var opt: OptType = OptType.UNKNOWN,
    var data: Map<String, String> = mapOf()
) {
    fun build(): NdefRecord? {
        return when (opt) {
            OptType.TEXT -> createTextRecord()
            OptType.APPLICATION -> createAppRecord()
            OptType.URL -> createUrlRecord()
            OptType.WIFI -> WifiInfoUtils.createWifiRecord(this)
            else -> null
        }
    }

    private fun createTextRecord(): NdefRecord {
        val text = data[DataKey.KEY_TEXT] ?: ""
        val context = NfcApplication.getContext()
        val code = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            context?.resources?.configuration?.locales?.get(0)?.language
        } else {
            context?.resources?.configuration?.locale?.language
        } ?: "en"
        return NdefRecord.createTextRecord(code, text)
    }

    private fun createAppRecord(): NdefRecord {
        val appName = data[DataKey.KEY_APP] ?: ""
        return NdefRecord.createApplicationRecord(appName)
    }

    private fun createUrlRecord(): NdefRecord {
        val url = data[DataKey.KEY_URL] ?: ""
        return NdefRecord.createUri(url)
    }



}

enum class OptType {
    TEXT,
    URL,
    WIFI,
    APPLICATION,
    FORMAT,
    COPY,
    SAL,
    UNKNOWN
}

object DataKey {
    const val KEY_TEXT = "key_text"
    const val KEY_APP = "key_app"
    const val KEY_URL = "key_url"
    const val KEY_WIFI_SSID = "key_wifi_ssid"
    const val KEY_WIFI_PASSWD = "key_wifi_passwd"
    const val KEY_WIFI_AUTH_TYPE = "key_wifi_auth_type"
    const val KEY_WIFI_ENC_TYPE = "KEY_ENC_TYPE"
}


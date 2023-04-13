package com.sena.nfctools.bean

import android.nfc.tech.MifareUltralight


/**
 * FileName: NfcAData
 * Author: JiaoCan
 * Date: 2023/4/13 13:38
 */

data class NfcAData(
    var atqa: ByteArray = ByteArray(0),
    var sak: Short = -1,

    var m1ClassicData: M1ClassicData? = null,
    var m1UltraData: M1UltralightData? = null
)


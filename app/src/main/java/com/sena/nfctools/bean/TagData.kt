package com.sena.nfctools.bean

import android.nfc.tech.TagTechnology


/**
 * FileName: BaseNfc
 * Author: JiaoCan
 * Date: 2023/4/13 11:46
 */

class TagData(id: ByteArray, techList: Array<String>) {

    val mId: ByteArray
    val mTechList: Array<String>
    var type: Int = -1

    var ndefData: NdefData? = null

    var nfcAData: NfcAData? = null

    init {
        mId = id
        mTechList = techList
        mTechList.forEach {
            if ("android.nfc.tech.NfcA" == it) {
                type = TagTechnology.NFC_A
            } else if (it.lowercase().contains("nfcb")) {
                type = TagTechnology.NFC_B
            } else if (it.lowercase().contains("nfcv")) {
                type = TagTechnology.NFC_V
            } else if (it.lowercase().contains("nfcf")) {
                type = TagTechnology.NFC_F
            }
        }
    }


}


package com.sena.nfctools.bean


/**
 * FileName: BaseCard
 * Author: JiaoCan
 * Date: 2023/3/28 13:38
 */

open class BaseCard(
    bId: ByteArray,
    bType: String
) {

    val cardId: ByteArray = bId
    val type: String = bType

}


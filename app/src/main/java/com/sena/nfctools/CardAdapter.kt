package com.sena.nfctools

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.sena.nfctools.bean.BaseCard
import com.sena.nfctools.bean.M1Card
import com.sena.nfctools.utils.ByteUtils


/**
 * FileName: CardAdapter
 * Author: JiaoCan
 * Date: 2023/3/28 18:18
 */

class CardAdapter : BaseQuickAdapter<BaseCard, BaseViewHolder>(R.layout.item_card) {

    override fun convert(holder: BaseViewHolder, item: BaseCard) {
        var name = "Unknown"
        var id = "Unknown"

        val type = item.type
        if ("M1" == type && item is M1Card) {
            name = ByteUtils.byteArrayToHexString(item.id, separator = ":")
        }
        holder.setText(R.id.name, name)
        holder.setText(R.id.id, id)
    }


}


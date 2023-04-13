package com.sena.nfctools

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.sena.nfctools.bean.TagData
import com.sena.nfctools.utils.ByteUtils


/**
 * FileName: CardAdapter
 * Author: JiaoCan
 * Date: 2023/3/28 18:18
 */

class CardAdapter : BaseQuickAdapter<TagData, BaseViewHolder>(R.layout.item_card) {

    override fun convert(holder: BaseViewHolder, item: TagData) {
        var name = "Unknown"
        var id = "Unknown"

        name = ByteUtils.byteArrayToHexString(item.mId, separator = ":")
        holder.setText(R.id.name, name)
        holder.setText(R.id.id, id)
    }


}


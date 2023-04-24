package com.sena.nfctools

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.sena.nfctools.newBean.BaseCard


/**
 * FileName: CardAdapter
 * Author: JiaoCan
 * Date: 2023/3/28 18:18
 */

class CardAdapter : BaseQuickAdapter<Pair<String, String>, BaseViewHolder>(R.layout.item_card) {

    override fun convert(holder: BaseViewHolder, item: Pair<String, String>) {

        holder.setText(R.id.name, item.second)
        holder.setText(R.id.id, item.first)
    }


}


package com.sena.nfctools.widget

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.sena.nfctools.R


/**
 * FileName: CardDetailAdapter
 * Author: JiaoCan
 * Date: 2023/4/24 10:40
 */

class CardDetailAdapter() : BaseQuickAdapter<Pair<String, String>, BaseViewHolder>(R.layout.item_card_detail) {


    override fun convert(holder: BaseViewHolder, item: Pair<String, String>) {
        holder.setText(R.id.title, item.first)
        holder.setText(R.id.content, item.second)
    }


}


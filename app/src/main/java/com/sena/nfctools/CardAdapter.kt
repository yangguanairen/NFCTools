package com.sena.nfctools

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.sena.nfctools.newBean.BaseCard


/**
 * FileName: CardAdapter
 * Author: JiaoCan
 * Date: 2023/3/28 18:18
 */

class CardAdapter : BaseQuickAdapter<BaseCard, BaseViewHolder>(R.layout.item_card) {

    override fun convert(holder: BaseViewHolder, item: BaseCard) {

        holder.setText(R.id.name, item.name)
        holder.setText(R.id.id, item.getId())
    }


}


package com.sena.nfctools

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sena.nfctools.newBean.BaseCard
import com.sena.nfctools.utils.TestFile
import java.lang.Exception


/**
 * FileName: CardViewModel
 * Author: JiaoCan
 * Date: 2023/3/29 17:37
 */

class CardViewModel : ViewModel() {


    private val cardList = mutableListOf<BaseCard>()

    val isChange: MutableLiveData<Boolean> = MutableLiveData(false)

    fun init(context: Context) {
        try {
            // TODO: 禁止主线程调用
            val list = TestFile.getAllCards(context)
            cardList.addAll(list)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        isChange.postValue(true)
    }

    fun put(context: Context, card: BaseCard) {
        val iterator = cardList.iterator()
        while (iterator.hasNext()) {
            val item = iterator.next()
            if (card.getId() == item.getId()) {
                iterator.remove()
            }
        }
        cardList.add(card)
        TestFile.addNewCard(context, card)
        isChange.postValue(true)
    }

    fun remove(card: BaseCard) {
        val iterator = cardList.iterator()
        while (iterator.hasNext()) {
            val item = iterator.next()
            if (card.getId() == item.getId()) {
                iterator.remove()
            }
        }
        isChange.postValue(true)
    }

    fun getCardList(): List<BaseCard> {
        return cardList
    }

    override fun onCleared() {
        super.onCleared()
        cardList.clear()
    }
}


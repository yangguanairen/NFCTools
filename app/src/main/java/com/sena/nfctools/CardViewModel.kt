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


// 拒接在内存中保存大量card信息，byteArray有时候会过多

class CardViewModel : ViewModel() {


    private val cardList = mutableListOf<Pair<String, String>>()

    val isChange: MutableLiveData<Boolean> = MutableLiveData(false)

    fun init(context: Context) {
        try {
            // TODO: 禁止主线程调用
//            val list = TestFile.getAllCards(context)
            val list = TestFile.getAllFiles(context)
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
            if (card.getId() == item.first) {
                iterator.remove()
            }
        }
        cardList.add(Pair(card.getId(), card.name))
        TestFile.addNewCard(context, card)
        isChange.postValue(true)
    }

    fun remove(context: Context, id: String) {
        val iterator = cardList.iterator()
        while (iterator.hasNext()) {
            val item = iterator.next()
            if (id == item.first) {
                iterator.remove()
                TestFile.deleteCard(context, id)
//                TestFile.
            }
        }
        isChange.postValue(true)
    }

    fun getCardList(): List<Pair<String, String>> {
        return cardList
    }

    override fun onCleared() {
        super.onCleared()
        cardList.clear()
    }
}


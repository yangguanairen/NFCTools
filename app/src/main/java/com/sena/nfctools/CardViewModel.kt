package com.sena.nfctools

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.sena.nfctools.bean.BaseCard
import com.sena.nfctools.bean.M1Card
import com.sena.nfctools.utils.DataStoreUtils
import com.sena.nfctools.utils.DataStoreUtils.dataStore
import org.json.JSONObject
import java.lang.Exception
import java.security.interfaces.DSAKey


/**
 * FileName: CardViewModel
 * Author: JiaoCan
 * Date: 2023/3/29 17:37
 */

class CardViewModel : ViewModel() {


    private val cardList: MutableMap<ByteArray, BaseCard> = mutableMapOf()

    val isChange: MutableLiveData<Boolean> = MutableLiveData(false)

    fun init(context: Context) {
        val m1Gson = DataStoreUtils.get(context.dataStore, DataStoreUtils.key_M1, "")
        try {
            val m1List: List<M1Card> = Gson().fromJson(m1Gson, object : TypeToken<List<M1Card>>() {}.type)
            m1List.forEach {
                cardList[it.id] = it
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        isChange.postValue(true)
    }

    fun put(card: BaseCard) {
        val id = card.cardId
//        val isConflict = cardList.none { it.key.contentEquals(id) }
        cardList[id] = card
        isChange.postValue(true)
    }

    fun remove(card: BaseCard) {
        val id = card.cardId
        cardList.remove(id)
        isChange.postValue(true)
    }

    fun getCardList(): List<BaseCard> {
        return cardList.map {
            it.value
        }
    }

    override fun onCleared() {
        super.onCleared()
        cardList.clear()
    }
}


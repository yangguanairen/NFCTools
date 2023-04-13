package com.sena.nfctools

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.sena.nfctools.bean.TagData
import com.sena.nfctools.utils.ByteUtils
import com.sena.nfctools.utils.DataStoreUtils
import com.sena.nfctools.utils.DataStoreUtils.dataStore
import java.lang.Exception


/**
 * FileName: CardViewModel
 * Author: JiaoCan
 * Date: 2023/3/29 17:37
 */

class CardViewModel : ViewModel() {


    private val cardList: MutableMap<String, TagData> = mutableMapOf()

    val isChange: MutableLiveData<Boolean> = MutableLiveData(false)

    fun init(context: Context) {
        val m1Gson = DataStoreUtils.get(context.dataStore, DataStoreUtils.key_M1, "")
        try {
            val m1List: List<TagData> = Gson().fromJson(m1Gson, object : TypeToken<List<TagData>>() {}.type)
            m1List.forEach {
                cardList[ByteUtils.byteArrayToHexString(it.mId)] = it
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        isChange.postValue(true)
    }

    fun put(card: TagData) {
        val id = ByteUtils.byteArrayToHexString(card.mId)
//        val isConflict = cardList.none { it.key.contentEquals(id) }
        cardList[id] = card
        isChange.postValue(true)
    }

    fun remove(card: TagData) {
        val id = ByteUtils.byteArrayToHexString(card.mId)
        cardList.remove(id)
        isChange.postValue(true)
    }

    fun getCardList(): List<TagData> {
        return cardList.map {
            it.value
        }
    }

    override fun onCleared() {
        super.onCleared()
        cardList.clear()
    }
}


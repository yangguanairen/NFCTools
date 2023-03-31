package com.sena.nfctools.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking


/**
 * FileName: SpUtils
 * Author: JiaoCan
 * Date: 2023/3/28 16:15
 *
 * https://developer.aliyun.com/article/1050438
 * https://developer.android.google.cn/topic/libraries/architecture/datastore?hl=zh-cn#kotlin
 */

object DataStoreUtils {

    val Context.dataStore: DataStore<Preferences> by preferencesDataStore("data_store_card")

    val key_M1: Preferences.Key<String> by lazy {
        stringPreferencesKey("key_m1")
    }

    suspend fun <T> put(dataStore: DataStore<Preferences>, key: Preferences.Key<T>, value: T) {
        dataStore.edit {
            it[key] = value
        }
    }

    fun <T> getAll(dataStore: DataStore<Preferences>, key: Preferences.Key<T>, defValue: T): List<T> = runBlocking {
        return@runBlocking arrayListOf<T>().apply {
            val map = dataStore.data.map {
                it[key] ?: defValue
            }.map {
                this.add(it)
            }
        }
    }

    fun <T> get(dataStore: DataStore<Preferences>, key: Preferences.Key<T>, defValue: T): T = runBlocking {
        dataStore.data.map {
            it[key] ?: defValue
        }.first()
    }
}


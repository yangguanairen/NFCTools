package com.sena.nfctools.utils

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.sena.nfctools.newBean.*
import java.io.File


/**
 * FileName: TestFile
 * Author: JiaoCan
 * Date: 2023/4/14 16:11
 */

// 标准的file
// id-cardType-customName

object CardFileUtils {


    fun addNewCard(context: Context, card: BaseCard) {
        val id = card.getId()

        val cardsDir = File(context.externalCacheDir, "cards")
        if (!cardsDir.exists()) cardsDir.mkdirs()


        cardsDir.listFiles()?.filter {
            it.isFile && !it.name.startsWith(".")
        }?.filter {
            it.name.startsWith(id)
        }?.forEach {
            val t = it.delete()
            println("删除了一个文件$t")
        }

        val newFile = File(cardsDir, "$id-${card.getCardType()}-新卡片")
//        if (newFile.exists()) {
//            val result = newFile.delete()
//            println("删除结果: $result")
//        }
//        if (!newFile.exists()) newFile.mkdir()

        val gson = Gson().toJson(card)
        newFile.writeText(gson)
    }

    fun deleteCard(context: Context, id: String): Boolean {
        val cardsDir = File(context.externalCacheDir, "cards")
        if (!cardsDir.exists()) return false

        var deleteCount = 0
        val list = cardsDir.listFiles()?.filter {
            it.isFile && !it.name.startsWith(".")
        }?.forEach {
            if (it.name.startsWith(id)) {
                it.delete()
                println("删除文件${it.name}")
                deleteCount++
            }
        }
        println("总共删除$deleteCount 个文件")
        return deleteCount > 0
    }

    fun getAllCards(context: Context): List<BaseCard> {
        val result = arrayListOf<BaseCard>()
        val cardsDir = File(context.externalCacheDir, "cards")
        cardsDir.listFiles()?.filter {
            it.isFile && !it.name.startsWith(".")
        }?.forEach {
            val t = it.name.split("-")
            val id = t[0]
            val name = t[1]
            deserialization(it.readText(), name)?.let { card ->
                result.add(card)
            }
        }
        return result
    }

    fun getAllFiles(context: Context): List<Pair<String, String>> {
        val result = arrayListOf<Pair<String, String>>()
        val cardsDir = File(context.externalCacheDir, "cards")
        cardsDir.listFiles()?.filter {
            it.isFile && !it.name.startsWith(".")
        }?.forEach {
            val t = it.name.split("-")
            if (t.size == 3) {
                val id = t[0]
                val cardType = t[1]
                val customName = t[2]
                result.add(Pair(id, cardType))
            }
        }
        return result
    }

    fun getCardById(context: Context, id: String): BaseCard? {
        val cardDir = File(context.externalCacheDir, "cards")
        val result = cardDir.listFiles()?.filter {
            it.isFile && it.name.startsWith(id)
        } ?: emptyList()
        if (result.isEmpty()) {
            return null
        } else {
            val file = result[0]
            val type = file.name.split("-")[1]
            val card = deserialization(file.readText(), type)
            return card
        }
    }

    private fun deserialization(text: String, type: String): BaseCard? {
        try {
            val card = when (type) {
                CARD_TYPE_M1 -> {
                    Gson().fromJson(text, TypeToken.get(M1Card::class.java))
                }
                CARD_TYPE_M0 -> {
                    Gson().fromJson(text, TypeToken.get(M0Card::class.java))
                }
                CARD_TYPE_NFCV -> {
                    Gson().fromJson(text, TypeToken.get(NfcVCard::class.java))
                }
                else -> {
                    null
                }
            }
            return card
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

}


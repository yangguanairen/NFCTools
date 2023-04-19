package com.sena.nfctools.utils

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.sena.nfctools.newBean.BaseCard
import com.sena.nfctools.newBean.M1Card
import com.sena.nfctools.newBean.Ntag21xCard
import java.io.File


/**
 * FileName: TestFile
 * Author: JiaoCan
 * Date: 2023/4/14 16:11
 */

object TestFile {


    fun addNewCard(context: Context, card: BaseCard) {
        val name = card.name
        val id = card.getId()

        val cardsDir = File(context.externalCacheDir, "cards")
        if (!cardsDir.exists()) cardsDir.mkdirs()


        val files = cardsDir.listFiles()?.filter {
            it.isFile && !it.name.startsWith(".")
        }?.filter {
            it.name.startsWith(name)
        }?.forEach {
            val t = it.delete()
            println("删除了一个文件$t")
        }

        val newFile = File(cardsDir, "$id-$name")
        if (newFile.exists()) {
            val result = newFile.delete()
            println("删除结果: $result")
        }
//        if (!newFile.exists()) newFile.mkdir()

        val gson = Gson().toJson(card)
        newFile.writeText(gson)
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
            if ("M1" == name) {
                val m1Card = Gson().fromJson<M1Card>(it.readText(), TypeToken.get(M1Card::class.java))
                result.add(m1Card)
            } else if ("NTAG215" == name || "NTAG213" == name || "NTAG216" == name) {
                val ntag21xCard = Gson().fromJson(it.readText(), TypeToken.get(Ntag21xCard::class.java))
                result.add(ntag21xCard)
            }
        }

        return result
    }

}


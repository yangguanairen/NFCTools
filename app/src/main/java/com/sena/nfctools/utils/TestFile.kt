package com.sena.nfctools.utils

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.sena.nfctools.newBean.BaseCard
import com.sena.nfctools.newBean.M1Card
import com.sena.nfctools.newBean.Ntag215Card
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

        val cardsDir = File(context.cacheDir, "cards")


        val files = cardsDir.listFiles()?.filter {
            it.isFile && !it.name.startsWith(".")
        }?.filter {
            it.name.startsWith(name)
        }?.forEach {
            it.delete()
        }

        val newFile = File(cardsDir, "$id-$name")
        if (newFile.exists()) newFile.deleteOnExit()

        val gson = Gson().toJson(card)
        newFile.writeText(gson)
    }

    fun getAllCards(context: Context): List<BaseCard> {
        val result = arrayListOf<BaseCard>()
        val cardsDir = File(context.cacheDir, "cards")
        cardsDir.listFiles()?.filter {
            it.isFile && !it.name.startsWith(".")
        }?.forEach {
            val t = it.name.split("-")
            val id = t[0]
            val name = t[1]
            if ("M1" == name) {
                val m1Card = Gson().fromJson<M1Card>(it.readText(), TypeToken.get(M1Card::class.java))
                result.add(m1Card)
            } else if ("NTAG215" == name) {
                val ntag215Card = Gson().fromJson(it.readText(), TypeToken.get(Ntag215Card::class.java))
                result.add(ntag215Card)
            }
        }

        return result
    }

}


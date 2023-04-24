package com.sena.nfctools

import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        val ya = arrayListOf(50, 34)
        val fei = arrayListOf(11, 13, 15, 17, 19, 21, 24, 28, 32)

        ya.forEach { y ->
            fei.forEach { f ->
                println("牙盘: $y, 飞轮: $f, 齿比: ${y.toFloat() / f}")
            }
        }
    }
}
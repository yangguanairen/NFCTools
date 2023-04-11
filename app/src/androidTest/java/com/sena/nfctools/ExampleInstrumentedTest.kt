package com.sena.nfctools

import android.nfc.NdefMessage
import android.nfc.NdefRecord
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.sena.nfctools.utils.ByteUtils

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.sena.nfctools", appContext.packageName)
    }

    @Test
    fun testMessage() {
        val appRecord = NdefRecord.createApplicationRecord("bin.mt.plus")
        val textRecord = NdefRecord.createTextRecord("en", "aaaa");

        val message1 = NdefMessage(appRecord)
        println("单独App: ${ByteUtils.byteArrayToHexString(message1.toByteArray(), separator = ":")}")
        val message2 = NdefMessage(textRecord)
        println("单独Text: ${ByteUtils.byteArrayToHexString(message2.toByteArray(), separator = ":")}")
        val message3 = NdefMessage(arrayOf(appRecord, textRecord))
        println("App->Text: ${ByteUtils.byteArrayToHexString(message3.toByteArray(), separator = ":")}")
        val message4 = NdefMessage(arrayOf(textRecord, appRecord))
        println("Text->App: ${ByteUtils.byteArrayToHexString(message4.toByteArray(), separator = ":")}")
    }
}
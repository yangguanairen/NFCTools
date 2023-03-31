package com.sena.nfctools.widget

import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.nfc.NfcAdapter


/**
 * FileName: BaseNfcActivity
 * Author: JiaoCan
 * Date: 2023/3/31 17:25
 */

open class BaseNfcActivity : BaseActivity() {

    private val mNfcAdapter: NfcAdapter by lazy {
        NfcAdapter.getDefaultAdapter(this)
    }

    override fun onResume() {
        super.onResume()

        // Q: intent.action == null
        // A: https://blog.csdn.net/queal/article/details/126379781
        // FLAG_IMMUTABLE 表示intent不能除发送端以外的其他应用修改
        val pendingIntent = PendingIntent.getActivity(this, 0,
            Intent(this, javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), PendingIntent.FLAG_MUTABLE)
        val filters = arrayOf(
            IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED),
            IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED),
            IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED)
        )
        mNfcAdapter.enableForegroundDispatch(this, pendingIntent, filters, null)
    }

    override fun onPause() {
        super.onPause()
        mNfcAdapter.disableForegroundDispatch(this)

    }
}


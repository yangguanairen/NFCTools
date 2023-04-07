package com.sena.nfctools

import android.app.Application
import android.content.Context
import java.lang.ref.SoftReference


/**
 * FileName: Application
 * Author: JiaoCan
 * Date: 2023/4/7 10:06
 */

class NfcApplication : Application() {

    companion object {
        private var mContext: SoftReference<Context>? = null

        fun getContext() = mContext?.get()
    }

    override fun onCreate() {
        super.onCreate()
        mContext = SoftReference(applicationContext)
    }


}


package com.sena.nfctools.widget.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity


/**
 * FileName: BaseFragment
 * Author: JiaoCan
 * Date: 2023/3/28 17:32
 */

open class BaseFragment : Fragment() {

    protected lateinit var mContext: Context
    protected lateinit var mOwner: FragmentActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context?.let {
            mContext = it
            mOwner = mContext as FragmentActivity
        }
    }

    open fun handleIntent(intent: Intent) {

    }
}


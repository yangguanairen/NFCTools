package com.sena.nfctools.widget.fragment

import android.content.Intent
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import com.sena.nfctools.CardViewModel
import com.sena.nfctools.databinding.FragmentReadBinding
import com.sena.nfctools.utils.ByteUtils
import com.sena.nfctools.utils.DataStoreUtils
import com.sena.nfctools.utils.DataStoreUtils.dataStore
import com.sena.nfctools.utils.M1CardUtils
import com.sena.nfctools.utils.NfcUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class ReadFragment : BaseFragment() {

    private lateinit var binding: FragmentReadBinding

    private lateinit var vm: CardViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentReadBinding.inflate(layoutInflater, container, false)

        vm = ViewModelProvider(mOwner)[CardViewModel::class.java]

        return binding.root
    }

    private fun initView() {

    }

    override fun handleIntent(intent: Intent) {
        super.handleIntent(intent)

        val tag = intent.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG)
        if (tag == null) {
            println("tag is null")
            return
        }

        lifecycleScope.launch {
            val result = withContext(Dispatchers.IO) {
                context?.let { mContext ->
                    val m1Card = M1CardUtils.readM1Card(tag)
                    if (m1Card == null) {
                        println("解析失败")
                        return@let null
                    }
                    vm.put(m1Card)
                    println(Gson().toJson(vm.getCardList()))
                    DataStoreUtils.put(mContext.dataStore, DataStoreUtils.key_M1, Gson().toJson(vm.getCardList()))
                    ByteUtils.byteArrayToHexString(m1Card.id)
                }
            }
            Toast.makeText(mContext, "录入完成 $result", Toast.LENGTH_SHORT).show()
        }

    }


}
package com.sena.nfctools.widget.fragment

import android.content.Context
import android.content.Intent
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.sena.nfctools.databinding.FragmentWriteBinding
import com.sena.nfctools.utils.M1CardUtils
import com.sena.nfctools.utils.Nfc_Wifi_Test
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WriteFragment : BaseFragment() {

    private lateinit var binding: FragmentWriteBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        binding = FragmentWriteBinding.inflate(layoutInflater, container, false)
        initView()
        return binding.root
    }

    private fun initView() {
        binding.writeTest1.setOnClickListener {
        }
    }

    override fun handleIntent(intent: Intent) {
        super.handleIntent(intent)
        val tag = intent.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG)
        if (tag == null) {
            println("tag is null")
            return
        }
        lifecycleScope.launch(Dispatchers.IO) {
//            M1CardUtils.testWriteWifi(tag, mContext)
            Nfc_Wifi_Test.write(tag)
        }
    }

}
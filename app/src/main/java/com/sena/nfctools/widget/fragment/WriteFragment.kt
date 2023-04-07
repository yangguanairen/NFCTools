package com.sena.nfctools.widget.fragment

import android.content.Intent
import android.net.wifi.WifiConfiguration
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.core.BasePopupView
import com.sena.nfctools.databinding.FragmentWriteBinding
import com.sena.nfctools.utils.NdefUtils
import com.sena.nfctools.widget.popup.TipPopup
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class WriteFragment : BaseFragment() {

    private lateinit var binding: FragmentWriteBinding

    private var canHandle: Boolean = false
    private lateinit var loadingPopup: BasePopupView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        binding = FragmentWriteBinding.inflate(layoutInflater, container, false)
        initView()
        return binding.root
    }

    private var opt = ""
    private var data = ""

    private fun initView() {
        val tipPopup = TipPopup.Builder(mContext)
            .onCancel {
                canHandle = false
            }.onDismiss {
                canHandle = false
            }.onShow {
                canHandle = true
            }
            .build()
        loadingPopup = XPopup.Builder(mContext)
            .hasShadowBg(false)
            .asCustom(tipPopup)


        binding.writeText.setOnClickListener {
            opt = "Text"
            data = "Hello World!"
            loadingPopup.show()
        }
        binding.writeApp.setOnClickListener {
            opt = "App"
            data = "bin.mt.plus"
            loadingPopup.show()
        }

    }

    override fun handleIntent(intent: Intent) {
        super.handleIntent(intent)
        if (!canHandle) {
            println("暂且无事件需要处理tag")
            return
        }
        val tag = intent.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG)
        if (tag == null) {
            println("tag is null")
            return
        }
        lifecycleScope.launch {
//            M1CardUtils.testWriteWifi(tag, mContext)
//            Nfc_Wifi_Test.write(tag)
            val isSuccess = withContext(Dispatchers.IO) {
                NdefUtils.write(tag, opt, data)
            }

            canHandle = false
            loadingPopup.dismiss()
            if (isSuccess) {
                println("写入成功")
            } else {
                println("写入失败")
            }
        }
    }

}
package com.sena.nfctools.widget.fragment

import android.content.Intent
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.core.BasePopupView
import com.sena.nfctools.bean.OptType
import com.sena.nfctools.bean.WriteData
import com.sena.nfctools.databinding.FragmentOtherBinding
import com.sena.nfctools.utils.NdefUtils
import com.sena.nfctools.widget.popup.TipPopup
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class OtherFragment : BaseFragment() {

    private lateinit var binding: FragmentOtherBinding

    private var canHandle = false
    private lateinit var loadingPopup: BasePopupView

    private var curOpt: OptType = OptType.UNKNOWN

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        binding = FragmentOtherBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    private fun initView() {
        val tipPopup = TipPopup.Builder(mContext)
            .onShow {
                canHandle = true
            }.onDismiss {
                canHandle = false
            }.onCancel {
                canHandle = false
            }.build()
        loadingPopup = XPopup.Builder(mContext)
            .asCustom(tipPopup)

        binding.format.setOnClickListener {
            curOpt = OptType.FORMAT
            loadingPopup.show()
        }
    }

    override fun handleIntent(intent: Intent) {
        super.handleIntent(intent)

        if (!canHandle) {
            println("暂无可以处理tag的事件")
            return
        }

        val tag = intent.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG)
        if (tag == null) {
            println("tag is null")
            return
        }

        lifecycleScope.launch {
            val result = withContext(Dispatchers.IO) {
                when (curOpt) {
                    OptType.FORMAT -> {
                        NdefUtils.format(tag)
                    }
                    else -> {

                    }
                }
                NdefUtils.format(tag)
            }
            val message = if (result) "格式化成功" else "格式化失败"
            Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show()
        }
    }
}
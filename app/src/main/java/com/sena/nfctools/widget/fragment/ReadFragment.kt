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
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.core.BasePopupView
import com.sena.nfctools.CardViewModel
import com.sena.nfctools.databinding.FragmentReadBinding
import com.sena.nfctools.newBean.BaseCard
import com.sena.nfctools.utils.NfcUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class ReadFragment : BaseFragment() {

    private lateinit var binding: FragmentReadBinding

    private lateinit var vm: CardViewModel

    private lateinit var readingPopup: BasePopupView
    private lateinit var confirmPopup: BasePopupView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentReadBinding.inflate(layoutInflater, container, false)

        vm = ViewModelProvider(mOwner)[CardViewModel::class.java]
        initView()
        return binding.root
    }

    private fun initView() {
        readingPopup = XPopup.Builder(mContext)
            .asLoading("正在复制中")

    }

    override fun handleIntent(intent: Intent) {
        super.handleIntent(intent)

        val tag = intent.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG)
        if (tag == null) {
            println("tag is null")
            return
        }

        lifecycleScope.launch {
            readingPopup.show()

            val result: BaseCard? = withContext(Dispatchers.IO) {
                NfcUtils.read(tag)
            }
            if (result == null) {
                Toast.makeText(mContext, "读取失败!!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(mContext, "读取成功!!", Toast.LENGTH_SHORT).show()

                confirmPopup = XPopup.Builder(mContext)
                    .asConfirm("读取到卡片$result, 是否要添加？", "") {
                        vm.put(mContext, result)
                        confirmPopup.dismiss()
                    }.show()
            }

            readingPopup.dismiss()

        }

    }


}
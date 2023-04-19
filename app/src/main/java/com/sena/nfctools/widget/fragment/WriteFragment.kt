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
import com.sena.nfctools.bean.DataKey
import com.sena.nfctools.bean.OptType
import com.sena.nfctools.bean.WriteData
import com.sena.nfctools.databinding.FragmentWriteBinding
import com.sena.nfctools.utils.NdefUtils
import com.sena.nfctools.utils.NfcUtils
import com.sena.nfctools.utils.ndef.WifiInfoUtils
import com.sena.nfctools.widget.popup.TipPopup
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class WriteFragment : BaseFragment() {

    private lateinit var binding: FragmentWriteBinding

    private var canHandle: Boolean = false
    private lateinit var loadingPopup: BasePopupView

    private val dataList = mutableListOf<WriteData>()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        binding = FragmentWriteBinding.inflate(layoutInflater, container, false)
        initView()
        return binding.root
    }

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
            .hasShadowBg(true)
            .asCustom(tipPopup)


        binding.writeText.setOnClickListener {
            dataList.clear()
            dataList.add(WriteData(
                opt = OptType.TEXT,
                data = mapOf(DataKey.KEY_TEXT to "测试文本")
            ))
            loadingPopup.show()
        }
        binding.writeApp.setOnClickListener {
            dataList.clear()
            dataList.add(WriteData(
                opt = OptType.APPLICATION,
                data = mapOf(DataKey.KEY_APP to "bin.mt.plus")
            ))
            loadingPopup.show()
        }
        binding.writeUrl.setOnClickListener {
            dataList.clear()
            dataList.add(WriteData(
                opt = OptType.URL,
                data = mapOf(DataKey.KEY_URL to "https://www.baidu.com")
            ))
            loadingPopup.show()
        }
        binding.writeWifi.setOnClickListener {
            dataList.clear()
            dataList.add(
                WriteData(
                opt = OptType.WIFI,
                data = mapOf(
                    DataKey.KEY_WIFI_SSID to "Aitmed-ECOS",
                    DataKey.KEY_WIFI_PASSWD to "aitmed123",
                    DataKey.KEY_WIFI_AUTH_TYPE to WifiInfoUtils.AUTH_TYPE_WPA2_PSK.toString(),
                    DataKey.KEY_WIFI_ENC_TYPE to WifiInfoUtils.ENC_TYPE_NONE.toString()
                )
            )
            )
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
            val isSuccess = withContext(Dispatchers.IO) {
                NfcUtils.write(tag, dataList)
            }

            loadingPopup.dismiss()
            val message = if (isSuccess) "写入成功" else "写入失败"
            Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show()
        }
    }

}
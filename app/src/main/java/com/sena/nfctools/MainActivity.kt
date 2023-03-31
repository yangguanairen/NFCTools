package com.sena.nfctools

import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.nfc.NfcAdapter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.FragmentManager
import com.sena.nfctools.databinding.ActivityMainBinding
import com.sena.nfctools.widget.fragment.*


/**
 *Android NFC使用详解 https://blog.csdn.net/u011082160/article/details/89146192
 *Android与RFID的点点滴滴（二）RFID通讯协议 https://blog.csdn.net/qq_32368129/article/details/114312929
 *Android NFC功能 简单实现 https://www.jianshu.com/p/cf36c214f2a8
 *
 * Android NFC详解 https://blog.csdn.net/u013164293/article/details/124474247
 */

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var fm: FragmentManager

    private val mNfcAdapter: NfcAdapter by lazy {
        NfcAdapter.getDefaultAdapter(this)
    }

    private var currentFragment: BaseFragment? = null

    private val fragmentMap: Map<String, BaseFragment> by lazy {
        mapOf(
            "manager" to ManagerFragment(),
            "read" to ReadFragment(),
            "write" to WriteFragment(),
            "format" to FormatFragment()
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        fm = supportFragmentManager
        initView()
    }

    private fun initView() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_baseline_menu_24)
            title = "NFC Sample"
        }
        binding.toolbar.setNavigationOnClickListener {
            binding.drawerLayout.openDrawer(binding.navView)
        }
        binding.navView.setNavigationItemSelectedListener {
            currentFragment = when (it.itemId) {
                R.id.main_manager -> fragmentMap["manager"]
                R.id.main_read -> fragmentMap["read"]
                R.id.main_write -> fragmentMap["write"]
                R.id.main_format -> fragmentMap["format"]
                else -> null
            }
            currentFragment?.let { f ->
                fm.beginTransaction().replace(binding.content.id, f).commit()
            }
            binding.drawerLayout.closeDrawer(binding.navView)
            true
        }

        currentFragment = fragmentMap["manager"]
        fm.beginTransaction().replace(binding.content.id, currentFragment!!)

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

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (intent == null) return
        currentFragment?.handleIntent(intent)
    }

    override fun onPause() {
        super.onPause()
        mNfcAdapter.disableForegroundDispatch(this)

    }


    override fun onDestroy() {
        super.onDestroy()
    }
}
package com.sena.nfctools.widget

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.fragment.app.FragmentManager
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.sena.nfctools.R
import com.sena.nfctools.databinding.ActivityMainBinding
import com.sena.nfctools.widget.fragment.*


/**
 *Android NFC使用详解 https://blog.csdn.net/u011082160/article/details/89146192
 *Android与RFID的点点滴滴（二）RFID通讯协议 https://blog.csdn.net/qq_32368129/article/details/114312929
 *Android NFC功能 简单实现 https://www.jianshu.com/p/cf36c214f2a8
 *
 * Android NFC详解 https://blog.csdn.net/u013164293/article/details/124474247
 */

class MainActivity : BaseNfcActivity() {

    companion object {
        const val NAME_MANAGER = "name_manager"
        const val NAME_READ = "name_read"
        const val NAME_WRITE = "name_write"
        const val NAME_FORMAT = "name_format"
        const val ACTION_DISPLAY_FRAGMENT = "action_display_fragment"
        const val KEY_FRAGMENT_NAME = "key_fragment_name"
    }

    private lateinit var binding: ActivityMainBinding

    private lateinit var fm: FragmentManager
    private lateinit var lbm: LocalBroadcastManager

    private val fragmentReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (ACTION_DISPLAY_FRAGMENT == intent?.action) {
                val name = intent.getStringExtra(KEY_FRAGMENT_NAME)
                if (name == null) {
                    return
                }
                val fragment = fragmentMap[name] ?: return
                fm.beginTransaction().replace(R.id.content, fragment).commit()
            }
        }

    }
    private var currentFragment: BaseFragment? = null

    private val fragmentMap: Map<String, BaseFragment> by lazy {
        mapOf(
            NAME_MANAGER to ManagerFragment(),
            NAME_READ to ReadFragment(),
            NAME_WRITE to WriteFragment(),
            NAME_FORMAT to OtherFragment()
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        fm = supportFragmentManager
        initView()
        initOther()
    }

    private fun initOther() {
        lbm = LocalBroadcastManager.getInstance(this)
        lbm.registerReceiver(fragmentReceiver, IntentFilter(ACTION_DISPLAY_FRAGMENT))
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
            currentFragment = fragmentMap[when(it.itemId) {
                R.id.main_manager -> NAME_MANAGER
                R.id.main_read -> NAME_READ
                R.id.main_write -> NAME_WRITE
                R.id.main_format -> NAME_FORMAT
                else -> ""
            }]
            currentFragment?.let { f ->
                fm.beginTransaction().replace(binding.content.id, f).commit()
            }
            binding.drawerLayout.closeDrawer(binding.navView)
            true
        }


        binding.navView.setCheckedItem(R.id.main_manager)
        currentFragment = fragmentMap[NAME_MANAGER]
        fm.beginTransaction().replace(binding.content.id, currentFragment!!).commit()

    }



    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (intent == null) return
        currentFragment?.handleIntent(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        lbm.unregisterReceiver(fragmentReceiver)
    }

}
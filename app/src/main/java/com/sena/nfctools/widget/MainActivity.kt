package com.sena.nfctools.widget

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.nfc.NfcAdapter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.FragmentManager
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

    private val fragmentReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (ACTION_DISPLAY_FRAGMENT == intent?.action) {
                val name = intent.getStringExtra(KEY_FRAGMENT_NAME)
                if (name == null) {

                }
                val fragment = fragmentMap[] ?: return
                fm.beginTransaction().replace(R.id.content, fragment).commit()
            }
        }

    }
    private var currentFragment: BaseFragment? = null

    private val fragmentMap: Map<Int, BaseFragment> by lazy {
        mapOf(
            R.id.main_manager to ManagerFragment(),
            R.id.main_read to ReadFragment(),
            R.id.main_write to WriteFragment(),
            R.id.main_format to FormatFragment()
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
            currentFragment = fragmentMap[it.itemId]
            currentFragment?.let { f ->
                fm.beginTransaction().replace(binding.content.id, f).commit()
            }
            binding.drawerLayout.closeDrawer(binding.navView)
            true
        }


        binding.navView.setCheckedItem(R.id.main_manager)
        currentFragment = fragmentMap[R.id.main_manager]
        fm.beginTransaction().replace(binding.content.id, currentFragment!!).commit()

    }



    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (intent == null) return
        currentFragment?.handleIntent(intent)
    }

}
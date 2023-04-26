package com.sena.nfctools.widget.cardViewer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.sena.nfctools.databinding.ActivityMemoryDetailBinding
import com.sena.nfctools.newBean.NfcVCard
import com.sena.nfctools.utils.ByteUtils
import com.sena.nfctools.utils.CardFileUtils
import com.sena.nfctools.widget.CardDetailAdapter

class MemoryDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMemoryDetailBinding

    private val list = arrayListOf<Pair<String, String>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMemoryDetailBinding.inflate(layoutInflater)
        val id = intent.getStringExtra("ID")
        if (id == null) {
            println("id is null")
            return
        }


        initData(id)
        initView()


        setContentView(binding.root)
    }

    private fun initData(id: String) {
        val card = CardFileUtils.getCardById(this, id) ?: return

//        if (card is NfcVCard) {
//            val data = card.icodeSlixData ?: return
//            data.blocks.forEach {
//                list.add(Pair(
//                    "Block ${it.index}",
//                    ByteUtils.byteArrayToHexString(it.data, separator = "")
//                ))
//            }
//        }
    }

    private fun initView() {
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = CardDetailAdapter()
        binding.recyclerView.adapter = adapter
        adapter.setList(list)
    }
}
package com.sena.nfctools.widget.cardViewer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.sena.nfctools.databinding.ActivityCardDetailBinding
import com.sena.nfctools.newBean.BaseCard
import com.sena.nfctools.utils.TestFile
import com.sena.nfctools.widget.CardDetailAdapter

class CardDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCardDetailBinding

    private lateinit var card: BaseCard

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCardDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val id = intent.getStringExtra("ID")
        if (id.isNullOrBlank()) {
            println("id is null")
            finish()
            return
        }
        val c = TestFile.getCardById(this,  id)
        if (c == null) {
            println("cannot find card $id in externCacheDir.cards")
            return
        }
        card = c

        initView()
    }

    private fun initView() {
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = CardDetailAdapter()
        binding.recyclerView.adapter = adapter

        val map = card.buildMap()
        map.toList()
        adapter.setList(map.toList())

    }


}
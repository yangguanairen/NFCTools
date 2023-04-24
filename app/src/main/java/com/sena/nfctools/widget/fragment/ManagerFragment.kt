package com.sena.nfctools.widget.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.interfaces.OnSelectListener
import com.sena.nfctools.CardAdapter
import com.sena.nfctools.CardViewModel
import com.sena.nfctools.databinding.FragmentManagerBinding
import com.sena.nfctools.newBean.BaseCard
import com.sena.nfctools.widget.cardViewer.CardDetailActivity
import com.sena.nfctools.widget.cardViewer.MemoryDetailActivity


class ManagerFragment : BaseFragment() {

    private var isFirst = true

    private lateinit var binding: FragmentManagerBinding
    private lateinit var mAdapter: CardAdapter

    private lateinit var vm: CardViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentManagerBinding.inflate(layoutInflater, container, false)

        vm = ViewModelProvider(mOwner)[CardViewModel::class.java]

        initView()

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        if (isFirst) {
            isFirst = false
            vm.init(mContext)
        }
    }

    private fun initView() {
        binding.recyclerView.layoutManager = LinearLayoutManager(mContext)
        mAdapter = CardAdapter()
        binding.recyclerView.adapter = mAdapter
        mAdapter.setOnItemClickListener { adapter, view, position ->
            val item = adapter.data[position] as Pair<String, String>
            val id = item.first


            XPopup.Builder(mContext)
                .asCenterList("", arrayOf("卡片信息", "内存详情")) { p, text ->
                    val intent = when (p) {
                        0 -> Intent(mContext, CardDetailActivity::class.java)
                        1 -> Intent(mContext, MemoryDetailActivity::class.java)
                        else -> return@asCenterList
                    }
                    intent.putExtra("ID", id)
                    mContext.startActivity(intent)
                }.show()
        }

        vm.isChange.observe(mOwner) {
            val list = vm.getCardList()
            mAdapter.setList(list)
        }
    }

//    override fun handleIntent(intent: Intent) {
//        super.handleIntent(intent)
//        val tag = intent.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG) ?: return
//        lifecycleScope.launch(Dispatchers.IO) {
//            val card = M1CardUtils.readM1Card(tag) ?: return@launch
//            vm.put(card)
//        }
//    }

    override fun onDestroyView() {
        super.onDestroyView()
        vm.isChange.removeObservers(mOwner)
    }

}
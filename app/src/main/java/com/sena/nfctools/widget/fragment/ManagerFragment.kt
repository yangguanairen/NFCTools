package com.sena.nfctools.widget.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.sena.nfctools.CardAdapter
import com.sena.nfctools.CardViewModel
import com.sena.nfctools.databinding.FragmentManagerBinding
import com.sena.nfctools.newBean.BaseCard


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
            val item = adapter.data[position] as BaseCard

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
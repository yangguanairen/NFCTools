package com.sena.nfctools.widget.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sena.nfctools.databinding.FragmentWriteBinding

class WriteFragment : BaseFragment() {

    private lateinit var binding: FragmentWriteBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        binding = FragmentWriteBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun handleIntent(intent: Intent) {
        super.handleIntent(intent)
    }

}
package com.sena.nfctools.widget.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sena.nfctools.databinding.FragmentFormatBinding

class FormatFragment : BaseFragment() {

    private lateinit var binding: FragmentFormatBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        binding = FragmentFormatBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun handleIntent(intent: Intent) {
        super.handleIntent(intent)
    }
}
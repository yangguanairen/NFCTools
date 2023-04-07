package com.sena.nfctools.widget.popup

import android.content.Context
import android.view.LayoutInflater
import com.lxj.xpopup.core.BasePopupView
import com.lxj.xpopup.core.CenterPopupView
import com.sena.nfctools.R
import com.sena.nfctools.databinding.PopupTipBinding


/**
 * FileName: TipPopup
 * Author: JiaoCan
 * Date: 2023/4/7 11:16
 */

class TipPopup(context: Context) : CenterPopupView(context) {

    private lateinit var binding: PopupTipBinding

    private var onCancelListener: OnCancelListener? = null
    private var onDismissListener: OnDismissListener? = null
    private var onShowListener: OnShowListener? = null

    override fun getImplLayoutId(): Int {
        return R.layout.popup_tip
    }

    override fun onCreate() {
        super.onCreate()
        binding = PopupTipBinding.bind(LayoutInflater.from(context).inflate(R.layout.popup_tip, null))
        binding.cancel.setOnClickListener {
            onCancelListener?.onCancel()
        }
    }

    fun setTitle(title: String) {
        binding.title.text = title
    }

    fun setContent(content: String) {
        binding.content.text = content
    }

    fun setOnCancelListener(l: OnCancelListener?) {
        onCancelListener = l
    }

    fun setOnDismissListener(l: OnDismissListener?) {
        onDismissListener = l
    }

    fun setOnShowListener(l: OnShowListener?) {
        onShowListener = l
    }

    override fun onDismiss() {
        super.onDismiss()
        onDismissListener?.onDismiss()
    }

    override fun onShow() {
        super.onShow()
        onShowListener?.onShow()
    }

    override fun show(): BasePopupView {
        return super.show()
    }


    interface OnCancelListener {
        fun onCancel()
    }

    interface OnDismissListener {
        fun onDismiss()
    }

    interface OnShowListener {
        fun onShow()
    }

    class Builder(context: Context) {

        private var popup: TipPopup

        init {
            popup = TipPopup(context)
        }


        fun title(title: String): Builder {
            popup.setTitle(title)
            return this
        }

        fun content(content: String): Builder {
            popup.setContent(content)
            return this
        }

        fun onDismiss(function: () -> Unit): Builder {
            popup.setOnDismissListener(object : OnDismissListener {
                override fun onDismiss() {
                    function.invoke()
                }
            })
            return this
        }

        fun onCancel(function: () -> Unit): Builder {
            popup.setOnCancelListener(object : OnCancelListener {
                override fun onCancel() {
                    function.invoke()
                }
            })
            return this
        }

        fun onShow(function: () -> Unit): Builder {
            popup.setOnShowListener(object : OnShowListener {
                override fun onShow() {
                    function.invoke()
                }
            })
            return this
        }

        fun build(): TipPopup {
            return popup
        }
    }

}


package com.dubhe.hyperlightmaster.fragment

import com.dubhe.hyperlightmaster.R
import com.dubhe.hyperlightmaster.base.BaseFragment
import com.dubhe.hyperlightmaster.databinding.FragmentInfoBinding
import com.dubhe.hyperlightmaster.util.MarkdownReader
import io.noties.markwon.Markwon

class InfoFragment : BaseFragment<FragmentInfoBinding>() {

    override fun getLayoutResId(): Int {
        return R.layout.fragment_info
    }

    override fun initView() {
        val markwon = Markwon.create(requireContext())

        markwon.setMarkdown(dataBinding.text, MarkdownReader.readAssetFile(requireContext(), "LockBrightnessMode.md"))
    }

}
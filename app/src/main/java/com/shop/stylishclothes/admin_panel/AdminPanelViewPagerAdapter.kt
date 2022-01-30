package com.shop.stylishclothes.admin_panel

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class AdminPanelViewPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> {
                WaitingFragment()
            }
            1 -> {
                ProcessingFragment()
            }
            else -> {
                SentFragment()
            }
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        when (position) {
            0 -> {
                return "Waiting"
            }
            1 -> {
                return "Processing"
            }
            2 -> {
                return "Sent"
            }
        }
        return super.getPageTitle(position)
    }

    override fun getCount(): Int {
        return 3
    }

}
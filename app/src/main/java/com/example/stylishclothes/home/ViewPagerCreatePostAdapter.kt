package com.example.stylishclothes.home

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class ViewPagerCreatePostAdapter(fm : FragmentManager) : FragmentPagerAdapter(fm) {

    override fun getCount(): Int {
        return 2
    }

    override fun getItem(position: Int): Fragment {
        return when(position) {
            0 -> {
                CreatePostFragment()
            }
            else -> {
                CreateAdFragment()
            }
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        when(position) {
            0 -> {
                return "Post"
            }
            1 -> {
                return "Advertisement"
            }
        }
        return super.getPageTitle(position)
    }

}
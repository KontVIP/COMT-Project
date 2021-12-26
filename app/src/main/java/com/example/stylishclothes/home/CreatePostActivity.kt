package com.example.stylishclothes.home

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.example.stylishclothes.R
import com.google.android.material.tabs.TabLayout


class CreatePostActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_post)

        val viewPager: ViewPager = findViewById(R.id.view_pager_create_post)
        viewPager.adapter = ViewPagerCreatePostAdapter(supportFragmentManager)

        val tabLayout: TabLayout = findViewById(R.id.tab_layout_create_post)
        tabLayout.setupWithViewPager(viewPager)


    }
}
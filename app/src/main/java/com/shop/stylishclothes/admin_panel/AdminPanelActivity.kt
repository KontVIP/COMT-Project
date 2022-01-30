package com.shop.stylishclothes.admin_panel

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager
import com.shop.stylishclothes.R
import com.shop.stylishclothes.admin_panel.models.AdminPanelViewModel
import com.google.android.material.tabs.TabLayout


class AdminPanelActivity : AppCompatActivity() {

    private lateinit var viewModel: AdminPanelViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_panel)



        viewModel = ViewModelProvider(this).get(AdminPanelViewModel::class.java)

        val viewPager: ViewPager = findViewById(R.id.admin_panel_view_pager)
        viewPager.adapter = AdminPanelViewPagerAdapter(supportFragmentManager)

        val tabLayout: TabLayout = findViewById(R.id.admin_panel_tab_layout)
        tabLayout.setupWithViewPager(viewPager)


        //when page is refreshing
        val viewPagerItem : Int? = intent.extras?.getInt("viewPagerItem")
        if (viewPagerItem != null) {
            viewPager.currentItem = viewPagerItem
        }



    }


}




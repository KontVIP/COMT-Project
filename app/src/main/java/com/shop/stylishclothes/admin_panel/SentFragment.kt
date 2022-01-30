package com.shop.stylishclothes.admin_panel

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.shop.stylishclothes.R
import com.shop.stylishclothes.admin_panel.models.AdminPanelViewModel
import com.shop.stylishclothes.admin_panel.models.SentViewModel

class SentFragment : Fragment() {

    private lateinit var viewModel: SentViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val rootView: View = inflater.inflate(R.layout.fragment_sent, container, false)


        val listView = rootView.findViewById<ListView>(R.id.offer_list)

        var offerList = listOf<Offer>()
        viewModel = ViewModelProvider(this).get(SentViewModel::class.java)

        viewModel.offersSentLive.observe(viewLifecycleOwner, Observer {
            offerList = it
            if (offerList.isNotEmpty()) {
                val offerAdapter = OfferAdapter(
                    context = requireContext(),
                    offers = offerList!!,
                    AdminPanelViewModel.OfferType.SENT
                )
                listView.adapter = offerAdapter
            }
        })


        val swipeRefreshLayout = rootView.findViewById<SwipeRefreshLayout>(R.id.pullToRefresh)
        swipeRefreshLayout.setOnRefreshListener {
            activity?.finish()
            activity?.intent?.putExtra("viewPagerItem", 2)
            startActivity(activity?.intent)
        }


        return rootView
    }

}
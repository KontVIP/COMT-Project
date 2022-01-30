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
import com.shop.stylishclothes.admin_panel.models.WaitingViewModel


class WaitingFragment : Fragment() {

    private lateinit var viewModel: WaitingViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val rootView: View = inflater.inflate(R.layout.fragment_watiting, container, false)


        val listView = rootView.findViewById<ListView>(R.id.offer_list)
        var offerAdapter: OfferAdapter? = null

        var offerList = listOf<Offer>()
        viewModel = ViewModelProvider(this).get(WaitingViewModel::class.java)

        viewModel.offersWaitingLive.observe(viewLifecycleOwner, Observer {
            offerList = it
            if (offerList.isNotEmpty()) {
                offerAdapter = OfferAdapter(
                    context = requireContext(),
                    offers = offerList!!,
                    AdminPanelViewModel.OfferType.WAITING,
                )
                listView.adapter = offerAdapter
            }
        })



        val swipeRefreshLayout = rootView.findViewById<SwipeRefreshLayout>(R.id.pullToRefresh)
        swipeRefreshLayout.setOnRefreshListener {
            activity?.finish()
            activity?.intent?.putExtra("viewPagerItem", 0)
            startActivity(activity?.intent)
        }

        return rootView
    }
}
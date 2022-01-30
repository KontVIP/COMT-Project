package com.shop.stylishclothes.admin_panel.models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.shop.stylishclothes.admin_panel.Offer
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ProcessingViewModel : ViewModel() {

    private var mutableOfferList = mutableListOf<Offer>()
    val mutableOfferProductList = mutableListOf<Offer.OfferProduct>()

    private var _offersProcessingLive = MutableLiveData<List<Offer>>()
    var offersProcessingLive: LiveData<List<Offer>> = _offersProcessingLive


    init {
        getOffers()
    }

    private fun getOffers() {
        FirebaseDatabase.getInstance().getReference("Offers/Processing")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.children.forEach {
                        mutableOfferProductList.clear()
                        it.child("listOfOfferProduct").children.forEach { productSnapshot ->
                            mutableOfferProductList.add(
                                Offer.OfferProduct(
                                    productSnapshot.child("id").value as String,
                                    productSnapshot.child("quantity").value.toString().toInt(),
                                    productSnapshot.child("size").value as String,
                                )
                            )
                        }
                        mutableOfferList.add(
                            Offer(
                                it.child("offerId").value as String,
                                it.child("fullName").value as String,
                                it.child("email").value as String,
                                it.child("phone").value as String,
                                it.child("region").value as String,
                                it.child("city").value as String,
                                it.child("deliveringBy").value.toString(),
                                it.child("officeNum").value.toString().toInt(),
                                mutableOfferProductList
                            )
                        )

                    }
                    _offersProcessingLive.value = mutableOfferList
                    offersProcessingLive = _offersProcessingLive
                }

                override fun onCancelled(error: DatabaseError) {
                }

            })

    }

    override fun onCleared() {
        super.onCleared()
    }
}
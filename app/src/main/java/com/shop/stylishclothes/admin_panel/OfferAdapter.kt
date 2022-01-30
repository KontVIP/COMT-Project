package com.shop.stylishclothes.admin_panel

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import com.shop.stylishclothes.R
import com.shop.stylishclothes.catalog.Product
import com.shop.stylishclothes.catalog.ProductAdapter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.ArrayList


class OfferAdapter(context: Context, var offers: List<Offer>, var offerType: String) : ArrayAdapter<Offer>(context, 0, offers) {


    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var listItemView : View? = convertView
        if (listItemView == null) {
            listItemView =
                LayoutInflater.from(context).inflate(R.layout.list_offer_item, parent, false)
        }
        //Product offer list
        val productIds = mutableListOf<String>()
        val productQuantities = mutableListOf<Int>()
        val productSizes = mutableListOf<String>()
        val products = mutableListOf<Product>()
        var totalPrice = 0
        var productQuantity = 0
        var quantity : Int = 0

        FirebaseDatabase.getInstance()
            .getReference("Offers/$offerType/")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    productIds.clear()
                    productQuantities.clear()
                    productSizes.clear()
                    quantity = 0
                    snapshot.children.forEach {
                        if (it.child("offerId").value.toString().trim() == offers[position].offerId.toString().trim()) {
                            //to prevent the error when data is moving to the other tab
                            listItemView?.findViewById<TextView>(R.id.full_name_text_view)?.text = offers[position].fullName
                            listItemView?.findViewById<TextView>(R.id.phone_text_view)?.text = offers[position].phone
                            listItemView?.findViewById<TextView>(R.id.email_text_view)?.text = offers[position].email
                            listItemView?.findViewById<TextView>(R.id.region_text_view)?.text = offers[position].region
                            listItemView?.findViewById<TextView>(R.id.city_text_view)?.text = ", місто ${offers[position].city}"
                            listItemView?.findViewById<TextView>(R.id.delivering_by_text_view)?.text = offers[position].deliveringBy
                            listItemView?.findViewById<TextView>(R.id.branch_num_text_view)?.text = offers[position].officeNum.toString()

                            it.child("listOfOfferProduct").children.forEach {
                                productIds.add(it.child("id").value.toString())
                                productQuantities.add(it.child("quantity").value.toString().toInt())
                                productSizes.add(it.child("size").value.toString())
                                quantity += it.child("quantity").value.toString().toInt()
                                listItemView?.findViewById<TextView>(R.id.quantity_text_view)?.text = quantity.toString()
                            }

                            FirebaseDatabase.getInstance()
                                .getReference("Categories")
                                .addListenerForSingleValueEvent(object : ValueEventListener {
                                    override fun onDataChange(snapshot: DataSnapshot) {
                                        for (dataSnapshot in snapshot.children) {
                                            for (i in productIds.indices) {
                                                if (dataSnapshot.child("Products/" + productIds.get(i)).getValue(
                                                        Product::class.java) != null) {
                                                    val product = dataSnapshot.child("Products/" + productIds.get(i)).getValue(
                                                        Product::class.java)
                                                    product?.currentSize = productSizes[i]
                                                    if (product!!.price != "") {
                                                        totalPrice += Integer.valueOf(product!!.price) * productQuantities[i]
                                                        productQuantity += productQuantities[i]
                                                    }
                                                    products.add(product)
                                                }
                                            }
                                            val productAdapter = ProductAdapter(context as Activity?, products as ArrayList<Product>?, 4, offerType, offers[position].offerId)

                                            listItemView?.findViewById<ListView>(R.id.product_offer_list)?.adapter = productAdapter
                                            listItemView?.findViewById<TextView>(R.id.total_price_text_view)?.text = "₴$totalPrice"


                                            listItemView?.findViewById<LinearLayout>(R.id.list_linear_layout)?.layoutParams?.height = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                                                115F, context.resources.displayMetrics).toInt() * products.size
                                        }


                                    }

                                    override fun onCancelled(error: DatabaseError) {}
                                })

                        }

                    }

                }

                override fun onCancelled(error: DatabaseError) {}
            })


        //Alert Dialog
        var cardView = listItemView?.findViewById<CardView>(R.id.offer_card_view)
        cardView?.setOnLongClickListener {

            val closeBuilder = AlertDialog.Builder(context)
            closeBuilder.setCancelable(true)

            when (offerType) {
                "Waiting" -> {
                    closeBuilder.setMessage("Перемістити в Processing?")
                    closeBuilder.setPositiveButton(
                        "Yes"
                    ) { _, _ ->


                        FirebaseDatabase.getInstance().getReference("Offers/$offerType").addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                snapshot.children.forEach { offerSnapshot ->
                                    if (offerSnapshot.key == offers[position].offerId) {
                                        FirebaseDatabase.getInstance().getReference("Offers/Processing/${offerSnapshot.key}").setValue(offerSnapshot.value).addOnSuccessListener {
                                            offerSnapshot.ref.removeValue()
                                            remove(offers[position])
                                            notifyDataSetChanged()
                                        }
                                    }
                                }
                            }
                            override fun onCancelled(error: DatabaseError) {
                                Toast.makeText(context, "Error: can't load the data!", Toast.LENGTH_SHORT).show()
                            }

                        })

                    }
                }
                "Processing" -> {
                    closeBuilder.setMessage("Перемістити в Sent?")
                    closeBuilder.setPositiveButton(
                        "Yes"
                    ) { _, _ ->
                        FirebaseDatabase.getInstance().getReference("Offers/$offerType").addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                snapshot.children.forEach { offerSnapshot ->
                                    if (offerSnapshot.key == offers[position].offerId) {
                                        FirebaseDatabase.getInstance().getReference("Offers/Sent/${offerSnapshot.key}").setValue(offerSnapshot.value).addOnSuccessListener {
                                            offerSnapshot.ref.removeValue()
                                            remove(offers[position])
                                            notifyDataSetChanged()
                                        }
                                    }
                                }
                            }
                            override fun onCancelled(error: DatabaseError) {
                                Toast.makeText(context, "Error: can't load the data!", Toast.LENGTH_SHORT).show()
                            }

                        })

                    }
                }
                "Sent" -> {
                    closeBuilder.setMessage("Видалити?")
                    closeBuilder.setPositiveButton(
                        "Yes"
                    ) { _, _ ->
                        FirebaseDatabase.getInstance().getReference("Offers/$offerType").addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                snapshot.children.forEach { offerSnapshot ->
                                    if (offerSnapshot.key == offers[position].offerId) {
                                        offerSnapshot.ref.removeValue()
                                        remove(offers[position])
                                        notifyDataSetChanged()
                                    }
                                }
                            }
                            override fun onCancelled(error: DatabaseError) {
                                Toast.makeText(context, "Error: can't load the data!", Toast.LENGTH_SHORT).show()
                            }

                        })

                    }
                }
            }

            closeBuilder.setNegativeButton(
                "No"
            ) { dialog, _ -> dialog.cancel() }

            val closeAlert = closeBuilder.create()
            closeAlert.show()

            true

        }





        return listItemView!!
    }
}
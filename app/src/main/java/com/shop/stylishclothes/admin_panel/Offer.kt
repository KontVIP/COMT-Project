package com.shop.stylishclothes.admin_panel

data class Offer (
    var offerId : String,
    var fullName : String,
    var email : String,
    var phone : String,
    var region : String,
    var city : String,
    var deliveringBy : String,
    var officeNum : Int,
    var listOfOfferProduct: List<OfferProduct>

    ) {

    data class OfferProduct (

        var id : String,
        var quantity : Int,
        var size : String

        )
}
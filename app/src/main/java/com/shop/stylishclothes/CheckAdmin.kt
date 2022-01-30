package com.shop.stylishclothes

import com.google.firebase.auth.FirebaseUser


class CheckAdmin {

    companion object {
        fun isAdmin(currentUser: FirebaseUser?) : Boolean {
            return currentUser?.email == "shop.clothes.stylish@gmail.com"
        }
    }

}
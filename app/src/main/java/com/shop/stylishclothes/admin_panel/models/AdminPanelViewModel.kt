package com.shop.stylishclothes.admin_panel.models

import androidx.lifecycle.ViewModel

class AdminPanelViewModel : ViewModel() {

    object OfferType {
        const val WAITING = "Waiting"
        const val PROCESSING = "Processing"
        const val SENT = "Sent"
    }

    override fun onCleared() {
        super.onCleared()
    }
}
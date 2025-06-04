package com.example.autocaravanas.model

import com.google.gson.annotations.SerializedName

data class StripeCheckoutResponse(
    @SerializedName("checkout_url")
    val checkoutUrl: String
)
package com.example.autocaravanas.model

import com.google.gson.annotations.SerializedName

data class ReservaResponse(
    @SerializedName("success")
    var success: Boolean,
    @SerializedName("data")
    var reserva: Reserva?,
    @SerializedName("message")
    var message: String
)

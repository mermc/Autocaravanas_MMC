package com.example.autocaravanas.model

import com.google.gson.annotations.SerializedName

data class ReservasResponse(
    @SerializedName("success")
    var success: Boolean,
    @SerializedName("data")
    var reservas: ArrayList<Reserva>,
    @SerializedName("message")
    var message: String
)

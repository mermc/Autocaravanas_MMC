package com.example.autocaravanas.model

import com.google.gson.annotations.SerializedName

data class DeleteResponse(
    @SerializedName("success")
    var success: Boolean,
    @SerializedName("data")
    var reserva: ArrayList<Any>,
    @SerializedName("message")
    var message: String
)

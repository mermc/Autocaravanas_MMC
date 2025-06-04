package com.example.autocaravanas.model

import com.google.gson.annotations.SerializedName

data class LogoutResponse(
    @SerializedName("success")
    var success: Boolean,
    @SerializedName("data")
    var login: Logout,
    @SerializedName("message")
    var message: String
)

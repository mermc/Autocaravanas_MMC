package com.example.autocaravanas.model

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("success")
    var success: Boolean,
    @SerializedName("data")
    var login: Login,
    @SerializedName("message")
    var message: String
)

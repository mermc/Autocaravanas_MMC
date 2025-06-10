package com.example.autocaravanas.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Usuario(
    val id: Int,
    val name: String,
    val email: String,
    @SerializedName("email_verified_at") val emailVerifiedAt: String?
)

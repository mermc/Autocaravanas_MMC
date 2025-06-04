package com.example.autocaravanas.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Usuario(
    @SerializedName("nombre")
    val nombre: String,
    @SerializedName("email")
    val email: String
) : Serializable


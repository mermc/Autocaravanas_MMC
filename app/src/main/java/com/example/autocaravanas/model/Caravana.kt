package com.example.autocaravanas.model
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Caravana(
    @SerializedName("id")
    val id: Int,
    @SerializedName("nombre")
    val nombre: String,
    @SerializedName("modelo")
    val modelo: String,
    @SerializedName("capacidad")
    val capacidad: Int,
    @SerializedName("precio_dia")
    val precioDia: Double,
    @SerializedName("foto")
    val foto: String?
) : Serializable
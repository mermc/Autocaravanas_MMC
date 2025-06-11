package com.example.autocaravanas.model

import com.google.gson.annotations.SerializedName

data class FechaDisponibilidad(
    @SerializedName("fecha_inicio")
    val fechaInicio: String,
    @SerializedName("fecha_fin")
    val fechaFin: String
)

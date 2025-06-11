package com.example.autocaravanas.model

import com.google.gson.annotations.SerializedName

data class DisponiblesResponse(
    @SerializedName("disponibles")
    val disponibles: List<Caravana>
)
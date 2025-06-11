package com.example.autocaravanas.model

data class ReservaUpdateResponse(
    val reserva: Reserva?,
    val error: String? = null
)

package com.example.autocaravanas.model
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Reserva(
    @SerializedName("id")
    val id: Int,
    @SerializedName("user_id")
    val userId: Int,
    @SerializedName("usuario")
    val usuario: Usuario?,
    @SerializedName("caravana_id")
    val caravanaId: Int,
    @SerializedName("caravana")
    val caravana: Caravana?,
    @SerializedName("fecha_inicio")
    val fechaInicio: String,
    @SerializedName("fecha_fin")
    val fechaFin: String,
    @SerializedName("precio_total")
    val precioTotal: String,
    @SerializedName("precio_pagado")
    val precioPagado: String,
    @SerializedName("fianza")
    val fianza: String,
    @SerializedName("created_at")
    val createdAt: String?,
    @SerializedName("updated_at")
    val updatedAt: String?
) : Serializable

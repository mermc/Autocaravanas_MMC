package com.example.autocaravanas.model
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
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
) : Parcelable
//Parcelable permite que la clase Caravana sea serializable y pueda ser pasada entre actividades o fragmentos
package com.example.autocaravanas.repository

import android.util.Log
import com.example.autocaravanas.api.ApiHelper
import com.example.autocaravanas.model.Caravana
import com.example.autocaravanas.model.FechaDisponibilidad
import com.example.autocaravanas.model.GenericResponse
import com.example.autocaravanas.model.Reserva
import com.example.autocaravanas.model.ReservaResponse
import com.example.autocaravanas.model.ReservaUpdateResponse
import com.example.autocaravanas.model.Usuario


class ReservaRepository (private val apiHelper: ApiHelper) {

    suspend fun login(email: String, password: String) =
        apiHelper.login(email, password)

    suspend fun register(name: String, email: String, password: String, confirmPassword: String) =
        apiHelper.register(name, email, password, confirmPassword)

    suspend fun reenviarEmailVerificacion(): GenericResponse {
        return apiHelper.reenviarEmailVerificacion()
    }

    suspend fun getUser() = apiHelper.getUser()

    suspend fun logout() = apiHelper.logout()

    suspend fun enviarEmail(to: String, subject: String, message: String) =
        apiHelper.enviarEmail(to, subject, message)

    suspend fun getReservas(): List<Reserva> {
        return apiHelper.getReservas()
    }

    suspend fun addReserva(reserva: Reserva): ReservaResponse = apiHelper.addReserva(reserva)

    suspend fun getCaravanasDisponibles(fechaInicio: String, fechaFin: String): Pair<List<Caravana>, String?> {
        val disponibilidad = FechaDisponibilidad(fechaInicio, fechaFin)

        return apiHelper.getCaravanasDisponibles(disponibilidad)
        Log.d("ReservaRepository", "Caravanas disponibles: ${disponibilidad.fechaInicio} a ${disponibilidad.fechaFin}")
    }

    suspend fun updateReserva(reserva: Reserva): ReservaUpdateResponse {
        val response = apiHelper.updateReserva(reserva)
        return if (response.isSuccessful) {
            response.body() ?: ReservaUpdateResponse(null)
        } else {

            ReservaUpdateResponse(null)
        }
    }

    suspend fun deleteReserva(reserva: Reserva) = apiHelper.deleteReserva(reserva.id)

}
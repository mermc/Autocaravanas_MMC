package com.example.autocaravanas.repository

import com.example.autocaravanas.api.ApiHelper
import com.example.autocaravanas.model.Reserva

class ReservaRepository (private val apiHelper: ApiHelper) {

    suspend fun login(email: String, password: String) =
        apiHelper.login(email, password)

    suspend fun register(name: String, email: String, password: String, confirmPassword: String) =
        apiHelper.register(name, email, password, confirmPassword)

    suspend fun logout() = apiHelper.logout()

    suspend fun getReservas() = apiHelper.getReservas()

    suspend fun addReserva(reserva: Reserva) = apiHelper.addReserva(reserva)

    suspend fun updateReserva(reserva: Reserva) = apiHelper.updateReserva(reserva)

    suspend fun deleteReserva(reserva: Reserva) = apiHelper.deleteReserva(reserva)

}
package com.example.autocaravanas.api

import com.example.autocaravanas.model.DeleteResponse
import com.example.autocaravanas.model.Login
import com.example.autocaravanas.model.LoginResponse
import com.example.autocaravanas.model.Logout
import com.example.autocaravanas.model.LogoutResponse
import com.example.autocaravanas.model.Reserva
import com.example.autocaravanas.model.ReservaResponse
import com.example.autocaravanas.model.ReservasResponse
import com.example.autocaravanas.model.StripeCheckoutResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ApiHelper(private val apiService: ApiService) {

    suspend fun login(email: String, password: String) : LoginResponse {
        return withContext(Dispatchers.IO) {
            val response = apiService.login(email, password)
            response.body() ?: LoginResponse(false, Login("", ""), "")
        }
    }

    suspend fun register(name: String, email: String, password: String, confirmPassword: String) : LoginResponse {
        return withContext(Dispatchers.IO) {
            val response = apiService.register(name, email, password, confirmPassword)
            response.body() ?: LoginResponse(false, Login("", ""), "")
        }
    }

    suspend fun logout() : LogoutResponse {
        return withContext(Dispatchers.IO) {
            val response = apiService.logout()
            response.body() ?: LogoutResponse(false, Logout(""), "")
        }
    }

    suspend fun getReservas(): ReservasResponse {
        return withContext(Dispatchers.IO) {
            val response = apiService.getReservas()
            response.body() ?: ReservasResponse(false, arrayListOf(), "")
        }
    }

    suspend fun addReserva(reserva: Reserva): StripeCheckoutResponse {
        return withContext(Dispatchers.IO) {
            val response = apiService.addReserva(reserva.caravanaId, reserva.fechaInicio, reserva.fechaFin)
            response.body() ?:  StripeCheckoutResponse ("")
        }
    }

    suspend fun updateReserva(reserva: Reserva): ReservaResponse {
        return withContext(Dispatchers.IO) {
            val response = apiService.updateReserva(reserva.id,reserva.caravanaId, reserva.fechaInicio, reserva.fechaFin)
            response.body() ?: ReservaResponse(false, null, "")
        }
    }

    suspend fun deleteReserva(reserva: Reserva): DeleteResponse {
        return withContext(Dispatchers.IO) {
            val response = apiService.deleteReserva(reserva.id)
            response.body() ?: DeleteResponse(false, arrayListOf(), "")
        }
    }

}
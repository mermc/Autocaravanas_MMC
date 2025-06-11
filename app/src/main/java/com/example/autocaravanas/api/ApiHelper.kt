package com.example.autocaravanas.api


import android.util.Log
import com.example.autocaravanas.model.Caravana
import com.example.autocaravanas.model.DeleteResponse
import com.example.autocaravanas.model.FechaDisponibilidad
import com.example.autocaravanas.model.GenericResponse
import com.example.autocaravanas.model.Login
import com.example.autocaravanas.model.LoginResponse
import com.example.autocaravanas.model.Logout
import com.example.autocaravanas.model.LogoutResponse
import com.example.autocaravanas.model.Reserva
import com.example.autocaravanas.model.ReservaResponse
import com.example.autocaravanas.model.ReservaUpdateResponse
import com.example.autocaravanas.model.Usuario
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response

class ApiHelper(private val apiService: ApiService) {

    suspend fun login(email: String, password: String): LoginResponse = withContext(Dispatchers.IO) {
        val response = apiService.login(email, password)
        response.body() ?: LoginResponse(false, Login("", ""), "Error en la respuesta")
    }

    suspend fun register(name: String, email: String, password: String, confirmPassword: String): LoginResponse =
        withContext(Dispatchers.IO) {
            val response = apiService.register(name, email, password, confirmPassword)
            response.body() ?: LoginResponse(false, Login("", ""), "Error en la respuesta")
        }

    suspend fun logout(): LogoutResponse = withContext(Dispatchers.IO) {
        val response = apiService.logout()
        response.body() ?: LogoutResponse(false, Logout(""), "Error al cerrar sesión")
    }

    suspend fun getReservas(): List<Reserva> {
        return apiService.getReservas()
    }

    suspend fun getCaravanasDisponibles(request: FechaDisponibilidad):  Pair<List<Caravana>, String?> =
        withContext(Dispatchers.IO) {
            val response = apiService.getCaravanasDisponibles(request)
            if (response.isSuccessful) {
                Pair(response.body()?.disponibles ?: emptyList(), null)
            } else if (response.code() == 422) {
                val errorJson = response.errorBody()?.string()
                val errorMsg = errorJson?.let {
                    try {
                        val json = org.json.JSONObject(it)
                        val errores = json.optJSONArray("errors")
                        if (errores != null && errores.length() > 0) {
                            // Une todos los mensajes en uno solo, separados por salto de línea
                            (0 until errores.length()).joinToString("\n") { i -> errores.getString(i) }
                        } else {
                            "Error de validación"
                        }
                    } catch (e: Exception) {
                        "Error de validación"
                    }
                } ?: "Error de validación"
                Pair(emptyList(), errorMsg)
            } else {
                Pair(emptyList(), "Error desconocido")
            }
        }

      suspend fun addReserva(reserva: Reserva): ReservaResponse = withContext(Dispatchers.IO) {
        val response = apiService.addReserva(reserva.caravanaId, reserva.fechaInicio, reserva.fechaFin)
        response.body() ?: ReservaResponse(false, null, "Error al reservar")
    }

    suspend fun updateReserva(reserva: Reserva): Response<ReservaUpdateResponse> = withContext(Dispatchers.IO) {
        apiService.updateReserva(
            id = reserva.id,
            caravanaId = reserva.caravanaId,
            fechaInicio = reserva.fechaInicio,
            fechaFin = reserva.fechaFin
        )
    }


    suspend fun deleteReserva(reservaId: Int): DeleteResponse = withContext(Dispatchers.IO) {
        val response = apiService.deleteReserva(reservaId)
        response.body() ?: DeleteResponse(false, "Error al eliminar reserva")
    }

    suspend fun enviarEmail(to: String, subject: String, message: String): GenericResponse =
        withContext(Dispatchers.IO) {
            val response = apiService.enviarEmail(to, subject, message)
            response.body() ?: GenericResponse(false, "Error al enviar email")
        }

    suspend fun reenviarEmailVerificacion(): GenericResponse = withContext(Dispatchers.IO) {
        val response = apiService.reenviarEmailVerificacion()
        response.body() ?: GenericResponse(false, "Error al reenviar verificación")
    }
/*
    suspend fun getUser() = withContext(Dispatchers.IO) {
        val response = apiService.getUser()
        Log.d("ApiDebug", "getUser response: $response")
        response.body()!!
    }

 */
suspend fun getUser(): Usuario? = withContext(Dispatchers.IO) {
    val response = apiService.getUser()
    if (response.isSuccessful) {
        Log.d("ApiDebug", "getUser success: ${response.body()}")
        response.body()
    } else {
        Log.e("ApiDebug", "getUser error: ${response.code()} - ${response.errorBody()?.string()}")
        null
    }
}

}

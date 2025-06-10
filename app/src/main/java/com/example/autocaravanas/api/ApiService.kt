package com.example.autocaravanas.api


import com.example.autocaravanas.model.Caravana
import com.example.autocaravanas.model.DeleteResponse
import com.example.autocaravanas.model.DisponiblesResponse
import com.example.autocaravanas.model.FechaDisponibilidad
import com.example.autocaravanas.model.GenericResponse
import com.example.autocaravanas.model.LoginResponse
import com.example.autocaravanas.model.LogoutResponse
import com.example.autocaravanas.model.Reserva
import com.example.autocaravanas.model.ReservaResponse
import com.example.autocaravanas.model.ReservaUpdateResponse
import com.example.autocaravanas.model.ReservasResponse
import com.example.autocaravanas.model.Usuario
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {

    @FormUrlEncoded
    @POST("api/login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): Response<LoginResponse>

    @FormUrlEncoded
    @POST("api/register")
    suspend fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String,
        @Field("confirm_password") confirmPassword: String
    ): Response<LoginResponse>

    @POST("api/logout")
    suspend fun logout(): Response<LogoutResponse>

    @GET("api/reserva")
    suspend fun getReservas(): List<Reserva>

    @FormUrlEncoded
    @POST("api/reserva")
    suspend fun addReserva(
        @Field("caravana_id") caravanaId: Int,
        @Field("fecha_inicio") fechaInicio: String,
        @Field("fecha_fin") fechaFin: String
    ): Response<ReservaResponse>

    @PUT("api/reserva/{reserva}")
    @FormUrlEncoded
    suspend fun updateReserva(
        @Path("reserva") id: Int,
        @Field("caravana_id") caravanaId: Int,
        @Field("fecha_inicio") fechaInicio: String,
        @Field("fecha_fin") fechaFin: String
    ): Response<ReservaUpdateResponse>

    @DELETE("api/reserva/{reserva}")
    suspend fun deleteReserva(
        @Path("reserva") id: Int
    ): Response<DeleteResponse>

    @POST("api/reservas/disponibles")
    suspend fun getCaravanasDisponibles(
        @Body request: FechaDisponibilidad
    ): Response<DisponiblesResponse>

    @FormUrlEncoded
    @POST("api/email")
    suspend fun enviarEmail(
        @Field("to") to: String,
        @Field("subject") subject: String,
        @Field("message") message: String
    ): Response<GenericResponse>

    @GET("api/user")
    suspend fun getUser(): Response<Usuario>

    @POST("email/verification-notification")
    suspend fun reenviarEmailVerificacion(): Response<GenericResponse>
}
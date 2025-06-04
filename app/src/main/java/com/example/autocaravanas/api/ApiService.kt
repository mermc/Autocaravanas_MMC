package com.example.autocaravanas.api


import com.example.autocaravanas.model.Caravana
import com.example.autocaravanas.model.DeleteResponse
import com.example.autocaravanas.model.LoginResponse
import com.example.autocaravanas.model.LogoutResponse
import com.example.autocaravanas.model.ReservaResponse
import com.example.autocaravanas.model.ReservasResponse
import com.example.autocaravanas.model.StripeCheckoutResponse
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @POST("api/login")
    @FormUrlEncoded
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ) : Response<LoginResponse>

    @POST("api/register")
    @FormUrlEncoded
    suspend fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String,
        @Field("confirm_password") confirmPassword: String
    ) : Response<LoginResponse>

    @POST("api/logout")
    suspend fun logout() : Response<LogoutResponse>

    @Headers(
        "Accept: application/json",
    )
    @GET("api/reserva")
    suspend fun getReservas(): Response<ReservasResponse>

    @Headers(
        "Accept: application/json",
    )
    @POST("api/reserva/stripe-checkout")
    @FormUrlEncoded
    suspend fun addReserva(
        @Query("caravana_id") caravanaId: Int,
        @Query("fecha_inicio") fechaInicio: String,
        @Query("fecha_fin") fechaFin: String
    ): Response<StripeCheckoutResponse>

    @Headers(
        "Accept: application/json",
    )
    @PUT("api/reserva/{id}")
    @FormUrlEncoded
    suspend fun updateReserva(
        @Path("id") id: Int,
        @Field("caravana_id") caravanaId: Int,
        @Field("fecha_inicio") fechaInicio: String,
        @Field("fecha_fin") fechaFin: String
    ): Response<ReservaResponse>

    @Headers(
        "Accept: application/json",
    )
    @DELETE("api/reserva/{id}")
    suspend fun deleteReserva(
        @Path("id") id: Int
    ): Response<DeleteResponse>

    @Headers(
        "Accept: application/json",
    )
    @GET("api/caravanas/disponibles")
    suspend fun getCaravanasDisponibles(
        @Query("fecha_inicio") fechaInicio: String,
        @Query("fecha_fin") fechaFin: String
    ): Response<List<Caravana>>

    @POST("api/email")
    @FormUrlEncoded
    suspend fun enviarEmail(
        @Field("to") to: String,
        @Field("subject") subject: String,
        @Field("message") message: String
    ): Response<GenericResponse>

}
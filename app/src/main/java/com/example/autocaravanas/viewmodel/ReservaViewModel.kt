package com.example.autocaravanas.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.autocaravanas.api.ApiAdapter.instance
import com.example.autocaravanas.api.ApiHelper
import com.example.autocaravanas.model.Reserva
import com.example.autocaravanas.model.ReservasResponse
import com.example.autocaravanas.model.ReservaResponse
import com.example.autocaravanas.repository.ReservaRepository

class ReservaViewModel (application: Application) : AndroidViewModel(application) {

    private val _reservas: MutableLiveData<List<Reserva>> = MutableLiveData()
    val reservas: LiveData<List<Reserva>> get() = _reservas

    private var repository: ReservaRepository = ReservaRepository(ApiHelper(instance!!))

    suspend fun getReservas(): ReservasResponse {
        val reservasResponse = repository.getReservas()
        _reservas.value = reservasResponse.reservas

        return reservasResponse
    }

    suspend fun addReserva(reserva: Reserva): Boolean {
        val reservaResponse = repository.addReserva(reserva)

        if (reservaResponse.success) {
            // Añadimos la reserva a la lista, y reasignamos el valor de LiveData para que se actualice
            _reservas.value = _reservas.value?.toMutableList()?.apply {
                add(reservaResponse.reserva!!)
            }
        }

        return reservaResponse.success
    }

    suspend fun updateReserva(reserva: Reserva, newReserva: Reserva): Boolean {
        val reservaResponse = repository.updateReserva(newReserva)

        if (reservaResponse.success) {

            // Reasignamos el valor de LiveData para que se actualice
            _reservas.value = _reservas.value.orEmpty().map {
                if (it == reserva) newReserva else it
            }

            _reservas.value = _reservas.value
        }

        return reservaResponse.success
    }

    suspend fun deleteReserva(reserva: Reserva): Boolean {
        val reservaResponse = repository.deleteReserva(reserva)

        if (reservaResponse.success) {
            // Reasignamos el valor de LiveData para que se actualice
            _reservas.value = _reservas.value?.toMutableList()?.apply {
                remove(reserva)
            }
        }

        return reservaResponse.success
    }

    suspend fun logout() : Boolean {
        val logoutResponse = repository.logout()
        return logoutResponse.success
    }
}
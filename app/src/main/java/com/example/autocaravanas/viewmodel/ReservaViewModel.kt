package com.example.autocaravanas.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.autocaravanas.api.ApiAdapter
import com.example.autocaravanas.api.ApiHelper
import com.example.autocaravanas.model.Caravana
import com.example.autocaravanas.model.Reserva
import com.example.autocaravanas.repository.ReservaRepository
import kotlinx.coroutines.launch

class ReservaViewModel(application: Application) : AndroidViewModel(application) {

    internal var repository: ReservaRepository = ReservaRepository(ApiHelper(ApiAdapter.apiService))

    private val _reservas = MutableLiveData<List<Reserva>>()
    val reservas: LiveData<List<Reserva>> get() = _reservas

    private val _updateResult = MutableLiveData<Reserva?>()
    val updateResult: LiveData<Reserva?> get() = _updateResult

    private val _deleteMessage = MutableLiveData<String?>()
    val deleteMessage: LiveData<String?> = _deleteMessage

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> get() = _loading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    // Cargar todas las reservas del usuario
    fun getReservas() {
        viewModelScope.launch {
            _loading.value = true
            try {
                val response = repository.getReservas()
                //Log.d("ReservaViewModel", "Respuesta getReservas: $response")
                _reservas.value = response
            } catch (e: Exception) {
                //Log.e("ReservaViewModel", "Error al cargar reservas", e)
                _error.value = "Error al cargar reservas: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    // Crear una nueva reserva
    fun crearReserva(reserva: Reserva) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val response = repository.addReserva(reserva)
                if (response.success && response.reserva != null) {
                    _reservas.value = ((_reservas.value.orEmpty() + response.reserva) as List<Reserva>?)
                } else {
                    _error.value = response.message
                }
            } catch (e: Exception) {
                _error.value = "Error al crear reserva: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    // Obtener caravanas disponibles entre dos fechas
    fun getCaravanasDisponibles
                (fechaInicio: String,
                 fechaFin: String,
                 onResult: (List<Caravana>) -> Unit) {
        viewModelScope.launch {
            try {
                val (disponibles, errorMsg) = repository.getCaravanasDisponibles(fechaInicio, fechaFin)
                if (errorMsg != null) {
                    _error.value = errorMsg
                }
                onResult(disponibles)
            } catch (e: Exception) {
                _error.value = "Error al consultar disponibilidad"
                onResult(emptyList())
            }
        }
    }

    // Actualizar una reserva
    fun updateReserva(reserva: Reserva, nueva: Reserva) {
        viewModelScope.launch {
            _loading.value = true
            try {
                Log.d("UpdateViewModel", "Ha llamado al ViewModel para actualizar la reserva: $nueva")
                val result = repository.updateReserva(nueva)
                if (result.reserva != null) {
                    // Actualiza la lista local
                    _reservas.value = _reservas.value.orEmpty().map {
                        if (it.id == reserva.id) result.reserva else it
                    }
                    _updateResult.value = result.reserva
                } else {
                    _updateResult.value = null
                    _error.value = "Menos de 7 días para inicio.No se pudo actualizar la reserva"
                }
            } catch (e: Exception) {
                _updateResult.value = null
                //Log.d("UpdateDebugViewModel", "Mensaje de error enviado al observer: ${e.message}")
                _error.value = "Error al actualizar reserva: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }


    fun clearUpdateResult() {
        _updateResult.value = null
    }


    // Eliminar una reserva
    fun deleteReserva(reserva: Reserva) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val result = repository.deleteReserva(reserva)
                // Mostrar siempre el mensaje recibido, no solo si success es false
                _deleteMessage.value = result.message
                //Log.d("ReservaViewModel", "Respuesta deleteReserva: $result")
                if (result.success) {
                    _reservas.value = _reservas.value.orEmpty().filterNot { it.id == reserva.id }
                }
            } catch (e: Exception) {
                _error.value = "Error al eliminar reserva: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    // Cerrar sesión
    fun logout(onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                val result = repository.logout()
                onResult(result.success)
            } catch (e: Exception) {
                _error.value = "Error al cerrar sesión: ${e.message}"
                onResult(false)
            }
        }
    }


    fun clearError() {
        _error.value = null
    }

    fun clearDeleteMessage() {
        _deleteMessage.value = null
    }
}
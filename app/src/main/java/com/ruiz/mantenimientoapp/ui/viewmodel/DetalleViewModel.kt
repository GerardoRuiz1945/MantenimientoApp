package com.ruiz.mantenimientoapp.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ruiz.mantenimientoapp.data.network.NetworkClient
import com.ruiz.mantenimientoapp.data.network.VehiculoDto
import com.ruiz.mantenimientoapp.data.network.MantenimientoDto
import kotlinx.coroutines.launch
import java.io.IOException

sealed interface DetalleUiState {
    object Loading : DetalleUiState
    data class Success(val vehiculo: VehiculoDto, val mantenimientos: List<MantenimientoDto>) : DetalleUiState
    data class Error(val mensaje: String) : DetalleUiState
}

class DetalleViewModel : ViewModel() {
    var detalleUiState: DetalleUiState by mutableStateOf(DetalleUiState.Loading)
        private set

    fun cargarDatosVehiculo(id: String) {
        viewModelScope.launch {
            detalleUiState = DetalleUiState.Loading
            detalleUiState = try {
                // Hacemos ambas peticiones en paralelo o secuencial a la API
                val vehiculo = NetworkClient.retrofitInstance.getVehiculoPorId(id)
                val mantenimientos = NetworkClient.retrofitInstance.getHistorialMantenimiento(id)
                DetalleUiState.Success(vehiculo, mantenimientos)
            } catch (e: IOException) {
                DetalleUiState.Error("Error de conexión al obtener detalles.")
            } catch (e: Exception) {
                DetalleUiState.Error("Error: ${e.localizedMessage}")
            }
        }
    }
}
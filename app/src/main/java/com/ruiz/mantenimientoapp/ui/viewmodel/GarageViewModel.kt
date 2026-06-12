package com.ruiz.mantenimientoapp.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ruiz.mantenimientoapp.data.network.NetworkClient
import com.ruiz.mantenimientoapp.data.network.VehiculoDto
import kotlinx.coroutines.launch
import java.io.IOException

// 1. Definimos los 3 estados obligatorios de la rúbrica
sealed interface GarageUiState {
    object Loading : GarageUiState
    data class Success(val vehiculos: List<VehiculoDto>) : GarageUiState
    data class Error(val mensaje: String) : GarageUiState
}

class GarageViewModel : ViewModel() {
    // Estado que la pantalla estará observando
    var garageUiState: GarageUiState by mutableStateOf(GarageUiState.Loading)
        private set

    init {
        obtenerVehiculos()
    }

    // Función que se conecta a la API en segundo plano
    fun obtenerVehiculos() {
        viewModelScope.launch {
            garageUiState = GarageUiState.Loading
            garageUiState = try {
                val lista = NetworkClient.retrofitInstance.getVehiculos()
                GarageUiState.Success(lista)
            } catch (e: IOException) {
                // Manejo de error si el servidor Ktor está apagado
                GarageUiState.Error("No se pudo conectar al servidor. Verifica que la API esté encendida.")
            } catch (e: Exception) {
                GarageUiState.Error("Error inesperado: ${e.localizedMessage}")
            }
        }
    }
}
package com.ruiz.mantenimientoapp

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ruiz.mantenimientoapp.ui.screens.*
import com.ruiz.mantenimientoapp.ui.viewmodel.GarageViewModel

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    // Instanciamos el ViewModel aquí para controlar el Garaje
    val garageViewModel: GarageViewModel = viewModel()

    NavHost(navController = navController, startDestination = "garage") {

        composable("garage") {
            GarageScreen(
                uiState = garageViewModel.garageUiState,
                onRetry = { garageViewModel.obtenerVehiculos() },
                onNavigateToDetalles = { vehiculoId ->
                    navController.navigate("detalle/$vehiculoId")
                }
            )
        }

        composable("detalle/{vehiculoId}") { backStackEntry ->
            val vehiculoId = backStackEntry.arguments?.getString("vehiculoId") ?: ""
            DetalleScreen(
                vehiculoId = vehiculoId,
                onNavigateToHistorial = { id ->
                    navController.navigate("historial/$id")
                }
            )
        }

        composable("historial/{vehiculoId}") { backStackEntry ->
            val vehiculoId = backStackEntry.arguments?.getString("vehiculoId") ?: ""
            HistorialScreen(
                vehiculoId = vehiculoId,
                onNavigateToAgregar = {
                    navController.navigate("agregar")
                }
            )
        }

        composable("agregar") {
            AgregarServicioScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
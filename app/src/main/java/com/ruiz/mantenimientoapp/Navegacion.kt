package com.ruiz.mantenimientoapp

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ruiz.mantenimientoapp.ui.screens.*
import com.ruiz.mantenimientoapp.ui.viewmodel.GarageViewModel
import com.ruiz.mantenimientoapp.ui.viewmodel.DetalleViewModel

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val garageViewModel: GarageViewModel = viewModel()
    val detalleViewModel: DetalleViewModel = viewModel()

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
                uiState = detalleViewModel.detalleUiState,
                onLoadData = { detalleViewModel.cargarDatosVehiculo(vehiculoId) },
                onNavigateToHistorial = { navController.navigate("historial/$vehiculoId") }
            )
        }

        composable("historial/{vehiculoId}") {
            HistorialScreen(
                uiState = detalleViewModel.detalleUiState,
                onNavigateToAgregar = { navController.navigate("agregar") }
            )
        }

        composable("agregar") {
            AgregarServicioScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
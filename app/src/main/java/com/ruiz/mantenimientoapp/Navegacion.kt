package com.ruiz.mantenimientoapp

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ruiz.mantenimientoapp.ui.screens.*

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "garage") {
        composable("garage") {
            GarageScreen(
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
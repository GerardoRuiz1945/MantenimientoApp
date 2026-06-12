package com.ruiz.mantenimientoapp.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ruiz.mantenimientoapp.data.network.VehiculoDto
import com.ruiz.mantenimientoapp.ui.viewmodel.GarageUiState

@Composable
fun GarageScreen(
    uiState: GarageUiState,
    onRetry: () -> Unit,
    onNavigateToDetalles: (String) -> Unit
) {
    Scaffold(
        topBar = {
            Box(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("Mi Garaje", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier.fillMaxSize().padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            when (uiState) {
                is GarageUiState.Loading -> { CircularProgressIndicator() }
                is GarageUiState.Error -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = uiState.mensaje, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(16.dp))
                        Button(onClick = onRetry) { Text("Reintentar") }
                    }
                }
                is GarageUiState.Success -> {
                    if (uiState.vehiculos.isEmpty()) {
                        Text("No hay vehículos registrados.")
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize().padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(uiState.vehiculos) { vehiculo ->
                                VehiculoCard(vehiculo = vehiculo, onClick = { onNavigateToDetalles(vehiculo.id) })
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun VehiculoCard(vehiculo: VehiculoDto, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "${vehiculo.marca} ${vehiculo.modelo}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = "Tipo: ${vehiculo.tipo}", style = MaterialTheme.typography.bodyMedium)
                Text(text = "${vehiculo.kilometrajeActual} km", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
fun DetalleScreen(vehiculoId: String, onNavigateToHistorial: (String) -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Pantalla 2: Detalles (ID: $vehiculoId)", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { onNavigateToHistorial(vehiculoId) }) {
            Text("Ver Historial de Servicios")
        }
    }
}

@Composable
fun HistorialScreen(vehiculoId: String, onNavigateToAgregar: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Pantalla 3: Historial", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onNavigateToAgregar) {
            Text("Registrar Nuevo Servicio")
        }
    }
}

@Composable
fun AgregarServicioScreen(onNavigateBack: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Pantalla 4: Nuevo Servicio", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onNavigateBack) {
            Text("Guardar y Regresar")
        }
    }
}
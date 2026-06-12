package com.ruiz.mantenimientoapp.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ruiz.mantenimientoapp.data.network.VehiculoDto
import com.ruiz.mantenimientoapp.ui.viewmodel.GarageUiState
import com.ruiz.mantenimientoapp.ui.viewmodel.DetalleUiState

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

// --- PANTALLA 2: DETALLES DEL VEHÍCULO ---
@Composable
fun DetalleScreen(
    vehiculoId: String,
    uiState: DetalleUiState,
    onLoadData: () -> Unit,
    onNavigateToHistorial: () -> Unit
) {
    // Forzamos la carga de datos cuando se abre la pantalla
    LaunchedEffect(vehiculoId) {
        onLoadData()
    }

    Scaffold(
        topBar = { Box(modifier = Modifier.fillMaxWidth().padding(16.dp)) { Text("Ficha Técnica", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold) } }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
            when (uiState) {
                is DetalleUiState.Loading -> CircularProgressIndicator()
                is DetalleUiState.Error -> Text(uiState.mensaje, color = MaterialTheme.colorScheme.error)
                is DetalleUiState.Success -> {
                    val v = uiState.vehiculo
                    Column(modifier = Modifier.fillMaxSize().padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("${v.marca} ${v.modelo}", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                                Text("Categoría: ${v.tipo}", style = MaterialTheme.typography.bodyLarge)
                            }
                        }

                        // Detalles específicos simulando manual técnico
                        Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text("Especificaciones de Referencia:", fontWeight = FontWeight.Bold)
                                Text("• Odómetro Actual: ${v.kilometrajeActual} km")
                                Text("• Estado General: Óptimo")
                            }
                        }

                        Spacer(modifier = Modifier.weight(1f))
                        Button(onClick = onNavigateToHistorial, modifier = Modifier.fillMaxWidth()) {
                            Text("Ver Historial de Mantenimientos (${uiState.mantenimientos.size})")
                        }
                    }
                }
            }
        }
    }
}

// --- PANTALLA 3: HISTORIAL DE SERVICIOS ---
@Composable
fun HistorialScreen(
    uiState: DetalleUiState,
    onNavigateToAgregar: () -> Unit
) {
    Scaffold(
        topBar = { Box(modifier = Modifier.fillMaxWidth().padding(16.dp)) { Text("Historial de Servicios", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold) } }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            when (uiState) {
                is DetalleUiState.Loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                is DetalleUiState.Error -> Text(uiState.mensaje, color = MaterialTheme.colorScheme.error, modifier = Modifier.align(Alignment.Center))
                is DetalleUiState.Success -> {
                    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                        if (uiState.mantenimientos.isEmpty()) {
                            Text("No hay servicios registrados para este vehículo.", modifier = Modifier.weight(1f))
                        } else {
                            LazyColumn(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                items(uiState.mantenimientos) { mtto ->
                                    Card(modifier = Modifier.fillMaxWidth(), border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)) {
                                        Column(modifier = Modifier.padding(16.dp)) {
                                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                                Text(mtto.tipoServicio, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                                                Text("$${mtto.costo}", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                                            }
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text("Fecha: ${mtto.fecha} | Kilometraje: ${mtto.kilometrajeEnServicio} km", style = MaterialTheme.typography.bodySmall)
                                        }
                                    }
                                }
                            }
                        }
                        Button(onClick = onNavigateToAgregar, modifier = Modifier.fillMaxWidth().padding(top = 16.dp)) {
                            Text("Registrar Nuevo Servicio")
                        }
                    }
                }
            }
        }
    }
}

// --- PANTALLA 4: AGREGAR SERVICIO ---
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
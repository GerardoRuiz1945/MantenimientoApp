package com.ruiz.mantenimientoapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ruiz.mantenimientoapp.data.network.VehiculoDto
import com.ruiz.mantenimientoapp.data.network.MantenimientoDto
import com.ruiz.mantenimientoapp.data.network.NetworkClient
import com.ruiz.mantenimientoapp.ui.viewmodel.GarageUiState
import com.ruiz.mantenimientoapp.ui.viewmodel.DetalleUiState
import kotlinx.coroutines.launch
import java.util.UUID

// --- PALETA DE COLORES AUTOMOTRIZ PREMIUM ---
val FondoGaráz = Color(0xFF121214)
val TarjetaGaráz = Color(0xFF1E1E22)
val AzulDeportivo = Color(0xFF007AFF)
val NaranjaRacing = Color(0xFFFF9500)
val TextoBlanco = Color(0xFFF5F5F7)
val TextoGris = Color(0xFF8E8E93)
val VerdeIndicador = Color(0xFF34C759)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GarageScreen(
    uiState: GarageUiState,
    onRetry: () -> Unit,
    onNavigateToDetalles: (String) -> Unit
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("MI GARAJE", fontWeight = FontWeight.Black, letterSpacing = 2.sp, color = TextoBlanco) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = FondoGaráz)
            )
        },
        containerColor = FondoGaráz
    ) { paddingValues ->
        Box(
            modifier = Modifier.fillMaxSize().padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            when (uiState) {
                is GarageUiState.Loading -> { CircularProgressIndicator(color = AzulDeportivo) }
                is GarageUiState.Error -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(24.dp)) {
                        Text(text = uiState.mensaje, color = Color(0xFFFF3B30), textAlign = TextAlign.Center, modifier = Modifier.padding(bottom = 16.dp))
                        Button(onClick = onRetry, colors = ButtonDefaults.buttonColors(containerColor = AzulDeportivo)) { Text("Reintentar Conexión") }
                    }
                }
                is GarageUiState.Success -> {
                    if (uiState.vehiculos.isEmpty()) {
                        Text("No hay vehículos en el garaje.", color = TextoGris)
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            item { Spacer(modifier = Modifier.height(8.dp)) }
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
    val esMoto = vehiculo.tipo.contains("Moto", ignoreCase = true)
    val colorAcento = if (esMoto) NaranjaRacing else AzulDeportivo

    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = TarjetaGaráz),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min)) {
            Box(
                modifier = Modifier.fillMaxHeight().width(6.dp).background(colorAcento)
            )

            Column(modifier = Modifier.padding(16.dp).weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = vehiculo.marca.uppercase(), style = MaterialTheme.typography.labelMedium, color = colorAcento, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                    Surface(
                        color = colorAcento.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(text = vehiculo.tipo, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), color = colorAcento, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                    }
                }

                Text(text = vehiculo.modelo, style = MaterialTheme.typography.titleLarge, color = TextoBlanco, fontWeight = FontWeight.Bold, modifier = Modifier.padding(vertical = 4.dp))

                HorizontalDivider(color = Color.White.copy(alpha = 0.05f), modifier = Modifier.padding(vertical = 8.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = "ODÓMETRO", style = MaterialTheme.typography.bodySmall, color = TextoGris, fontWeight = FontWeight.Bold)
                    Text(text = "${vehiculo.kilometrajeActual} KM", style = MaterialTheme.typography.bodyMedium, color = TextoBlanco, fontWeight = FontWeight.Black)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleScreen(
    vehiculoId: String,
    uiState: DetalleUiState,
    onLoadData: () -> Unit,
    onNavigateToHistorial: () -> Unit
) {
    LaunchedEffect(vehiculoId) {
        onLoadData()
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("FICHA TÉCNICA", fontWeight = FontWeight.Black, letterSpacing = 1.5.sp, color = TextoBlanco) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = FondoGaráz)
            )
        },
        containerColor = FondoGaráz
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
            when (uiState) {
                is DetalleUiState.Loading -> CircularProgressIndicator(color = AzulDeportivo)
                is DetalleUiState.Error -> Text(uiState.mensaje, color = Color(0xFFFF3B30))
                is DetalleUiState.Success -> {
                    val v = uiState.vehiculo
                    val esMoto = v.tipo.contains("Moto", ignoreCase = true)
                    val colorAcento = if (esMoto) NaranjaRacing else AzulDeportivo

                    Column(modifier = Modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = TarjetaGaráz)) {
                            Column(modifier = Modifier.padding(20.dp)) {
                                Text(v.marca.uppercase(), color = colorAcento, fontWeight = FontWeight.Black, letterSpacing = 1.sp, style = MaterialTheme.typography.labelLarge)
                                Text(v.modelo, style = MaterialTheme.typography.headlineLarge, color = TextoBlanco, fontWeight = FontWeight.Bold)
                            }
                        }

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F0F11))
                        ) {
                            Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("DISTANCIA TOTAL RECORRIDA", color = TextoGris, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = String.format("%,d KM", v.kilometrajeActual),
                                    style = MaterialTheme.typography.displaySmall,
                                    color = colorAcento,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 1.sp
                                )
                            }
                        }

                        Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = TarjetaGaráz)) {
                            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier.size(12.dp).background(VerdeIndicador, RoundedCornerShape(6.dp)))
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text("ESTADO DEL MOTOR", color = TextoGris, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                                    Text("Sistemas en condiciones óptimas", color = TextoBlanco, fontWeight = FontWeight.SemiBold)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        Button(
                            onClick = onNavigateToHistorial,
                            modifier = Modifier.fillMaxWidth().height(54.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = colorAcento)
                        ) {
                            Text("VER BITÁCORA DE SERVICIOS (${uiState.mantenimientos.size})", fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistorialScreen(
    uiState: DetalleUiState,
    onNavigateToAgregar: () -> Unit
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("HISTORIAL DE SERVICIOS", fontWeight = FontWeight.Black, letterSpacing = 1.sp, color = TextoBlanco) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = FondoGaráz)
            )
        },
        containerColor = FondoGaráz
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            when (uiState) {
                is DetalleUiState.Loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = AzulDeportivo)
                is DetalleUiState.Error -> Text(uiState.mensaje, color = Color(0xFFFF3B30), modifier = Modifier.align(Alignment.Center))
                is DetalleUiState.Success -> {
                    val esMoto = uiState.vehiculo.tipo.contains("Moto", ignoreCase = true)
                    val colorAcento = if (esMoto) NaranjaRacing else AzulDeportivo

                    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                        if (uiState.mantenimientos.isEmpty()) {
                            Text("No se registran órdenes de servicio anteriores.", color = TextoGris, modifier = Modifier.weight(1f).align(Alignment.CenterHorizontally).padding(top = 40.dp))
                        } else {
                            LazyColumn(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                items(uiState.mantenimientos) { mtto ->
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(14.dp),
                                        colors = CardDefaults.cardColors(containerColor = TarjetaGaráz)
                                    ) {
                                        Column(modifier = Modifier.padding(16.dp)) {
                                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                                Text(mtto.tipoServicio, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium, color = TextoBlanco, modifier = Modifier.weight(1f))
                                                Surface(
                                                    color = VerdeIndicador.copy(alpha = 0.15f),
                                                    shape = RoundedCornerShape(8.dp)
                                                ) {
                                                    Text("$${mtto.costo}", color = VerdeIndicador, fontWeight = FontWeight.Black, modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp), style = MaterialTheme.typography.bodyMedium)
                                                }
                                            }
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                                Text("Fecha: ${mtto.fecha}", style = MaterialTheme.typography.bodySmall, color = TextoGris)
                                                Text("${mtto.kilometrajeEnServicio} KM", style = MaterialTheme.typography.bodySmall, color = colorAcento, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        Button(
                            onClick = onNavigateToAgregar,
                            modifier = Modifier.fillMaxWidth().padding(top = 16.dp).height(54.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = colorAcento)
                        ) {
                            Text("REGISTRAR NUEVA ORDEN", fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgregarServicioScreen(
    vehiculoId: String,
    onNavigateBack: () -> Unit
) {
    var tipoServicio by remember { mutableStateOf("") }
    var kilometraje by remember { mutableStateOf("") }
    var costo by remember { mutableStateOf("") }
    var cargando by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("NUEVA ORDEN", fontWeight = FontWeight.Black, letterSpacing = 1.5.sp, color = TextoBlanco) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = FondoGaráz)
            )
        },
        containerColor = FondoGaráz
    ) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues).padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            OutlinedTextField(
                value = tipoServicio,
                onValueChange = { tipoServicio = it },
                label = { Text("Descripción del Servicio") },
                modifier = Modifier.fillMaxWidth(),
                enabled = !cargando,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AzulDeportivo,
                    unfocusedBorderColor = TarjetaGaráz,
                    focusedLabelColor = AzulDeportivo,
                    unfocusedLabelColor = TextoGris
                )
            )

            OutlinedTextField(
                value = kilometraje,
                onValueChange = { kilometraje = it },
                label = { Text("Odómetro al ingresar (KM)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                enabled = !cargando,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AzulDeportivo,
                    unfocusedBorderColor = TarjetaGaráz,
                    focusedLabelColor = AzulDeportivo,
                    unfocusedLabelColor = TextoGris
                )
            )

            OutlinedTextField(
                value = costo,
                onValueChange = { costo = it },
                label = { Text("Costo del Mantenimiento ($)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                enabled = !cargando,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AzulDeportivo,
                    unfocusedBorderColor = TarjetaGaráz,
                    focusedLabelColor = AzulDeportivo,
                    unfocusedLabelColor = TextoGris
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (cargando) {
                CircularProgressIndicator(color = AzulDeportivo)
            } else {
                Button(
                    onClick = {
                        if (tipoServicio.isNotBlank() && kilometraje.isNotBlank() && costo.isNotBlank()) {
                            cargando = true
                            coroutineScope.launch {
                                try {
                                    val nuevoServicio = MantenimientoDto(
                                        id = UUID.randomUUID().toString(),
                                        vehiculoId = vehiculoId,
                                        tipoServicio = tipoServicio,
                                        fecha = "2026-06-11",
                                        kilometrajeEnServicio = kilometraje.toIntOrNull() ?: 0,
                                        costo = costo.toDoubleOrNull() ?: 0.0
                                    )
                                    NetworkClient.retrofitInstance.agregarMantenimiento(nuevoServicio)
                                    onNavigateBack()
                                } catch (e: Exception) {
                                    cargando = false
                                }
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(54.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = VerdeIndicador),
                    enabled = tipoServicio.isNotBlank() && kilometraje.isNotBlank() && costo.isNotBlank()
                ) {
                    Text("GUARDAR EN BASE DE DATOS", fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                }
            }
        }
    }
}
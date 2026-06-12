package com.ruiz.mantenimientoapp.data.network

import kotlinx.serialization.Serializable
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

@Serializable
data class VehiculoDto(
    val id: String,
    val tipo: String,
    val marca: String,
    val modelo: String,
    val kilometrajeActual: Int
)

@Serializable
data class MantenimientoDto(
    val id: String,
    val vehiculoId: String,
    val tipoServicio: String,
    val fecha: String,
    val kilometrajeEnServicio: Int,
    val costo: Double
)

interface ApiService {
    @GET("api/vehiculos")
    suspend fun getVehiculos(): List<VehiculoDto>

    @GET("api/vehiculos/{id}")
    suspend fun getVehiculoPorId(@Path("id") id: String): VehiculoDto

    @GET("api/vehiculos/{id}/mantenimientos")
    suspend fun getHistorialMantenimiento(@Path("id") id: String): List<MantenimientoDto>

    // --- NUEVO: Enviar un nuevo servicio a la API ---
    @POST("api/mantenimientos")
    suspend fun agregarMantenimiento(@Body mantenimiento: MantenimientoDto): MantenimientoDto
}

object NetworkClient {
    private const val BASE_URL = "http://10.0.2.2:8080/"

    private val jsonConfig = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    val retrofitInstance: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(jsonConfig.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(ApiService::class.java)
    }
}
package com.ruiz.mantenimientoapp.data.network

import kotlinx.serialization.Serializable
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.http.GET
import retrofit2.http.Path

// 1. Modelos de datos idénticos a los de tu API para que coincidan perfectamente
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

// 2. La interfaz que define las peticiones HTTP que hará tu app
interface ApiService {
    @GET("api/vehiculos")
    suspend fun getVehiculos(): List<VehiculoDto>

    @GET("api/vehiculos/{id}")
    suspend fun getVehiculoPorId(@Path("id") id: String): VehiculoDto

    @GET("api/vehiculos/{id}/mantenimientos")
    suspend fun getHistorialMantenimiento(@Path("id") id: String): List<MantenimientoDto>
}

// 3. El cliente Retrofit (La antena de la app)
object NetworkClient {
    // NOTA: Si usas el emulador de Android Studio, "10.0.2.2" es la dirección para entrar al localhost de tu computadora.
    private const val BASE_URL = "http://10.0.2.2:8080/"

    private val jsonConfig = Json {
        ignoreUnknownKeys = true // Evita errores si la API manda datos extra
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
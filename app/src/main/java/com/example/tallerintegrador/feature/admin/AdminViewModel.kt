// app/src/main/java/com/example/tallerintegrador/feature/admin/AdminViewModel.kt
package com.example.tallerintegrador.feature.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tallerintegrador.data.local.TokenManager
import com.example.tallerintegrador.data.network.ApiService
import com.example.tallerintegrador.data.model.pelicula
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

// ===== MODELS =====

data class Usuario(
    val id: Int,
    val name: String,
    val email: String,
    val role: String = "user",
    val createdAt: String?,
    val favoritosCount: Int = 0
)

data class EstadisticasAdmin(
    val totalUsuarios: Int,
    val totalPeliculas: Int,
    val totalFavoritos: Int,
    val peliculaMasPopular: String?,
    val usuarioMasActivo: String?
)

data class LogActividad(
    val id: Int,
    val usuarioId: Int,
    val usuarioNombre: String,
    val accion: String,
    val detalles: String,
    val timestamp: String
)

// ===== VIEWMODEL =====

@HiltViewModel
class AdminViewModel @Inject constructor(
    private val apiService: ApiService,
    private val tokenManager: TokenManager
) : ViewModel() {

    // ===== ESTADOS =====

    private val _isAdmin = MutableStateFlow(false)
    val isAdmin: StateFlow<Boolean> = _isAdmin.asStateFlow()

    private val _estadisticas = MutableStateFlow<EstadisticasAdmin?>(null)
    val estadisticas: StateFlow<EstadisticasAdmin?> = _estadisticas.asStateFlow()

    private val _usuarios = MutableStateFlow<List<Usuario>>(emptyList())
    val usuarios: StateFlow<List<Usuario>> = _usuarios.asStateFlow()

    private val _peliculas = MutableStateFlow<List<pelicula>>(emptyList())
    val peliculas: StateFlow<List<pelicula>> = _peliculas.asStateFlow()

    private val _logs = MutableStateFlow<List<LogActividad>>(emptyList())
    val logs: StateFlow<List<LogActividad>> = _logs.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    // ===== EMAILS DE ADMINISTRADOR =====
    private val adminEmails = setOf(
        "admin@cinemaaguilas.com",
        "jcgarcia@uas.edu.mx",
        "administrador@uas.edu.mx",
        "fer@gmail.com",
        "cinemasaguilasuas@admin.com"
    )

    init {
        verificarAdmin()
    }

    // ===== VERIFICACIÓN DE ADMIN =====

    private fun verificarAdmin() {
        val userEmail = tokenManager.getUserEmail()
        _isAdmin.value = userEmail?.lowercase() in adminEmails
    }

    fun esAdmin(): Boolean = _isAdmin.value

    // ===== CARGAR DATOS =====

    fun cargarDashboard() {
        viewModelScope.launch {
            if (!esAdmin()) return@launch

            _isLoading.value = true
            _error.value = null

            // Cargar en paralelo
            launch { cargarEstadisticas() }
            launch { cargarUsuarios() }
            launch { cargarPeliculas() }
            launch { cargarLogs() }

            // El isLoading se puede manejar de forma más granular si es necesario,
            // pero por ahora lo quitamos al final de la carga inicial.
            _isLoading.value = false
        }
    }

    private suspend fun cargarEstadisticas() {
        try {
            val response = apiService.getAdminDashboard("Bearer ${tokenManager.getToken()}")
            _estadisticas.value = EstadisticasAdmin(
                totalUsuarios = response.totalUsuarios,
                totalPeliculas = response.totalPeliculas,
                totalFavoritos = response.totalFavoritos,
                peliculaMasPopular = response.peliculaMasPopular,
                usuarioMasActivo = response.usuarioMasActivo
            )
        } catch (e: Exception) {
            _error.value = "Error cargando estadísticas: endpoint no encontrado (404)."
        }
    }

    private suspend fun cargarUsuarios() {
        try {
            val usuarios = apiService.getAdminUsers("Bearer ${tokenManager.getToken()}")
            _usuarios.value = usuarios
        } catch (e: Exception) {
            _error.value = "Error cargando usuarios: ${e.message}"
        }
    }

    private suspend fun cargarPeliculas() {
        try {
            val peliculas = apiService.getAdminPeliculas("Bearer ${tokenManager.getToken()}")
            _peliculas.value = peliculas
        } catch (e: Exception) {
            _error.value = "Error cargando películas: ${e.message}"
        }
    }

    private suspend fun cargarLogs() {
        try {
            val logs = apiService.getAdminLogs("Bearer ${tokenManager.getToken()}")
            _logs.value = logs
        } catch (e: Exception) {
            _error.value = "Error cargando logs: ${e.message}"
        }
    }

    // ===== GESTIÓN DE USUARIOS =====

    fun eliminarUsuario(usuarioId: Int) {
        viewModelScope.launch {
            if (!esAdmin()) return@launch

            _isLoading.value = true
            try {
                apiService.deleteAdminUser(
                    authHeader = "Bearer ${tokenManager.getToken()}",
                    userId = usuarioId
                )

                // Actualizar lista local
                _usuarios.value = _usuarios.value.filter { it.id != usuarioId }

                // Recalcular estadísticas
                cargarEstadisticas()

            } catch (e: Exception) {
                _error.value = "Error al eliminar usuario: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun actualizarUsuario(usuarioId: Int, nuevoNombre: String, nuevoEmail: String) {
        viewModelScope.launch {
            if (!esAdmin()) return@launch

            _isLoading.value = true
            try {
                apiService.updateAdminUser(
                    authHeader = "Bearer ${tokenManager.getToken()}",
                    userId = usuarioId,
                    request = mapOf(
                        "name" to nuevoNombre,
                        "email" to nuevoEmail
                    )
                )

                // Actualizar lista local
                _usuarios.value = _usuarios.value.map { usuario ->
                    if (usuario.id == usuarioId) {
                        usuario.copy(name = nuevoNombre, email = nuevoEmail)
                    } else {
                        usuario
                    }
                }

            } catch (e: Exception) {
                _error.value = "Error al actualizar usuario: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // ===== GESTIÓN DE PELÍCULAS =====

    fun eliminarPelicula(peliculaId: Int) {
        viewModelScope.launch {
            if (!esAdmin()) return@launch

            _isLoading.value = true
            try {
                apiService.deleteAdminPelicula(
                    authHeader = "Bearer ${tokenManager.getToken()}",
                    peliculaId = peliculaId
                )

                // Actualizar lista local
                _peliculas.value = _peliculas.value.filter { it.id != peliculaId }

                // Recalcular estadísticas
                cargarEstadisticas()

            } catch (e: Exception) {
                _error.value = "Error al eliminar película: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // ===== UTILIDADES =====

    fun limpiarError() {
        _error.value = null
    }

    fun refresh() {
        cargarDashboard()
    }
}

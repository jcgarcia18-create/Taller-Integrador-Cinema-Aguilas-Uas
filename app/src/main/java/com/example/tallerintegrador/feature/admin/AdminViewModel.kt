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
        "fer@gmail.com"
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

            try {
                cargarUsuarios()
                cargarPeliculas()
                cargarLogs()
                calcularEstadisticas()
            } catch (e: Exception) {
                _error.value = "Error al cargar dashboard: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private suspend fun cargarUsuarios() {
        try {
            // MOCK DATA para desarrollo
            _usuarios.value = listOf(
                Usuario(1, "Juan Pérez", "juan@example.com", "2025-01-15", 12),
                Usuario(2, "María García", "maria@example.com", "2025-02-20", 8),
                Usuario(3, "Carlos López", "carlos@example.com", "2025-03-10", 15),
                Usuario(4, "Ana Martínez", "ana@example.com", "2025-04-05", 3)
            )
        } catch (e: Exception) {
            throw Exception("Error cargando usuarios: ${e.message}")
        }
    }

    private suspend fun cargarPeliculas() {
        try {
            val peliculas = apiService.getPeliculas()
            _peliculas.value = peliculas
        } catch (e: Exception) {
            throw Exception("Error cargando películas: ${e.message}")
        }
    }

    private suspend fun cargarLogs() {
        try {
            // MOCK DATA para desarrollo
            _logs.value = listOf(
                LogActividad(1, 2, "María García", "Agregar Favorito", "Inception", "2025-11-27 10:30"),
                LogActividad(2, 3, "Carlos López", "Login", "Inicio de sesión exitoso", "2025-11-27 09:15"),
                LogActividad(3, 1, "Juan Pérez", "Eliminar Favorito", "The Matrix", "2025-11-26 18:45"),
                LogActividad(4, 4, "Ana Martínez", "Registro", "Cuenta creada", "2025-11-26 14:20")
            )
        } catch (e: Exception) {
            throw Exception("Error cargando logs: ${e.message}")
        }
    }

    private fun calcularEstadisticas() {
        val totalUsuarios = _usuarios.value.size
        val totalPeliculas = _peliculas.value.size
        val totalFavoritos = _usuarios.value.sumOf { it.favoritosCount }

        val peliculaMasPopular = _peliculas.value.maxByOrNull { pelicula ->
            _usuarios.value.count { it.favoritosCount > 0 }
        }?.title

        val usuarioMasActivo = _usuarios.value.maxByOrNull { it.favoritosCount }?.name

        _estadisticas.value = EstadisticasAdmin(
            totalUsuarios = totalUsuarios,
            totalPeliculas = totalPeliculas,
            totalFavoritos = totalFavoritos,
            peliculaMasPopular = peliculaMasPopular,
            usuarioMasActivo = usuarioMasActivo
        )
    }

    // ===== GESTIÓN DE USUARIOS =====

    fun eliminarUsuario(usuarioId: Int) {
        viewModelScope.launch {
            if (!esAdmin()) return@launch

            _isLoading.value = true
            try {
                _usuarios.value = _usuarios.value.filter { it.id != usuarioId }
                calcularEstadisticas()
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
                _peliculas.value = _peliculas.value.filter { it.id != peliculaId }
                calcularEstadisticas()
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
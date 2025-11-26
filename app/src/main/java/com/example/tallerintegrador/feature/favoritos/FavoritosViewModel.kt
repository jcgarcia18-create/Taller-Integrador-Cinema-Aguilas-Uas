package com.example.tallerintegrador.feature.favoritos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tallerintegrador.data.local.TokenManager
import com.example.tallerintegrador.data.model.pelicula
import com.example.tallerintegrador.data.repository.FavoritosRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoritosViewModel @Inject constructor(
    private val repository: FavoritosRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _favoritos = MutableStateFlow<List<pelicula>>(emptyList())
    val favoritos: StateFlow<List<pelicula>> = _favoritos.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    // ✅ Flow reactivo de IDs (ya lo tienes)
    val favoritosIds: StateFlow<Set<Int>> = repository.getFavoritosIdsFlow()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptySet()
        )

    // ✅ NUEVO: Auto-recargar cuando cambian los IDs
    init {
        viewModelScope.launch {
            favoritosIds.collect { ids ->
                // Cuando cambian los IDs, recarga la lista completa
                if (ids.isNotEmpty()) {
                    cargarFavoritosInternamente()
                }
            }
        }
    }

    fun cargarFavoritos() {
        val token = getTokenOrNull() ?: return

        viewModelScope.launch {
            cargarFavoritosInternamente()
        }
    }

    // ✅ NUEVO: Método interno privado
    private suspend fun cargarFavoritosInternamente() {
        val token = getTokenOrNull() ?: return

        _isLoading.value = true
        _error.value = null

        try {
            val peliculas = repository.getFavoritos(token)
            _favoritos.value = peliculas
        } catch (e: Exception) {
            _error.value = "Error al cargar favoritos: ${e.message}"
        } finally {
            _isLoading.value = false
        }
    }

    fun toggleFavorito(peliculaId: Int, currentlyFavorite: Boolean) {
        val token = getTokenOrNull() ?: return

        viewModelScope.launch {
            try {
                if (currentlyFavorite) {
                    repository.removeFavorito(token, peliculaId)
                } else {
                    repository.addFavorito(token, peliculaId)
                }

                // ✅ Ya no necesitas recargar manualmente
                // El init {} detecta el cambio automáticamente

            } catch (e: Exception) {
                _error.value = "Error al actualizar favorito: ${e.message}"
            }
        }
    }

    suspend fun esFavorito(peliculaId: Int): Boolean {
        return repository.isFavorito(peliculaId)
    }

    fun esFavoritoFlow(peliculaId: Int): Flow<Boolean> {
        return repository.isFavoritoFlow(peliculaId)
    }

    fun clearCache() {
        viewModelScope.launch {
            repository.clearCache()
            _favoritos.value = emptyList()
        }
    }

    private fun getTokenOrNull(): String? = tokenManager.getToken()
}
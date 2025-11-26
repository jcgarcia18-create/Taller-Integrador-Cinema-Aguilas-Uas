package com.example.tallerintegrador.feature.peliculas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tallerintegrador.data.model.pelicula
import com.example.tallerintegrador.data.repository.PeliculaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class PeliculaDetailState {
    object Idle : PeliculaDetailState()
    object Loading : PeliculaDetailState()
    data class Success(val pelicula: pelicula) : PeliculaDetailState()
    data class Error(val message: String) : PeliculaDetailState()
}

@HiltViewModel
class PeliculaViewModel @Inject constructor(
    private val repository: PeliculaRepository
) : ViewModel() {

    private val _peliculas = MutableStateFlow<List<pelicula>>(emptyList())
    val peliculas: StateFlow<List<pelicula>> = _peliculas.asStateFlow()

    private val _isLoadingList = MutableStateFlow(false)
    val isLoadingList: StateFlow<Boolean> = _isLoadingList.asStateFlow()

    private val _peliculaDetail = MutableStateFlow<PeliculaDetailState>(PeliculaDetailState.Idle)
    val peliculaDetail: StateFlow<PeliculaDetailState> = _peliculaDetail.asStateFlow()

    fun getPeliculas(forceRefresh: Boolean = false) {
        if (_peliculas.value.isNotEmpty() && !forceRefresh) {
            return
        }

        viewModelScope.launch {
            _isLoadingList.value = true

            try {
                val peliculasList = repository.getPeliculas(forceRefresh)
                _peliculas.value = peliculasList
            } catch (e: Exception) {
                _peliculas.value = emptyList()
            } finally {
                _isLoadingList.value = false
            }
        }
    }

    fun getPeliculaById(peliculaId: Int, forceRefresh: Boolean = false) {
        viewModelScope.launch {
            _peliculaDetail.value = PeliculaDetailState.Loading

            try {
                val pelicula = repository.getPeliculaById(peliculaId, forceRefresh)
                _peliculaDetail.value = PeliculaDetailState.Success(pelicula)
            } catch (e: Exception) {
                _peliculaDetail.value = PeliculaDetailState.Error(
                    e.message ?: "Error al cargar los detalles de la película"
                )
            }
        }
    }

    fun getPeliculaByIdWithFallback(peliculaId: Int) {
        viewModelScope.launch {
            val peliculaLocal = _peliculas.value.find { it.id == peliculaId }

            if (peliculaLocal != null) {
                _peliculaDetail.value = PeliculaDetailState.Success(peliculaLocal)
            } else {
                getPeliculaById(peliculaId)
            }
        }
    }

    fun clearPeliculaDetail() {
        _peliculaDetail.value = PeliculaDetailState.Idle
    }

    fun clearCache() {
        viewModelScope.launch {
            repository.clearCache()
            _peliculas.value = emptyList()
        }
    }

    fun refresh() {
        getPeliculas(forceRefresh = true)
    }
}
package com.example.tallerintegrador.data.repository

import com.example.tallerintegrador.data.local.cache.CacheManager
import com.example.tallerintegrador.data.model.pelicula
import com.example.tallerintegrador.data.network.ApiService
import kotlinx.coroutines.flow.Flow

/**
 * Repository de Favoritos con cache local reactivo
 */
class FavoritosRepository(
    private val apiService: ApiService,
    private val cacheManager: CacheManager
) {

    /**
     * Obtiene favoritos del servidor y sincroniza el cache
     */
    suspend fun getFavoritos(token: String): List<pelicula> {
        val listaPeliculas = apiService.getFavoritos("Bearer $token")
        val favoritosIds = listaPeliculas.map { it.id }
        cacheManager.syncFavoritos(favoritosIds)
        return listaPeliculas
    }

    /**
     * Agrega a favoritos (servidor + cache optimista)
     */
    suspend fun addFavorito(token: String, peliculaId: Int) {
        // 1. Actualiza el cache inmediatamente (optimistic update)
        cacheManager.addFavoritoToCache(peliculaId)

        try {
            // 2. Sincroniza con el servidor
            apiService.addFavorito("Bearer $token", peliculaId)
        } catch (e: Exception) {
            // Si falla, revierte el cache
            cacheManager.removeFavoritoFromCache(peliculaId)
            throw e
        }
    }

    /**
     * ✅ Elimina de favoritos (servidor + cache optimista)
     */
    suspend fun removeFavorito(token: String, peliculaId: Int) {
        // 1. Actualiza el cache inmediatamente (optimistic update)
        cacheManager.removeFavoritoFromCache(peliculaId)

        try {
            // 2. Sincroniza con el servidor
            apiService.removeFavorito("Bearer $token", peliculaId)
        } catch (e: Exception) {
            // Si falla, revierte el cache
            cacheManager.addFavoritoToCache(peliculaId)
            throw e
        }
    }

    /**
     * ✅ Verifica si es favorito desde el CACHE (sin red)
     */
    suspend fun isFavorito(peliculaId: Int): Boolean {
        return cacheManager.isFavorito(peliculaId)
    }

    /**
     * ✅ Flow reactivo de estado de favorito
     */
    fun isFavoritoFlow(peliculaId: Int): Flow<Boolean> {
        return cacheManager.isFavoritoFlow(peliculaId)
    }

    /**
     * ✅ Flow reactivo de todos los IDs de favoritos
     */
    fun getFavoritosIdsFlow(): Flow<Set<Int>> {
        return cacheManager.getFavoritosIdsFlow()
    }

    /**
     * Limpia el cache de favoritos
     */
    suspend fun clearCache() {
        cacheManager.clearFavoritosCache()
    }
}
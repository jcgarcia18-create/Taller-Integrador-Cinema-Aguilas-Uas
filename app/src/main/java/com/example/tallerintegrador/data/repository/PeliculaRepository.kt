package com.example.tallerintegrador.data.repository

import android.content.Context
import com.example.tallerintegrador.data.local.cache.CacheManager
import com.example.tallerintegrador.data.model.pelicula
import com.example.tallerintegrador.data.network.ApiService

/**
 * ✅ Repository con estrategia CACHE-FIRST
 *
 * 1. Intenta obtener datos del cache
 * 2. Si no hay cache o está expirado, pide a la red
 * 3. Actualiza el cache con datos frescos
 */
class PeliculaRepository(
    private val apiService: ApiService,
    private val cacheManager: CacheManager
) {

    /**
     * ✅ Obtiene todas las películas con cache inteligente
     *
     * - Primero intenta del cache (si es válido)
     * - Si no hay cache, pide a la red
     * - Actualiza el cache automáticamente
     */
    suspend fun getPeliculas(forceRefresh: Boolean = false): List<pelicula> {
        // Si se fuerza refresh, ignora el cache
        if (!forceRefresh) {
            val cachedPeliculas = cacheManager.getCachedPeliculas()
            if (cachedPeliculas != null) {
                return cachedPeliculas
            }
        }

        // Pide a la red
        val peliculas = apiService.getPeliculas()

        // Actualiza el cache
        cacheManager.cachePeliculas(peliculas)

        return peliculas
    }

    /**
     * ✅ Obtiene una película específica por ID con cache
     *
     * - Primero busca en cache
     * - Si no está, pide a la API
     * - Actualiza el cache individual
     */
    suspend fun getPeliculaById(peliculaId: Int, forceRefresh: Boolean = false): pelicula {
        // Si se fuerza refresh, ignora el cache
        if (!forceRefresh) {
            val cachedPelicula = cacheManager.getCachedPelicula(peliculaId)
            if (cachedPelicula != null) {
                return cachedPelicula
            }
        }

        // Pide a la red
        val pelicula = apiService.getPeliculaById(peliculaId)

        // Actualiza el cache individual
        cacheManager.cachePelicula(pelicula)

        return pelicula
    }

    /**
     * Limpia el cache (útil para logout o refresh manual)
     */
    suspend fun clearCache() {
        cacheManager.clearPeliculasCache()
    }
}
package com.example.tallerintegrador.di

import com.example.tallerintegrador.data.local.cache.CacheManager
import com.example.tallerintegrador.data.network.ApiService
import com.example.tallerintegrador.data.repository.FavoritosRepository
import com.example.tallerintegrador.data.repository.PeliculaRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun providePeliculaRepository(
        apiService: ApiService,
        cacheManager: CacheManager
    ): PeliculaRepository {
        return PeliculaRepository(apiService, cacheManager)
    }

    @Provides
    @Singleton
    fun provideFavoritosRepository(
        apiService: ApiService,
        cacheManager: CacheManager
    ): FavoritosRepository {
        return FavoritosRepository(apiService, cacheManager)
    }
}
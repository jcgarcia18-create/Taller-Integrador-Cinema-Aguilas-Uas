package com.example.tallerintegrador.di

import android.content.Context
import androidx.room.Room
import com.example.tallerintegrador.data.local.cache.AppDatabase
import com.example.tallerintegrador.data.local.cache.PeliculaCacheDao
import com.example.tallerintegrador.data.local.cache.FavoritoCacheDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "cinema_aguilas_database"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun providePeliculaCacheDao(database: AppDatabase): PeliculaCacheDao {
        return database.peliculaCacheDao()
    }

    @Provides
    @Singleton
    fun provideFavoritoCacheDao(database: AppDatabase): FavoritoCacheDao {
        return database.favoritoCacheDao()
    }
}
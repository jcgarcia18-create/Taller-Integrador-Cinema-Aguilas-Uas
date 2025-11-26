package com.example.tallerintegrador.data.local.cache

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        PeliculaCacheEntity::class,
        FavoritoCacheEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun peliculaCacheDao(): PeliculaCacheDao
    abstract fun favoritoCacheDao(): FavoritoCacheDao

}
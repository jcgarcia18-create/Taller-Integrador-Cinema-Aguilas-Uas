package com.example.tallerintegrador.data.model

import com.google.gson.annotations.SerializedName

/**
 * Respuesta al agregar un favorito.
 * Estructura: { "message": "Agregado a favoritos", "added": true }
 */
data class AddFavoritoResponse(
    @SerializedName("message")
    val message: String,

    @SerializedName("added")
    val added: Boolean
)

/**
 * Respuesta al eliminar un favorito.
 * Estructura: { "message": "Eliminado de favoritos", "removed": true }
 */
data class RemoveFavoritoResponse(
    @SerializedName("message")
    val message: String,

    @SerializedName("removed")
    val removed: Boolean
)

/**
 * Respuesta al verificar si una película es favorita.
 * Estructura: { "is_favorite": true, "pelicula_id": 4 }
 */
data class CheckFavoritoResponse(
    @SerializedName("is_favorite")
    val isFavorite: Boolean,

    @SerializedName("pelicula_id")
    val peliculaId: Int
)
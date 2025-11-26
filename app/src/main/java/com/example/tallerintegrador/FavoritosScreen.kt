package com.example.tallerintegrador

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.tallerintegrador.data.model.pelicula
import com.example.tallerintegrador.feature.favoritos.FavoritosViewModel
import com.example.tallerintegrador.ui.theme.DarkBlue
import com.example.tallerintegrador.ui.theme.Yellow
import com.example.tallerintegrador.feature.peliculas.PeliculaViewModel

// ✅ CAMBIOS CLAVE EN FavoritosScreen.kt


@Composable
fun FavoritosScreen(
    peliculaViewModel: PeliculaViewModel?,
    navController: NavController?,
    favoritosViewModel: FavoritosViewModel
) {
    val favoritos by favoritosViewModel.favoritos.collectAsState()
    val isLoading by favoritosViewModel.isLoading.collectAsState()
    val error by favoritosViewModel.error.collectAsState()

    // ✅ CAMBIO: Solo carga si la lista está vacía
    LaunchedEffect(Unit) {
        if (favoritos.isEmpty()) {
            favoritosViewModel.cargarFavoritos()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBlue)
    ) {
        when {
            isLoading && favoritos.isEmpty() -> {
                // ✅ NUEVO: Indicador de carga
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(color = Yellow)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Cargando favoritos...",
                        color = Color.White,
                        fontSize = 16.sp
                    )
                }
            }

            error != null -> {
                // ✅ NUEVO: Manejo de errores
                Column(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Filled.ErrorOutline,
                        contentDescription = "Error",
                        tint = Color.Red,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Error al cargar favoritos",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = error ?: "Error desconocido",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = { favoritosViewModel.cargarFavoritos() },
                        colors = ButtonDefaults.buttonColors(containerColor = Yellow)
                    ) {
                        Text("Reintentar", color = DarkBlue)
                    }
                }
            }

            favoritos.isEmpty() -> {
                Text(
                    text = "Todavía no tienes películas favoritas",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 16.sp,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(favoritos) { peli ->
                        FavoritoItem(
                            pelicula = peli,
                            onClick = {
                                navController?.navigate("detalle_pelicula/${peli.id}")
                            },
                            onRemove = {
                                favoritosViewModel.toggleFavorito(peli.id, true)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FavoritoItem(
    pelicula: pelicula,
    onClick: () -> Unit,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = DarkBlue.copy(alpha = 0.5f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = pelicula.posterUrl,
                contentDescription = pelicula.title,
                modifier = Modifier
                    .size(90.dp)
                    .aspectRatio(2f / 3f)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = pelicula.title,
                    color = Yellow,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = pelicula.genre,
                    color = Color.White.copy(alpha = 0.7f),
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${pelicula.durationMinutes} min",
                    color = Color.White.copy(alpha = 0.6f),
                    style = MaterialTheme.typography.bodySmall
                )
            }

            IconButton(
                onClick = onRemove,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = "Eliminar de favoritos",
                    tint = Color.Red.copy(alpha = 0.8f)
                )
            }
        }
    }
}
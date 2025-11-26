package com.example.tallerintegrador

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.tallerintegrador.data.model.pelicula
import com.example.tallerintegrador.feature.favoritos.FavoritosViewModel
import com.example.tallerintegrador.feature.peliculas.PeliculaViewModel
import com.example.tallerintegrador.ui.theme.DarkBlue
import com.example.tallerintegrador.ui.theme.Yellow

/**
 * ✅ NUEVA PANTALLA: Muestra películas filtradas por género
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PeliculasPorGeneroScreen(
    genero: String,
    peliculaViewModel: PeliculaViewModel,
    favoritosViewModel: FavoritosViewModel,
    navController: NavController
) {
    val todasPeliculas by peliculaViewModel.peliculas.collectAsState()
    val isLoading by peliculaViewModel.isLoadingList.collectAsState()
    val favoritosIds by favoritosViewModel.favoritosIds.collectAsState()

    // Filtrar películas por género
    val peliculasFiltradas = remember(todasPeliculas, genero) {
        todasPeliculas.filter { pelicula ->
            pelicula.genre.split(',').any { it.trim().equals(genero, ignoreCase = true) }
        }
    }

    // Cargar películas si no hay datos
    LaunchedEffect(Unit) {
        if (todasPeliculas.isEmpty()) {
            peliculaViewModel.getPeliculas()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = genero,
                            color = Yellow,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${peliculasFiltradas.size} películas",
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 14.sp
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkBlue,
                    navigationIconContentColor = Color.White
                )
            )
        },
        containerColor = DarkBlue
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                isLoading && todasPeliculas.isEmpty() -> {
                    // Indicador de carga
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(color = Yellow)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Cargando películas...",
                            color = Color.White,
                            fontSize = 16.sp
                        )
                    }
                }

                peliculasFiltradas.isEmpty() -> {
                    // No hay películas de este género
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "😔",
                            fontSize = 64.sp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No hay películas de $genero",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Intenta con otro género",
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 14.sp
                        )
                    }
                }

                else -> {
                    // Grid de películas
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(peliculasFiltradas) { pelicula ->
                            MovieCard(
                                pelicula = pelicula,
                                isFavorite = favoritosIds.contains(pelicula.id),
                                onClick = {
                                    navController.navigate("detalle_pelicula/${pelicula.id}")
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
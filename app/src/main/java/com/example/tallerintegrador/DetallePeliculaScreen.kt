package com.example.tallerintegrador

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.tallerintegrador.feature.favoritos.FavoritosViewModel
import com.example.tallerintegrador.feature.peliculas.PeliculaViewModel
import com.example.tallerintegrador.feature.peliculas.PeliculaDetailState
import com.example.tallerintegrador.ui.theme.DarkBlue
import com.example.tallerintegrador.ui.theme.Yellow
import androidx.compose.ui.layout.ContentScale
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri

/**
 * ✅ ACTUALIZADO: Ahora recibe FavoritosViewModel como parámetro
 * Ya no crea una nueva instancia internamente
 */
// ✅ CAMBIOS CLAVE EN DetallePeliculaScreen.kt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetallePeliculaScreen(
    peliculaId: Int,
    viewModel: PeliculaViewModel,
    navController: NavController,
    favoritosViewModel: FavoritosViewModel
) {
    val context = LocalContext.current
    val peliculaDetailState by viewModel.peliculaDetail.collectAsState()

    // ✅ CAMBIO: Usa Flow reactivo en lugar de suspend function
    val isFavorite by favoritosViewModel.esFavoritoFlow(peliculaId)
        .collectAsState(initial = false)

    // Carga automática de la película
    LaunchedEffect(peliculaId) {
        viewModel.getPeliculaByIdWithFallback(peliculaId)
    }

    // Limpieza cuando se sale
    DisposableEffect(Unit) {
        onDispose {
            viewModel.clearPeliculaDetail()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    navigationIconContentColor = Color.White,
                    actionIconContentColor = Color.White
                ),
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                }
            )
        },
        containerColor = DarkBlue
    ) { padding ->
        when (val state = peliculaDetailState) {
            is PeliculaDetailState.Idle -> {
                // Estado inicial
            }

            is PeliculaDetailState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(color = Yellow)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Cargando detalles...",
                            color = Color.White,
                            fontSize = 16.sp
                        )
                    }
                }
            }

            is PeliculaDetailState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ErrorOutline,
                            contentDescription = "Error",
                            tint = Color.Red,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Error al cargar la película",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = state.message,
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Button(
                            onClick = { viewModel.getPeliculaById(peliculaId, forceRefresh = true) },
                            colors = ButtonDefaults.buttonColors(containerColor = Yellow)
                        ) {
                            Text("Reintentar", color = DarkBlue)
                        }
                    }
                }
            }

            is PeliculaDetailState.Success -> {
                val pelicula = state.pelicula

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(padding)
                ) {
                    // Póster con gradiente
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(400.dp)
                    ) {
                        var isLoading by remember { mutableStateOf(true) }
                        var hasError by remember { mutableStateOf(false) }

                        AsyncImage(
                            model = pelicula.posterUrl,
                            contentDescription = pelicula.title,
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Gray.copy(alpha = 0.3f)),
                            contentScale = ContentScale.Crop,
                            alignment = Alignment.Center,
                            onLoading = { isLoading = true },
                            onSuccess = {
                                isLoading = false
                                hasError = false
                            },
                            onError = {
                                isLoading = false
                                hasError = true
                            }
                        )

                        if (isLoading && !hasError) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(
                                    color = Yellow,
                                    modifier = Modifier.size(50.dp)
                                )
                            }
                        }

                        if (hasError) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color.Gray.copy(alpha = 0.5f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.ErrorOutline,
                                    contentDescription = "Error",
                                    tint = Color.White,
                                    modifier = Modifier.size(80.dp)
                                )
                            }
                        }

                        // Gradiente
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            Color.Transparent,
                                            DarkBlue.copy(alpha = 0.7f),
                                            DarkBlue
                                        ),
                                        startY = 200f
                                    )
                                )
                        )

                        // Botones de acción
                        Row(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // ✅ CAMBIO: El botón se actualiza automáticamente con el Flow
                            IconButton(
                                onClick = {
                                    favoritosViewModel.toggleFavorito(pelicula.id, isFavorite)
                                },
                                modifier = Modifier
                                    .background(
                                        color = DarkBlue.copy(alpha = 0.7f),
                                        shape = RoundedCornerShape(50)
                                    )
                            ) {
                                Icon(
                                    imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                                    contentDescription = "Favorito",
                                    tint = if (isFavorite) Color.Red else Color.White
                                )
                            }

                            IconButton(
                                onClick = { /* Compartir */ },
                                modifier = Modifier
                                    .background(
                                        color = DarkBlue.copy(alpha = 0.7f),
                                        shape = RoundedCornerShape(50)
                                    )
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Share,
                                    contentDescription = "Compartir",
                                    tint = Color.White
                                )
                            }
                        }
                    }

                    // Información de la película
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    ) {
                        Text(
                            text = pelicula.title,
                            color = Yellow,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            InfoChip(text = pelicula.genre)
                            InfoChip(text = "${pelicula.durationMinutes} min")
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Text(
                            text = "Sinopsis",
                            color = Yellow,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = pelicula.description,
                            color = Color.White,
                            fontSize = 16.sp,
                            lineHeight = 24.sp
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // Botón de tráiler
                        if (pelicula.videoUrl.isNotBlank() &&
                            pelicula.videoUrl != "url" &&
                            (pelicula.videoUrl.startsWith("http://") ||
                                    pelicula.videoUrl.startsWith("https://"))) {

                            Text(
                                text = "Tráiler",
                                color = Yellow,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Button(
                                onClick = {
                                    try {
                                        val customTabsIntent = CustomTabsIntent.Builder().build()
                                        customTabsIntent.launchUrl(context, pelicula.videoUrl.toUri())
                                    } catch (e: Exception) {
                                        Log.e("DetallePelicula", "Error abriendo URL: ${e.message}")
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Yellow),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.PlayArrow,
                                    contentDescription = "Ver tráiler",
                                    tint = DarkBlue
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Ver tráiler",
                                    color = DarkBlue,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))
                        }

                        // Botón de reproducir
                        Button(
                            onClick = { /* Reproducir película */ },
                            colors = ButtonDefaults.buttonColors(containerColor = Yellow),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.PlayArrow,
                                contentDescription = "Reproducir",
                                tint = DarkBlue,
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Reproducir película",
                                color = DarkBlue,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(32.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun InfoChip(text: String) {
    Surface(
        color = Yellow.copy(alpha = 0.2f),
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = text,
            color = Yellow,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}
package com.example.tallerintegrador

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.tallerintegrador.data.model.pelicula
import com.example.tallerintegrador.feature.peliculas.PeliculaViewModel
import com.example.tallerintegrador.ui.theme.DarkBlue
import com.example.tallerintegrador.ui.theme.Yellow
import com.example.tallerintegrador.feature.favoritos.FavoritosViewModel
import com.example.tallerintegrador.auth.AuthViewModel

@Composable
fun HomeScreen(
    viewModel: PeliculaViewModel,
    navController: NavController? = null,
    authViewModel: AuthViewModel,
    favoritosViewModel: FavoritosViewModel
) {
    val peliculas by viewModel.peliculas.collectAsState()
    val isLoading by viewModel.isLoadingList.collectAsState()
    val favoritosIds by favoritosViewModel.favoritosIds.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        if (peliculas.isEmpty()) {
            viewModel.getPeliculas()
        }
    }

    LaunchedEffect(Unit) {
        if (favoritosIds.isEmpty()) {
            favoritosViewModel.cargarFavoritos()
        }
    }

    val peliculasPorGenero = remember(peliculas) {
        val map = mutableMapOf<String, MutableList<pelicula>>()
        peliculas.forEach { pelicula ->
            (pelicula.genre ?: "").split(',').forEach { genero ->
                val trimmedGenero = genero.trim()
                if (trimmedGenero.isNotEmpty()) {
                    map.getOrPut(trimmedGenero) { mutableListOf() }.add(pelicula)
                }
            }
        }
        map
    }

    HomeScreenContent(
        peliculasPorGenero = peliculasPorGenero,
        selectedTab = selectedTab,
        onTabSelected = { selectedTab = it },
        viewModel = viewModel,
        navController = navController,
        authViewModel = authViewModel,
        favoritosViewModel = favoritosViewModel,
        isLoading = isLoading,
        favoritosIds = favoritosIds
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenContent(
    peliculasPorGenero: Map<String, List<pelicula>>,
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    viewModel: PeliculaViewModel? = null,
    navController: NavController? = null,
    authViewModel: AuthViewModel? = null,
    favoritosViewModel: FavoritosViewModel? = null,
    isLoading: Boolean = false,
    favoritosIds: Set<Int> = emptySet()
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("CinemasAguilasUas", color = Yellow) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkBlue,
                    titleContentColor = Yellow,
                    actionIconContentColor = Color.White
                ),
                actions = {
                    TextButton(onClick = {
                        onTabSelected(4)
                    }) {
                        Text("Mi perfil", color = Yellow)
                    }
                    TextButton(onClick = {
                        authViewModel?.logout()
                        viewModel?.clearCache()
                        favoritosViewModel?.clearCache()
                        navController?.navigate("welcome") {
                            popUpTo(0) { inclusive = true }
                        }
                    }) {
                        Text("Cerrar sesión", color = Color.White)
                    }
                }
            )
        },
        bottomBar = {
            BottomNavigationBar(
                selectedTab = selectedTab,
                onTabSelected = onTabSelected
            )
        },
        containerColor = DarkBlue
    ) { padding ->
        when (selectedTab) {
            0 -> {
                if (isLoading && peliculasPorGenero.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Yellow)
                    }
                } else {
                    LazyColumn(modifier = Modifier.padding(padding)) {
                        peliculasPorGenero.forEach { (genero, peliculasDelGenero) ->
                            item {
                                Text(
                                    text = genero,
                                    color = Yellow,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(start = 16.dp, top = 16.dp, end = 16.dp)
                                )
                            }
                            item {
                                LazyRow(
                                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    items(peliculasDelGenero) { pelicula ->
                                        MovieCard(
                                            pelicula = pelicula,
                                            isFavorite = favoritosIds.contains(pelicula.id),
                                            onClick = {
                                                navController?.navigate("detalle_pelicula/${pelicula.id}")
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
            1 -> {
                Box(modifier = Modifier.padding(padding)) {
                    CategoriasScreen(navController = navController)
                }
            }
            2 -> {
                Box(modifier = Modifier.padding(padding)) {
                    viewModel?.let {
                        BusquedaScreen(
                            viewModel = it,
                            navController = navController
                        )
                    }
                }
            }
            3 -> {
                Box(modifier = Modifier.padding(paddingValues = padding)) {
                    favoritosViewModel?.let { favsVM ->
                        FavoritosScreen(
                            peliculaViewModel = viewModel,
                            navController = navController,
                            favoritosViewModel = favsVM
                        )
                    }
                }
            }
            4 -> {
                Box(modifier = Modifier.padding(padding)) {
                    authViewModel?.let { auth ->
                        favoritosViewModel?.let { favs ->
                            PerfilScreen(
                                navController = navController,
                                authViewModel = auth,
                                favoritosViewModel = favs
                            )
                        }
                    }
                }
            }
        }
    }
}
@Composable
fun BottomNavigationBar(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    NavigationBar(
        containerColor = DarkBlue,
        contentColor = Yellow,
        tonalElevation = 8.dp
    ) {
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Home, contentDescription = "Inicio") },
            label = { Text("Inicio", fontSize = 11.sp) },
            selected = selectedTab == 0,
            onClick = { onTabSelected(0) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Yellow,
                unselectedIconColor = Color.White.copy(alpha = 0.6f),
                selectedTextColor = Yellow,
                unselectedTextColor = Color.White.copy(alpha = 0.6f),
                indicatorColor = DarkBlue.copy(alpha = 0.2f)
            )
        )
        NavigationBarItem(
            icon = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = "Categorías") },
            label = { Text("Categorías", fontSize = 11.sp) },
            selected = selectedTab == 1,
            onClick = { onTabSelected(1) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Yellow,
                unselectedIconColor = Color.White.copy(alpha = 0.6f),
                selectedTextColor = Yellow,
                unselectedTextColor = Color.White.copy(alpha = 0.6f),
                indicatorColor = DarkBlue.copy(alpha = 0.2f)
            )
        )
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Search, contentDescription = "Búsqueda") },
            label = { Text("Búsqueda", fontSize = 11.sp) },
            selected = selectedTab == 2,
            onClick = { onTabSelected(2) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Yellow,
                unselectedIconColor = Color.White.copy(alpha = 0.6f),
                selectedTextColor = Yellow,
                unselectedTextColor = Color.White.copy(alpha = 0.6f),
                indicatorColor = DarkBlue.copy(alpha = 0.2f)
            )
        )
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Favorite, contentDescription = "Favoritos") },
            label = { Text("Favoritos", fontSize = 11.sp) },
            selected = selectedTab == 3,
            onClick = { onTabSelected(3) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Yellow,
                unselectedIconColor = Color.White.copy(alpha = 0.6f),
                selectedTextColor = Yellow,
                unselectedTextColor = Color.White.copy(alpha = 0.6f),
                indicatorColor = DarkBlue.copy(alpha = 0.2f)
            )
        )
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Person, contentDescription = "Perfil") },
            label = { Text("Perfil", fontSize = 11.sp) },
            selected = selectedTab == 4,
            onClick = { onTabSelected(4) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Yellow,
                unselectedIconColor = Color.White.copy(alpha = 0.6f),
                selectedTextColor = Yellow,
                unselectedTextColor = Color.White.copy(alpha = 0.6f),
                indicatorColor = DarkBlue.copy(alpha = 0.2f)
            )
        )
    }
}

@Composable
fun MovieCard(
    pelicula: pelicula,
    isFavorite: Boolean = false,
    onClick: () -> Unit = {}
) {
    Box(modifier = Modifier.width(150.dp)) {
        Column(
            modifier = Modifier.clickable(onClick = onClick),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AsyncImage(
                model = pelicula.posterUrl,
                contentDescription = pelicula.title ?: "",
                modifier = Modifier
                    .width(150.dp)
                    .height(225.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = pelicula.title ?: "",
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }

        if (isFavorite) {
            Icon(
                imageVector = Icons.Filled.Favorite,
                contentDescription = "Favorito",
                tint = Color.Red,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .size(24.dp)
            )
        }
    }
}

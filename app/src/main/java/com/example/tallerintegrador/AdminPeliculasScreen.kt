package com.example.tallerintegrador

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.tallerintegrador.data.model.pelicula
import com.example.tallerintegrador.feature.admin.AdminViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminPeliculasScreen(
    navController: NavController,
    adminViewModel: AdminViewModel = hiltViewModel()
) {
    val peliculas by adminViewModel.peliculas.collectAsState()
    val isLoading by adminViewModel.isLoading.collectAsState()
    val error by adminViewModel.error.collectAsState()

    var showDeleteDialog by remember { mutableStateOf(false) }
    var showDetailsDialog by remember { mutableStateOf(false) }
    var showFilterDialog by remember { mutableStateOf(false) }
    var selectedPelicula by remember { mutableStateOf<pelicula?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedGenero by remember { mutableStateOf<String?>(null) }

    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    // Películas filtradas
    val peliculasFiltradas = remember(peliculas, searchQuery, selectedGenero) {
        val generoFiltro = selectedGenero
        peliculas.filter { pelicula ->
            val matchesSearch = searchQuery.isEmpty() ||
                    pelicula.title.contains(searchQuery, ignoreCase = true) ||
                    pelicula.genre.contains(searchQuery, ignoreCase = true) ||
                    pelicula.description.contains(searchQuery, ignoreCase = true)

            val matchesGenero = selectedGenero == null ||
                    pelicula.genre.contains(generoFiltro ?: "", ignoreCase = true)

            matchesSearch && matchesGenero
        }
    }

    // Géneros únicos
    val generosDisponibles = remember(peliculas) {
        peliculas.flatMap { it.genre.split(",").map { g -> g.trim() } }
            .distinct()
            .sorted()
    }

    LaunchedEffect(error) {
        error?.let {
            snackbarHostState.showSnackbar(it)
            adminViewModel.limpiarError()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "Gestionar Películas",
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "${peliculas.size} en catálogo",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 12.sp
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showFilterDialog = true }) {
                        Badge(
                            containerColor = if (selectedGenero != null)
                                MaterialTheme.colorScheme.error
                            else
                                Color.Transparent
                        ) {
                            Icon(
                                imageVector = Icons.Filled.FilterList,
                                contentDescription = "Filtros",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                    IconButton(onClick = { adminViewModel.refresh() }) {
                        Icon(
                            imageVector = Icons.Filled.Refresh,
                            contentDescription = "Actualizar",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    IconButton(onClick = {
                        navController.navigate("admin/peliculas/nueva")
                    }) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = "Agregar película",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                isLoading && peliculas.isEmpty() -> {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                peliculas.isEmpty() -> {
                    EmptyMoviesState(modifier = Modifier.align(Alignment.Center))
                }

                else -> {
                    Column(modifier = Modifier.fillMaxSize()) {
                        // Barra de búsqueda
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            placeholder = {
                                Text(
                                    "Buscar películas...",
                                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    Icons.Filled.Search,
                                    contentDescription = "Buscar",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            },
                            trailingIcon = {
                                if (searchQuery.isNotEmpty()) {
                                    IconButton(onClick = { searchQuery = "" }) {
                                        Icon(
                                            Icons.Filled.Close,
                                            contentDescription = "Limpiar",
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                                cursorColor = MaterialTheme.colorScheme.primary,
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                            ),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )

                        // Chip de filtro activo
                        if (selectedGenero != null) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                AssistChip(
                                    onClick = { selectedGenero = null },
                                    label = { Text("Género: $selectedGenero") },
                                    leadingIcon = {
                                        Icon(
                                            Icons.Filled.FilterList,
                                            null,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    },
                                    trailingIcon = {
                                        Icon(
                                            Icons.Filled.Close,
                                            null,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    },
                                    colors = AssistChipDefaults.assistChipColors(
                                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                                        labelColor = MaterialTheme.colorScheme.primary,
                                        leadingIconContentColor = MaterialTheme.colorScheme.primary,
                                        trailingIconContentColor = MaterialTheme.colorScheme.primary
                                    )
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        // Stats
                        MovieStatsBar(
                            total = peliculas.size,
                            filtradas = peliculasFiltradas.size,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Grid de películas
                        if (peliculasFiltradas.isEmpty()) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        Icons.Filled.SearchOff,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f),
                                        modifier = Modifier.size(64.dp)
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text(
                                        "No se encontraron películas",
                                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                                        fontSize = 16.sp
                                    )
                                    if (selectedGenero != null || searchQuery.isNotEmpty()) {
                                        Spacer(modifier = Modifier.height(8.dp))
                                        TextButton(
                                            onClick = {
                                                searchQuery = ""
                                                selectedGenero = null
                                            }
                                        ) {
                                            Text(
                                                "Limpiar filtros",
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                    }
                                }
                            }
                        } else {
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(2),
                                contentPadding = PaddingValues(16.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(peliculasFiltradas) { pelicula ->
                                    AdminPeliculaCard(
                                        pelicula = pelicula,
                                        onDetails = {
                                            selectedPelicula = pelicula
                                            showDetailsDialog = true
                                        },
                                        onEdit = {
                                            navController.navigate("admin/peliculas/editar/${pelicula.id}")
                                        },
                                        onDelete = {
                                            selectedPelicula = pelicula
                                            showDeleteDialog = true
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // DIÁLOGO DE FILTROS
    if (showFilterDialog) {
        AlertDialog(
            onDismissRequest = { showFilterDialog = false },
            title = {
                Text(
                    "Filtrar por Género",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                LazyColumn {
                    item {
                        FilterChip(
                            selected = selectedGenero == null,
                            onClick = {
                                selectedGenero = null
                                showFilterDialog = false
                            },
                            label = { Text("Todos los géneros") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.primary
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        )
                    }

                    items(generosDisponibles.size) { index ->
                        val genero = generosDisponibles[index]
                        FilterChip(
                            selected = selectedGenero == genero,
                            onClick = {
                                selectedGenero = genero
                                showFilterDialog = false
                            },
                            label = { Text(genero) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.primary,
                                labelColor = MaterialTheme.colorScheme.onSurface
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showFilterDialog = false }) {
                    Text("Cerrar", color = MaterialTheme.colorScheme.primary)
                }
            },
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(16.dp)
        )
    }

    // DIÁLOGO DE DETALLES
    if (showDetailsDialog && selectedPelicula != null) {
        AlertDialog(
            onDismissRequest = { showDetailsDialog = false },
            title = {
                Text(
                    selectedPelicula?.title ?: "",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    AsyncImage(
                        model = selectedPelicula?.posterUrl,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )

                    MovieDetailRow("ID", selectedPelicula?.id.toString())
                    MovieDetailRow("Género", selectedPelicula?.genre ?: "")
                    MovieDetailRow("Duración", "${selectedPelicula?.durationMinutes} min")
                    MovieDetailRow("Descripción", selectedPelicula?.description ?: "")
                }
            },
            confirmButton = {
                TextButton(onClick = { showDetailsDialog = false }) {
                    Text("Cerrar", color = MaterialTheme.colorScheme.primary)
                }
            },
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(16.dp)
        )
    }

    // DIÁLOGO ELIMINAR
    if (showDeleteDialog && selectedPelicula != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            icon = {
                Icon(
                    Icons.Filled.Warning,
                    contentDescription = "Advertencia",
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(48.dp)
                )
            },
            title = {
                Text(
                    "Eliminar Película",
                    color = MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    Text(
                        "¿Estás seguro de eliminar \"${selectedPelicula?.title}\"?",
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        "Esta acción:",
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            "• Eliminará la película del catálogo",
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                            fontSize = 14.sp
                        )
                        Text(
                            "• La quitará de todos los favoritos",
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                            fontSize = 14.sp
                        )
                        Text(
                            "• No se puede deshacer",
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        selectedPelicula?.let { pelicula ->
                            adminViewModel.eliminarPelicula(pelicula.id)
                            scope.launch {
                                snackbarHostState.showSnackbar("Película eliminada")
                            }
                        }
                        showDeleteDialog = false
                        selectedPelicula = null
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Eliminar", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancelar", color = MaterialTheme.colorScheme.primary)
                }
            },
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@Composable
fun MovieStatsBar(
    total: Int,
    filtradas: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    Icons.Filled.Movie,
                    null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    total.toString(),
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "Total",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 12.sp
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    Icons.Filled.Visibility,
                    null,
                    tint = Color(0xFF2196F3),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    filtradas.toString(),
                    color = Color(0xFF2196F3),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "Mostrando",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
fun AdminPeliculaCard(
    pelicula: pelicula,
    onDetails: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column {
            // Poster
            AsyncImage(
                model = pelicula.posterUrl,
                contentDescription = pelicula.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)),
                contentScale = ContentScale.Crop
            )

            // Info
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                Text(
                    text = pelicula.title,
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = pelicula.genre,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    IconButton(
                        onClick = onDetails,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Info,
                            contentDescription = "Detalles",
                            tint = Color(0xFF2196F3),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    IconButton(
                        onClick = onEdit,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Edit,
                            contentDescription = "Editar",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = "Eliminar",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MovieDetailRow(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 2.dp)) {
        Text(
            label,
            color = MaterialTheme.colorScheme.primary,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            value,
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 13.sp
        )
    }
}

@Composable
fun EmptyMoviesState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            Icons.Filled.Movie,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f),
            modifier = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "No hay películas en el catálogo",
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
            fontSize = 16.sp
        )
    }
}
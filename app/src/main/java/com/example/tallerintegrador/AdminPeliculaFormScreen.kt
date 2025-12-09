package com.example.tallerintegrador

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.tallerintegrador.feature.admin.AdminViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminPeliculaFormScreen(
    navController: NavController,
    peliculaId: Int? = null,
    adminViewModel: AdminViewModel
) {
    val peliculas by adminViewModel.peliculas.collectAsState()
    val isLoading by adminViewModel.isLoading.collectAsState()
    val error by adminViewModel.error.collectAsState()

    // CRÍTICO: Buscar la película REACTIVAMENTE
    val peliculaExistente = remember(peliculas, peliculaId) {
        peliculaId?.let { id ->
            peliculas.find { it.id == id }
        }
    }

    val esEdicion = peliculaExistente != null
    val titulo = if (esEdicion) "Editar Película" else "Agregar Película"

    // Estados del formulario CON VALORES INICIALES
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var posterUrl by remember { mutableStateOf("") }
    var videoUrl by remember { mutableStateOf("") }
    var durationMinutes by remember { mutableStateOf("") }
    var genre by remember { mutableStateOf("") }
    var datosCarCargados by remember { mutableStateOf(false) }

    // CARGAR DATOS DE LA PELÍCULA EXISTENTE
    LaunchedEffect(peliculaExistente) {
        if (peliculaExistente != null && !datosCarCargados) {
            title = peliculaExistente.title
            description = peliculaExistente.description
            posterUrl = peliculaExistente.posterUrl
            videoUrl = peliculaExistente.videoUrl
            durationMinutes = peliculaExistente.durationMinutes.toString()
            genre = peliculaExistente.genre
            datosCarCargados = true
        }
    }

    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    // Validaciones
    val titleError = remember(title) {
        when {
            title.isBlank() -> "El título es obligatorio"
            title.length < 2 -> "El título debe tener al menos 2 caracteres"
            else -> null
        }
    }

    val descriptionError = remember(description) {
        when {
            description.isBlank() -> "La descripción es obligatoria"
            description.length < 10 -> "La descripción debe tener al menos 10 caracteres"
            else -> null
        }
    }

    val posterUrlError = remember(posterUrl) {
        when {
            posterUrl.isBlank() -> "La URL del póster es obligatoria"
            !posterUrl.startsWith("http") -> "Debe ser una URL válida (http://...)"
            else -> null
        }
    }

    val videoUrlError = remember(videoUrl) {
        when {
            videoUrl.isBlank() -> "La URL del video es obligatoria"
            !videoUrl.startsWith("http") -> "Debe ser una URL válida (http://...)"
            else -> null
        }
    }

    val durationError = remember(durationMinutes) {
        when {
            durationMinutes.isBlank() -> "La duración es obligatoria"
            durationMinutes.toIntOrNull() == null -> "Debe ser un número válido"
            (durationMinutes.toIntOrNull() ?: 0) < 1 -> "Debe ser mayor a 0"
            else -> null
        }
    }

    val genreError = remember(genre) {
        when {
            genre.isBlank() -> "El género es obligatorio"
            else -> null
        }
    }

    val isFormValid = titleError == null &&
            descriptionError == null &&
            posterUrlError == null &&
            videoUrlError == null &&
            durationError == null &&
            genreError == null

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
                            titulo,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        if (peliculaId != null) {
                            Text(
                                "ID: $peliculaId",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 12.sp
                            )
                        }
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
            // MOSTRAR INDICADOR SI ESTÁ CARGANDO DATOS
            if (peliculaId != null && peliculaExistente == null && !datosCarCargados) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "Cargando datos de la película...",
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 16.sp
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // INDICADOR VISUAL DE MODO
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = if (esEdicion)
                                    MaterialTheme.colorScheme.primaryContainer
                                else
                                    Color(0xFF2196F3).copy(alpha = 0.15f)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (esEdicion) Icons.Filled.Edit else Icons.Filled.Add,
                                    contentDescription = null,
                                    tint = if (esEdicion)
                                        MaterialTheme.colorScheme.primary
                                    else
                                        Color(0xFF2196F3),
                                    modifier = Modifier.size(28.dp)
                                )

                                Spacer(modifier = Modifier.width(12.dp))

                                Column {
                                    Text(
                                        if (esEdicion) "Modo Edición" else "Modo Creación",
                                        color = MaterialTheme.colorScheme.onSurface,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        if (esEdicion)
                                            "Modifica los campos que necesites"
                                        else
                                            "Completa todos los campos obligatorios (*)",
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontSize = 13.sp
                                    )
                                }
                            }
                        }
                    }

                    // TÍTULO
                    item {
                        OutlinedTextField(
                            value = title,
                            onValueChange = { title = it },
                            label = { Text("Título *", color = MaterialTheme.colorScheme.primary) },
                            leadingIcon = {
                                Icon(Icons.Filled.Movie, null, tint = MaterialTheme.colorScheme.primary)
                            },
                            isError = titleError != null,
                            supportingText = {
                                titleError?.let {
                                    Text(it, color = MaterialTheme.colorScheme.error)
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                                cursorColor = MaterialTheme.colorScheme.primary,
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                                errorBorderColor = MaterialTheme.colorScheme.error
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }

                    // DESCRIPCIÓN
                    item {
                        OutlinedTextField(
                            value = description,
                            onValueChange = { description = it },
                            label = { Text("Descripción *", color = MaterialTheme.colorScheme.primary) },
                            leadingIcon = {
                                Icon(Icons.Filled.Description, null, tint = MaterialTheme.colorScheme.primary)
                            },
                            isError = descriptionError != null,
                            supportingText = {
                                descriptionError?.let {
                                    Text(it, color = MaterialTheme.colorScheme.error)
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 4,
                            maxLines = 6,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                                cursorColor = MaterialTheme.colorScheme.primary,
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                                errorBorderColor = MaterialTheme.colorScheme.error
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }

                    // GÉNERO
                    item {
                        OutlinedTextField(
                            value = genre,
                            onValueChange = { genre = it },
                            label = { Text("Género *", color = MaterialTheme.colorScheme.primary) },
                            leadingIcon = {
                                Icon(Icons.Filled.Category, null, tint = MaterialTheme.colorScheme.primary)
                            },
                            placeholder = {
                                Text(
                                    "Ej: Acción, Drama, Comedia",
                                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                                )
                            },
                            isError = genreError != null,
                            supportingText = {
                                genreError?.let {
                                    Text(it, color = MaterialTheme.colorScheme.error)
                                } ?: Text(
                                    "Separa múltiples géneros con comas",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                                cursorColor = MaterialTheme.colorScheme.primary,
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                                errorBorderColor = MaterialTheme.colorScheme.error
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }

                    // DURACIÓN
                    item {
                        OutlinedTextField(
                            value = durationMinutes,
                            onValueChange = {
                                if (it.isEmpty() || it.all { char -> char.isDigit() }) {
                                    durationMinutes = it
                                }
                            },
                            label = { Text("Duración (minutos) *", color = MaterialTheme.colorScheme.primary) },
                            leadingIcon = {
                                Icon(Icons.Filled.Schedule, null, tint = MaterialTheme.colorScheme.primary)
                            },
                            isError = durationError != null,
                            supportingText = {
                                durationError?.let {
                                    Text(it, color = MaterialTheme.colorScheme.error)
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                                cursorColor = MaterialTheme.colorScheme.primary,
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                                errorBorderColor = MaterialTheme.colorScheme.error
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }

                    // URL PÓSTER
                    item {
                        OutlinedTextField(
                            value = posterUrl,
                            onValueChange = { posterUrl = it },
                            label = { Text("URL del Póster *", color = MaterialTheme.colorScheme.primary) },
                            leadingIcon = {
                                Icon(Icons.Filled.Image, null, tint = MaterialTheme.colorScheme.primary)
                            },
                            placeholder = {
                                Text(
                                    "https://...",
                                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                                )
                            },
                            isError = posterUrlError != null,
                            supportingText = {
                                posterUrlError?.let {
                                    Text(it, color = MaterialTheme.colorScheme.error)
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                                cursorColor = MaterialTheme.colorScheme.primary,
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                                errorBorderColor = MaterialTheme.colorScheme.error
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }

                    // PREVIEW DEL PÓSTER
                    if (posterUrl.isNotBlank() && posterUrl.startsWith("http")) {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                                )
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            Icons.Filled.Visibility,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            "Preview del Póster",
                                            color = MaterialTheme.colorScheme.primary,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(12.dp))
                                    AsyncImage(
                                        model = posterUrl,
                                        contentDescription = "Preview",
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(300.dp)
                                            .clip(RoundedCornerShape(8.dp)),
                                        contentScale = ContentScale.Crop
                                    )
                                }
                            }
                        }
                    }

                    // URL VIDEO
                    item {
                        OutlinedTextField(
                            value = videoUrl,
                            onValueChange = { videoUrl = it },
                            label = { Text("URL del Video/Tráiler *", color = MaterialTheme.colorScheme.primary) },
                            leadingIcon = {
                                Icon(Icons.Filled.VideoLibrary, null, tint = MaterialTheme.colorScheme.primary)
                            },
                            placeholder = {
                                Text(
                                    "https://...",
                                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                                )
                            },
                            isError = videoUrlError != null,
                            supportingText = {
                                videoUrlError?.let {
                                    Text(it, color = MaterialTheme.colorScheme.error)
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                                cursorColor = MaterialTheme.colorScheme.primary,
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                                errorBorderColor = MaterialTheme.colorScheme.error
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }

                    // BOTONES
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            OutlinedButton(
                                onClick = { navController.popBackStack() },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = MaterialTheme.colorScheme.onSurface
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Filled.Cancel, null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Cancelar")
                            }

                            Button(
                                onClick = {
                                    scope.launch {
                                        if (esEdicion && peliculaId != null) {
                                            adminViewModel.actualizarPelicula(
                                                peliculaId = peliculaId,
                                                title = title,
                                                description = description,
                                                posterUrl = posterUrl,
                                                videoUrl = videoUrl,
                                                durationMinutes = durationMinutes.toInt(),
                                                genre = genre,
                                                onSuccess = {
                                                    scope.launch {
                                                        snackbarHostState.showSnackbar("Película actualizada exitosamente")
                                                        navController.popBackStack()
                                                    }
                                                }
                                            )
                                        } else {
                                            adminViewModel.crearPelicula(
                                                title = title,
                                                description = description,
                                                posterUrl = posterUrl,
                                                videoUrl = videoUrl,
                                                durationMinutes = durationMinutes.toInt(),
                                                genre = genre,
                                                onSuccess = {
                                                    scope.launch {
                                                        snackbarHostState.showSnackbar("Película creada exitosamente")
                                                        navController.popBackStack()
                                                    }
                                                }
                                            )
                                        }
                                    }
                                },
                                enabled = isFormValid && !isLoading,
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    disabledContainerColor = Color.Gray
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                if (isLoading) {
                                    CircularProgressIndicator(
                                        color = MaterialTheme.colorScheme.onPrimary,
                                        modifier = Modifier.size(20.dp),
                                        strokeWidth = 2.dp
                                    )
                                } else {
                                    Icon(
                                        Icons.Filled.Save,
                                        null,
                                        tint = MaterialTheme.colorScheme.onPrimary
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        if (esEdicion) "Actualizar" else "Crear",
                                        color = MaterialTheme.colorScheme.onPrimary,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(32.dp))
                    }
                }
            }
        }
    }
}
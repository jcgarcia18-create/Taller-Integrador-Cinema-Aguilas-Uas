// app/src/main/java/com/example/tallerintegrador/AdminLogsScreen.kt
package com.example.tallerintegrador

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Article
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.tallerintegrador.feature.admin.AdminViewModel
import com.example.tallerintegrador.feature.admin.LogActividad
import com.example.tallerintegrador.ui.theme.DarkBlue
import com.example.tallerintegrador.ui.theme.Yellow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminLogsScreen(
    navController: NavController,
    adminViewModel: AdminViewModel = hiltViewModel()
) {
    val logs by adminViewModel.logs.collectAsState()
    val isLoading by adminViewModel.isLoading.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf<String?>(null) }
    var showFilterDialog by remember { mutableStateOf(false) }

    // Filtros disponibles
    val filtrosDisponibles = listOf(
        "Login", "Logout", "Registro", "Agregar Favorito",
        "Eliminar Favorito", "Ver Película", "Buscar", "Error"
    )

    // Logs filtrados
    val logsFiltrados = remember(logs, searchQuery, selectedFilter) {
        logs.filter { log ->
            val matchesSearch = searchQuery.isEmpty() ||
                    log.usuarioNombre.contains(searchQuery, ignoreCase = true) ||
                    log.accion.contains(searchQuery, ignoreCase = true) ||
                    log.detalles.contains(searchQuery, ignoreCase = true)

            val matchesFilter = selectedFilter == null ||
                    log.accion.equals(selectedFilter, ignoreCase = true)

            matchesSearch && matchesFilter
        }
    }

    // Estadísticas de logs
    val estadisticas = remember(logs) {
        mapOf(
            "Total" to logs.size,
            "Login" to logs.count { it.accion.equals("Login", ignoreCase = true) },
            "Favoritos" to logs.count {
                it.accion.contains("Favorito", ignoreCase = true)
            },
            "Errores" to logs.count { it.accion.equals("Error", ignoreCase = true) }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "Logs de Actividad",
                            color = Yellow,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "${logs.size} registros",
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 12.sp
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
                actions = {
                    IconButton(onClick = { showFilterDialog = true }) {
                        Badge(
                            containerColor = if (selectedFilter != null) Color.Red else Color.Transparent
                        ) {
                            Icon(
                                imageVector = Icons.Filled.FilterList,
                                contentDescription = "Filtros",
                                tint = Color.White
                            )
                        }
                    }
                    IconButton(onClick = { adminViewModel.refresh() }) {
                        Icon(
                            imageVector = Icons.Filled.Refresh,
                            contentDescription = "Actualizar",
                            tint = Color.White
                        )
                    }
                    IconButton(onClick = { /* TODO: Exportar logs */ }) {
                        Icon(
                            imageVector = Icons.Filled.Download,
                            contentDescription = "Exportar",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkBlue
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
                isLoading && logs.isEmpty() -> {
                    CircularProgressIndicator(
                        color = Yellow,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                logs.isEmpty() -> {
                    EmptyLogsState(modifier = Modifier.align(Alignment.Center))
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
                                Text("Buscar en logs...", color = Color.White.copy(alpha = 0.5f))
                            },
                            leadingIcon = {
                                Icon(Icons.Filled.Search, contentDescription = "Buscar", tint = Yellow)
                            },
                            trailingIcon = {
                                if (searchQuery.isNotEmpty()) {
                                    IconButton(onClick = { searchQuery = "" }) {
                                        Icon(Icons.Filled.Close, contentDescription = "Limpiar", tint = Yellow)
                                    }
                                }
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                cursorColor = Yellow,
                                focusedBorderColor = Yellow,
                                unfocusedBorderColor = Yellow.copy(alpha = 0.5f)
                            ),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )

                        // Chip de filtro activo
                        if (selectedFilter != null) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                AssistChip(
                                    onClick = { selectedFilter = null },
                                    label = { Text("Filtro: $selectedFilter") },
                                    leadingIcon = {
                                        Icon(Icons.Filled.FilterList, null, modifier = Modifier.size(18.dp))
                                    },
                                    trailingIcon = {
                                        Icon(Icons.Filled.Close, null, modifier = Modifier.size(18.dp))
                                    },
                                    colors = AssistChipDefaults.assistChipColors(
                                        containerColor = Yellow.copy(alpha = 0.2f),
                                        labelColor = Yellow,
                                        leadingIconContentColor = Yellow,
                                        trailingIconContentColor = Yellow
                                    )
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        // Estadísticas
                        LogsStatsCard(
                            estadisticas = estadisticas,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Lista de logs
                        if (logsFiltrados.isEmpty()) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        Icons.Filled.SearchOff,
                                        contentDescription = null,
                                        tint = Color.White.copy(alpha = 0.3f),
                                        modifier = Modifier.size(64.dp)
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text(
                                        "No se encontraron logs",
                                        color = Color.White.copy(alpha = 0.6f),
                                        fontSize = 16.sp
                                    )
                                    if (selectedFilter != null || searchQuery.isNotEmpty()) {
                                        Spacer(modifier = Modifier.height(8.dp))
                                        TextButton(
                                            onClick = {
                                                searchQuery = ""
                                                selectedFilter = null
                                            }
                                        ) {
                                            Text("Limpiar filtros", color = Yellow)
                                        }
                                    }
                                }
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(logsFiltrados) { log ->
                                    LogCard(log = log)
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
                    "Filtrar Logs",
                    color = Yellow,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                LazyColumn {
                    item {
                        FilterChip(
                            selected = selectedFilter == null,
                            onClick = {
                                selectedFilter = null
                                showFilterDialog = false
                            },
                            label = { Text("Todos los logs") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Yellow.copy(alpha = 0.3f),
                                selectedLabelColor = Yellow
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        )
                    }

                    items(filtrosDisponibles.size) { index ->
                        val filtro = filtrosDisponibles[index]
                        FilterChip(
                            selected = selectedFilter == filtro,
                            onClick = {
                                selectedFilter = filtro
                                showFilterDialog = false
                            },
                            label = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(
                                        imageVector = getLogIcon(filtro),
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(filtro)
                                }
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Yellow.copy(alpha = 0.3f),
                                selectedLabelColor = Yellow,
                                labelColor = Color.White
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
                    Text("Cerrar", color = Yellow)
                }
            },
            containerColor = DarkBlue,
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@Composable
fun LogsStatsCard(
    estadisticas: Map<String, Int>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Yellow.copy(alpha = 0.15f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            estadisticas.forEach { (label, value) ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        value.toString(),
                        color = Yellow,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        label,
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

@Composable
fun LogCard(log: LogActividad) {
    var expanded by remember { mutableStateOf(false) }
    val (icon, iconColor) = getLogIconAndColor(log.accion)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.05f)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Ícono de acción
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(iconColor.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = log.accion,
                        tint = iconColor,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Información principal
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = log.usuarioNombre,
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = log.accion,
                            color = iconColor,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                        if (log.detalles.isNotEmpty()) {
                            Text(
                                text = " • ${log.detalles.take(20)}${if (log.detalles.length > 20) "..." else ""}",
                                color = Color.White.copy(alpha = 0.6f),
                                fontSize = 13.sp
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.Schedule,
                            contentDescription = "Tiempo",
                            tint = Color.White.copy(alpha = 0.5f),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = log.timestamp,
                            color = Color.White.copy(alpha = 0.5f),
                            fontSize = 12.sp
                        )
                    }
                }

                // Indicador expandible
                Icon(
                    imageVector = if (expanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                    contentDescription = if (expanded) "Contraer" else "Expandir",
                    tint = Color.White.copy(alpha = 0.5f)
                )
            }

            // Información expandida
            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp)
                ) {
                    HorizontalDivider(
                        color = Color.White.copy(alpha = 0.2f),
                        modifier = Modifier.padding(vertical = 8.dp)
                    )

                    LogDetailRow("ID de Log", log.id.toString())
                    LogDetailRow("ID de Usuario", log.usuarioId.toString())
                    LogDetailRow("Acción", log.accion)
                    if (log.detalles.isNotEmpty()) {
                        LogDetailRow("Detalles", log.detalles)
                    }
                    LogDetailRow("Fecha y hora", log.timestamp)
                }
            }
        }
    }
}

@Composable
fun LogDetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Text(
            text = "$label:",
            color = Yellow,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.width(120.dp)
        )
        Text(
            text = value,
            color = Color.White,
            fontSize = 12.sp,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun EmptyLogsState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            Icons.AutoMirrored.Filled.Article,
            contentDescription = null,
            tint = Color.White.copy(alpha = 0.3f),
            modifier = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "No hay actividad registrada",
            color = Color.White.copy(alpha = 0.6f),
            fontSize = 16.sp
        )
    }
}

private fun getLogIconAndColor(accion: String): Pair<ImageVector, Color> {
    return when (accion.lowercase()) {
        "login" -> Icons.AutoMirrored.Filled.Login to Color(0xFF4CAF50)
        "logout" -> Icons.AutoMirrored.Filled.Logout to Color(0xFFFF9800)
        "registro" -> Icons.Filled.PersonAdd to Color(0xFF2196F3)
        "agregar favorito" -> Icons.Filled.Favorite to Color(0xFFE91E63)
        "eliminar favorito" -> Icons.Filled.FavoriteBorder to Color(0xFF9C27B0)
        "ver pelicula", "ver película" -> Icons.Filled.PlayArrow to Color(0xFF00BCD4)
        "buscar" -> Icons.Filled.Search to Color(0xFF607D8B)
        "error" -> Icons.Filled.Error to Color(0xFFF44336)
        else -> Icons.Filled.Circle to Color(0xFF9E9E9E)
    }
}

private fun getLogIcon(accion: String): ImageVector {
    return when (accion.lowercase()) {
        "login" -> Icons.AutoMirrored.Filled.Login
        "logout" -> Icons.AutoMirrored.Filled.Logout
        "registro" -> Icons.Filled.PersonAdd
        "agregar favorito" -> Icons.Filled.Favorite
        "eliminar favorito" -> Icons.Filled.FavoriteBorder
        "ver pelicula", "ver película" -> Icons.Filled.PlayArrow
        "buscar" -> Icons.Filled.Search
        "error" -> Icons.Filled.Error
        else -> Icons.Filled.Circle
    }
}
// app/src/main/java/com/example/tallerintegrador/AdminDashboardScreen.kt
package com.example.tallerintegrador

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Article
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.tallerintegrador.feature.admin.AdminViewModel
import com.example.tallerintegrador.ui.theme.DarkBlue
import com.example.tallerintegrador.ui.theme.Yellow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    navController: NavController,
    adminViewModel: AdminViewModel = hiltViewModel()
) {
    val isAdmin by adminViewModel.isAdmin.collectAsState()
    val estadisticas by adminViewModel.estadisticas.collectAsState()
    val isLoading by adminViewModel.isLoading.collectAsState()

    LaunchedEffect(Unit) {
        if (isAdmin) {
            adminViewModel.cargarDashboard()
        }
    }

    // Redirigir si no es admin
    if (!isAdmin) {
        LaunchedEffect(Unit) {
            navController.popBackStack()
        }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.Shield,
                            contentDescription = "Admin",
                            tint = Yellow,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Panel de Administración",
                            color = Yellow,
                            fontWeight = FontWeight.Bold
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
                    IconButton(onClick = { adminViewModel.refresh() }) {
                        Icon(
                            imageVector = Icons.Filled.Refresh,
                            contentDescription = "Actualizar",
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
            if (isLoading && estadisticas == null) {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(color = Yellow)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Cargando dashboard...", color = Color.White)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // ESTADÍSTICAS
                    item {
                        Text(
                            "Estadísticas Generales",
                            color = Yellow,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    item {
                        estadisticas?.let { stats ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                EstadisticaCard(
                                    modifier = Modifier.weight(1f),
                                    icon = Icons.Filled.People,
                                    label = "Usuarios",
                                    value = stats.totalUsuarios.toString(),
                                    color = Color(0xFF4CAF50)
                                )
                                EstadisticaCard(
                                    modifier = Modifier.weight(1f),
                                    icon = Icons.Filled.Movie,
                                    label = "Películas",
                                    value = stats.totalPeliculas.toString(),
                                    color = Color(0xFF2196F3)
                                )
                            }
                        }
                    }

                    item {
                        estadisticas?.let { stats ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                EstadisticaCard(
                                    modifier = Modifier.weight(1f),
                                    icon = Icons.Filled.Favorite,
                                    label = "Favoritos",
                                    value = stats.totalFavoritos.toString(),
                                    color = Color(0xFFE91E63)
                                )
                                EstadisticaCard(
                                    modifier = Modifier.weight(1f),
                                    icon = Icons.AutoMirrored.Filled.TrendingUp,
                                    label = "Popular",
                                    value = stats.peliculaMasPopular?.take(10) ?: "N/A",
                                    color = Color(0xFFFF9800)
                                )
                            }
                        }
                    }

                    // ACCIONES RÁPIDAS
                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Acciones Rápidas",
                            color = Yellow,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    item {
                        AdminActionCard(
                            icon = Icons.Filled.People,
                            title = "Gestionar Usuarios",
                            description = "Ver, editar y eliminar usuarios",
                            onClick = { navController.navigate("admin/usuarios") }
                        )
                    }

                    item {
                        AdminActionCard(
                            icon = Icons.Filled.Movie,
                            title = "Gestionar Películas",
                            description = "Agregar, editar y eliminar películas",
                            onClick = { navController.navigate("admin/peliculas") }
                        )
                    }

                    item {
                        AdminActionCard(
                            icon = Icons.AutoMirrored.Filled.Article,
                            title = "Ver Logs de Actividad",
                            description = "Revisar historial del sistema",
                            onClick = { navController.navigate("admin/logs") }
                        )
                    }

                    item {
                        AdminActionCard(
                            icon = Icons.Filled.Settings,
                            title = "Configuración Avanzada",
                            description = "Ajustes del sistema",
                            onClick = { /* TODO */ }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EstadisticaCard(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    label: String,
    value: String,
    color: Color
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.15f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = color,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = label,
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 12.sp
            )
        }
    }
}

@Composable
fun AdminActionCard(
    icon: ImageVector,
    title: String,
    description: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.05f)
        ),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(
                        color = Yellow.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = Yellow,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = description,
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 13.sp
                )
            }

            Icon(
                imageVector = Icons.Filled.ChevronRight,
                contentDescription = "Ir",
                tint = Color.White.copy(alpha = 0.5f)
            )
        }
    }
}
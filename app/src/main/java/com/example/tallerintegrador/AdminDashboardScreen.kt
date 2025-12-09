package com.example.tallerintegrador

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.tallerintegrador.feature.admin.AdminViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    navController: NavController,
    adminViewModel: AdminViewModel = hiltViewModel()
) {
    val isAdmin by adminViewModel.isAdmin.collectAsState()
    val estadisticas by adminViewModel.estadisticas.collectAsState()
    val isLoading by adminViewModel.isLoading.collectAsState()
    val error by adminViewModel.error.collectAsState()

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
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                "Panel de Administración",
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                            Text(
                                "Cinema Águilas UAS",
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
                actions = {
                    IconButton(onClick = { adminViewModel.refresh() }) {
                        Icon(
                            imageVector = Icons.Filled.Refresh,
                            contentDescription = "Actualizar",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                isLoading && estadisticas == null -> {
                    LoadingDashboard()
                }

                error != null && estadisticas == null -> {
                    ErrorDashboard(
                        error = error ?: "Error desconocido",
                        onRetry = { adminViewModel.refresh() }
                    )
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // BIENVENIDA
                        item {
                            WelcomeCard()
                        }

                        // ESTADÍSTICAS PRINCIPALES
                        item {
                            SectionTitle("Resumen General")
                        }

                        item {
                            estadisticas?.let { stats ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    AnimatedStatsCard(
                                        modifier = Modifier.weight(1f),
                                        icon = Icons.Filled.People,
                                        label = "Usuarios",
                                        value = stats.totalUsuarios,
                                        color = Color(0xFF4CAF50),
                                        targetValue = stats.totalUsuarios
                                    )
                                    AnimatedStatsCard(
                                        modifier = Modifier.weight(1f),
                                        icon = Icons.Filled.Movie,
                                        label = "Películas",
                                        value = stats.totalPeliculas,
                                        color = Color(0xFF2196F3),
                                        targetValue = stats.totalPeliculas
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
                                    AnimatedStatsCard(
                                        modifier = Modifier.weight(1f),
                                        icon = Icons.Filled.Favorite,
                                        label = "Favoritos",
                                        value = stats.totalFavoritos,
                                        color = Color(0xFFE91E63),
                                        targetValue = stats.totalFavoritos
                                    )
                                    AnimatedStatsCard(
                                        modifier = Modifier.weight(1f),
                                        icon = Icons.AutoMirrored.Filled.TrendingUp,
                                        label = "Popular",
                                        value = 0,
                                        color = Color(0xFFFF9800),
                                        valueText = stats.peliculaMasPopular?.take(10) ?: "N/A",
                                        targetValue = 100
                                    )
                                }
                            }
                        }

                        // INDICADORES ADICIONALES
                        item {
                            estadisticas?.let { stats ->
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                                    )
                                ) {
                                    Column(
                                        modifier = Modifier.padding(16.dp)
                                    ) {
                                        Text(
                                            "Información Destacada",
                                            color = MaterialTheme.colorScheme.primary,
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Spacer(modifier = Modifier.height(12.dp))

                                        InfoRow(
                                            icon = Icons.Filled.Star,
                                            label = "Película más popular",
                                            value = stats.peliculaMasPopular ?: "N/A",
                                            iconColor = Color(0xFFFF9800)
                                        )

                                        Spacer(modifier = Modifier.height(8.dp))

                                        InfoRow(
                                            icon = Icons.Filled.Person,
                                            label = "Usuario más activo",
                                            value = stats.usuarioMasActivo ?: "N/A",
                                            iconColor = Color(0xFF4CAF50)
                                        )
                                    }
                                }
                            }
                        }

                        // ACCIONES RÁPIDAS
                        item {
                            Spacer(modifier = Modifier.height(8.dp))
                            SectionTitle("Acciones Rápidas")
                        }

                        item {
                            AdminActionCard(
                                icon = Icons.Filled.People,
                                title = "Gestionar Usuarios",
                                description = "Ver, editar y eliminar usuarios",
                                badge = estadisticas?.totalUsuarios?.toString(),
                                badgeColor = Color(0xFF4CAF50),
                                onClick = { navController.navigate("admin/usuarios") }
                            )
                        }

                        item {
                            AdminActionCard(
                                icon = Icons.Filled.Movie,
                                title = "Gestionar Películas",
                                description = "Agregar, editar y eliminar películas",
                                badge = estadisticas?.totalPeliculas?.toString(),
                                badgeColor = Color(0xFF2196F3),
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
                                icon = Icons.Filled.BarChart,
                                title = "Estadísticas Avanzadas",
                                description = "Reportes y análisis detallados",
                                onClick = { /* TODO */ }
                            )
                        }

                        item {
                            AdminActionCard(
                                icon = Icons.Filled.Settings,
                                title = "Configuración del Sistema",
                                description = "Ajustes avanzados de la plataforma",
                                onClick = { /* TODO */ }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun WelcomeCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                            MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f)
                        )
                    )
                )
                .padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.Dashboard,
                    contentDescription = "Dashboard",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(48.dp)
                )

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        "Bienvenido al Dashboard",
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "Gestiona todo desde aquí",
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
fun AnimatedStatsCard(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    label: String,
    value: Int,
    color: Color,
    targetValue: Int,
    valueText: String? = null
) {
    var animatedValue by remember { mutableIntStateOf(0) }

    LaunchedEffect(targetValue) {
        animatedValue = targetValue
    }

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
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = color,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = valueText ?: animatedValue.toString(),
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = label,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
fun InfoRow(
    icon: ImageVector,
    label: String,
    value: String,
    iconColor: Color
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = iconColor,
            modifier = Modifier.size(20.dp)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 12.sp
            )
            Text(
                text = value,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        color = MaterialTheme.colorScheme.primary,
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold
    )
}

@Composable
fun AdminActionCard(
    icon: ImageVector,
    title: String,
    description: String,
    badge: String? = null,
    badgeColor: Color = Color(0xFFFFE600),
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
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
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = title,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )

                    if (badge != null) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            color = badgeColor.copy(alpha = 0.3f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = badge,
                                color = badgeColor,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                Text(
                    text = description,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 13.sp
                )
            }

            Icon(
                imageVector = Icons.Filled.ChevronRight,
                contentDescription = "Ir",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun LoadingDashboard() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        val infiniteTransition = rememberInfiniteTransition(label = "loading")
        val rotation by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(1000, easing = LinearEasing)
            ),
            label = "rotation"
        )

        Icon(
            imageVector = Icons.Filled.Refresh,
            contentDescription = "Cargando",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .size(64.dp)
                .rotate(rotation)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            "Cargando dashboard...",
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 16.sp
        )
    }
}

@Composable
fun ErrorDashboard(
    error: String,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Filled.ErrorOutline,
            contentDescription = "Error",
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier.size(64.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            "Error al cargar dashboard",
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            error,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            fontSize = 14.sp
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Icon(
                Icons.Filled.Refresh,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimary
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                "Reintentar",
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}
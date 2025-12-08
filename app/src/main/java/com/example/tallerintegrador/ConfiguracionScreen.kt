package com.example.tallerintegrador

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.tallerintegrador.feature.favoritos.FavoritosViewModel
import com.example.tallerintegrador.feature.peliculas.PeliculaViewModel
import com.example.tallerintegrador.ui.theme.LocalThemeManager
import kotlinx.coroutines.launch

/**
 * PANTALLA DE CONFIGURACIÓN CON CAMBIO DE TEMA INSTANTÁNEO
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfiguracionScreen(
    navController: NavController?,
    peliculaViewModel: PeliculaViewModel?,
    favoritosViewModel: FavoritosViewModel?
) {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("app_settings", Context.MODE_PRIVATE) }
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    // ACCESO AL THEME MANAGER
    val themeManager = LocalThemeManager.current
    val isDarkMode by themeManager.isDarkMode.collectAsState()

    // ===== ESTADOS DE CONFIGURACIÓN =====
    var notificacionesActivadas by remember {
        mutableStateOf(prefs.getBoolean("notificaciones", true))
    }
    var autoplayActivado by remember {
        mutableStateOf(prefs.getBoolean("autoplay", true))
    }
    var calidadVideo by remember {
        mutableStateOf(prefs.getString("calidad_video", "HD") ?: "HD")
    }
    var idioma by remember {
        mutableStateOf(prefs.getString("idioma", "Español") ?: "Español")
    }
    var descargasWifiOnly by remember {
        mutableStateOf(prefs.getBoolean("descargas_wifi", true))
    }
    var reproduccionAutomatica by remember {
        mutableStateOf(prefs.getBoolean("reproduccion_auto", false))
    }

    // Diálogos
    var showCalidadDialog by remember { mutableStateOf(false) }
    var showIdiomaDialog by remember { mutableStateOf(false) }
    var showClearCacheDialog by remember { mutableStateOf(false) }

    // COLORES DINÁMICOS según el tema
    val backgroundColor = MaterialTheme.colorScheme.background
    val surfaceColor = MaterialTheme.colorScheme.surface
    val primaryColor = MaterialTheme.colorScheme.primary
    val onBackgroundColor = MaterialTheme.colorScheme.onBackground

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Configuración",
                        color = primaryColor,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController?.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = onBackgroundColor
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = surfaceColor
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = backgroundColor
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // ===== SECCIÓN: APARIENCIA =====
            item {
                SectionHeader("Apariencia")
            }

            item {
                SettingItemSwitch(
                    icon = if (isDarkMode) Icons.Filled.DarkMode else Icons.Filled.LightMode,
                    title = "Modo ${if (isDarkMode) "Oscuro" else "Claro"}",
                    subtitle = if (isDarkMode)
                        "Interfaz oscura activada"
                    else
                        "Interfaz clara activada",
                    checked = isDarkMode,
                    onCheckedChange = { newValue ->
                        // CAMBIO INSTANTÁNEO
                        themeManager.setDarkMode(newValue)
                        scope.launch {
                            snackbarHostState.showSnackbar(
                                if (newValue) "Modo Oscuro activado"
                                else "Modo Claro activado ☀️"
                            )
                        }
                    }
                )
            }

            // ===== SECCIÓN: REPRODUCCIÓN =====
            item {
                SectionHeader("Reproducción")
            }

            item {
                SettingItemWithDialog(
                    icon = Icons.Filled.HighQuality,
                    title = "Calidad de Video",
                    subtitle = calidadVideo,
                    onClick = { showCalidadDialog = true }
                )
            }

            item {
                SettingItemSwitch(
                    icon = Icons.Filled.PlayArrow,
                    title = "Autoplay",
                    subtitle = "Reproducir siguiente episodio automáticamente",
                    checked = autoplayActivado,
                    onCheckedChange = {
                        autoplayActivado = it
                        prefs.edit().putBoolean("autoplay", it).apply()
                        scope.launch {
                            snackbarHostState.showSnackbar(
                                if (it) "Autoplay activado" else "Autoplay desactivado"
                            )
                        }
                    }
                )
            }

            item {
                SettingItemSwitch(
                    icon = Icons.Filled.PlayCircle,
                    title = "Reproducción Automática de Avances",
                    subtitle = "Reproducir trailers al navegar",
                    checked = reproduccionAutomatica,
                    onCheckedChange = {
                        reproduccionAutomatica = it
                        prefs.edit().putBoolean("reproduccion_auto", it).apply()
                    }
                )
            }

            // ===== SECCIÓN: DESCARGAS =====
            item {
                SectionHeader("Descargas")
            }

            item {
                SettingItemSwitch(
                    icon = Icons.Filled.Wifi,
                    title = "Solo descargar con Wi-Fi",
                    subtitle = "Evita consumo de datos móviles",
                    checked = descargasWifiOnly,
                    onCheckedChange = {
                        descargasWifiOnly = it
                        prefs.edit().putBoolean("descargas_wifi", it).apply()
                        scope.launch {
                            snackbarHostState.showSnackbar(
                                if (it) "Solo descargas por Wi-Fi" else "Descargas por cualquier red"
                            )
                        }
                    }
                )
            }

            // ===== SECCIÓN: NOTIFICACIONES =====
            item {
                SectionHeader("Notificaciones")
            }

            item {
                SettingItemSwitch(
                    icon = Icons.Filled.Notifications,
                    title = "Notificaciones Push",
                    subtitle = "Recibir alertas de nuevos contenidos",
                    checked = notificacionesActivadas,
                    onCheckedChange = {
                        notificacionesActivadas = it
                        prefs.edit().putBoolean("notificaciones", it).apply()
                        scope.launch {
                            snackbarHostState.showSnackbar(
                                if (it) "Notificaciones activadas" else "Notificaciones desactivadas"
                            )
                        }
                    }
                )
            }

            // ===== SECCIÓN: IDIOMA Y REGIÓN =====
            item {
                SectionHeader("Idioma y Región")
            }

            item {
                SettingItemWithDialog(
                    icon = Icons.Filled.Language,
                    title = "Idioma",
                    subtitle = idioma,
                    onClick = { showIdiomaDialog = true }
                )
            }

            // ===== SECCIÓN: ALMACENAMIENTO =====
            item {
                SectionHeader("Almacenamiento")
            }

            item {
                SettingItemAction(
                    icon = Icons.Filled.Delete,
                    title = "Limpiar Caché",
                    subtitle = "Libera espacio eliminando datos temporales",
                    onClick = { showClearCacheDialog = true },
                    isDestructive = false
                )
            }

            item {
                SettingItemAction(
                    icon = Icons.Filled.Refresh,
                    title = "Actualizar Catálogo",
                    subtitle = "Forzar actualización de películas",
                    onClick = {
                        scope.launch {
                            peliculaViewModel?.refresh()
                            snackbarHostState.showSnackbar("Actualizando catálogo...")
                        }
                    },
                    isDestructive = false
                )
            }

            // ===== SECCIÓN: INFORMACIÓN =====
            item {
                SectionHeader("Información")
            }

            item {
                SettingItemInfo(
                    icon = Icons.Filled.Info,
                    title = "Versión de la App",
                    subtitle = "1.0.0"
                )
            }

            item {
                SettingItemInfo(
                    icon = Icons.Filled.Code,
                    title = "Compilación",
                    subtitle = "Build 2025.01"
                )
            }

            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }

    // ===== DIÁLOGO DE CALIDAD =====
    if (showCalidadDialog) {
        AlertDialog(
            onDismissRequest = { showCalidadDialog = false },
            title = {
                Text(
                    "Calidad de Video",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    listOf("SD", "HD", "Full HD", "4K").forEach { calidad ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = calidadVideo == calidad,
                                onClick = {
                                    calidadVideo = calidad
                                    prefs.edit().putString("calidad_video", calidad).apply()
                                    showCalidadDialog = false
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Calidad cambiada a $calidad")
                                    }
                                },
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = MaterialTheme.colorScheme.primary,
                                    unselectedColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                                )
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(calidad, color = MaterialTheme.colorScheme.onBackground)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showCalidadDialog = false }) {
                    Text("Cerrar", color = MaterialTheme.colorScheme.primary)
                }
            },
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(16.dp)
        )
    }

    // ===== DIÁLOGO DE IDIOMA =====
    if (showIdiomaDialog) {
        AlertDialog(
            onDismissRequest = { showIdiomaDialog = false },
            title = {
                Text(
                    "Seleccionar Idioma",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    listOf("Español", "English", "Français", "Português").forEach { lang ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = idioma == lang,
                                onClick = {
                                    idioma = lang
                                    prefs.edit().putString("idioma", lang).apply()
                                    showIdiomaDialog = false
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Idioma cambiado a $lang")
                                    }
                                },
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = MaterialTheme.colorScheme.primary,
                                    unselectedColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                                )
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(lang, color = MaterialTheme.colorScheme.onBackground)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showIdiomaDialog = false }) {
                    Text("Cerrar", color = MaterialTheme.colorScheme.primary)
                }
            },
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(16.dp)
        )
    }

    // ===== DIÁLOGO DE LIMPIAR CACHÉ =====
    if (showClearCacheDialog) {
        AlertDialog(
            onDismissRequest = { showClearCacheDialog = false },
            title = {
                Text(
                    "Limpiar Caché",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    "¿Estás seguro de que deseas eliminar todos los datos en caché? " +
                            "Esto liberará espacio pero las películas se cargarán más lento la próxima vez.",
                    color = MaterialTheme.colorScheme.onBackground
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showClearCacheDialog = false
                        scope.launch {
                            peliculaViewModel?.clearCache()
                            favoritosViewModel?.clearCache()
                            snackbarHostState.showSnackbar("Caché eliminado exitosamente")
                        }
                    }
                ) {
                    Text("Limpiar", color = Color.Red, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearCacheDialog = false }) {
                    Text("Cancelar", color = MaterialTheme.colorScheme.primary)
                }
            },
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(16.dp)
        )
    }
}

// ===== COMPONENTES AUXILIARES CON MATERIAL THEME =====

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        color = MaterialTheme.colorScheme.primary,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
    )
}

@Composable
fun SettingItemSwitch(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = subtitle,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 13.sp
                )
            }

            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = MaterialTheme.colorScheme.primary,
                    checkedTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                )
            )
        }
    }
}

@Composable
fun SettingItemWithDialog(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
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
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = subtitle,
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Icon(
                imageVector = Icons.Filled.ChevronRight,
                contentDescription = "Cambiar",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun SettingItemAction(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    isDestructive: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
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
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = if (isDestructive) Color.Red else MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = if (isDestructive) Color.Red else MaterialTheme.colorScheme.onSurface,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = subtitle,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 13.sp
                )
            }
        }
    }
}

@Composable
fun SettingItemInfo(
    icon: ImageVector,
    title: String,
    subtitle: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = subtitle,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 13.sp
                )
            }
        }
    }
}
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
import com.example.tallerintegrador.ui.theme.DarkBlue
import com.example.tallerintegrador.ui.theme.Yellow
import kotlinx.coroutines.launch

/**
 * ✅ PANTALLA DE CONFIGURACIÓN FUNCIONAL
 *
 * Características implementadas:
 * - Gestión de caché
 * - Calidad de reproducción
 * - Notificaciones
 * - Autoplay
 * - Tema oscuro
 * - Idioma
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Configuración",
                        color = Yellow,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController?.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkBlue
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = DarkBlue
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
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
            title = { Text("Calidad de Video", color = Yellow, fontWeight = FontWeight.Bold) },
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
                                    selectedColor = Yellow,
                                    unselectedColor = Color.White.copy(alpha = 0.6f)
                                )
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(calidad, color = Color.White)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showCalidadDialog = false }) {
                    Text("Cerrar", color = Yellow)
                }
            },
            containerColor = DarkBlue,
            shape = RoundedCornerShape(16.dp)
        )
    }

    // ===== DIÁLOGO DE IDIOMA =====
    if (showIdiomaDialog) {
        AlertDialog(
            onDismissRequest = { showIdiomaDialog = false },
            title = { Text("Seleccionar Idioma", color = Yellow, fontWeight = FontWeight.Bold) },
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
                                    selectedColor = Yellow,
                                    unselectedColor = Color.White.copy(alpha = 0.6f)
                                )
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(lang, color = Color.White)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showIdiomaDialog = false }) {
                    Text("Cerrar", color = Yellow)
                }
            },
            containerColor = DarkBlue,
            shape = RoundedCornerShape(16.dp)
        )
    }

    // ===== DIÁLOGO DE LIMPIAR CACHÉ =====
    if (showClearCacheDialog) {
        AlertDialog(
            onDismissRequest = { showClearCacheDialog = false },
            title = { Text("Limpiar Caché", color = Yellow, fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "¿Estás seguro de que deseas eliminar todos los datos en caché? " +
                            "Esto liberará espacio pero las películas se cargarán más lento la próxima vez.",
                    color = Color.White
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showClearCacheDialog = false
                        scope.launch {
                            // Limpiar cachés
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
                    Text("Cancelar", color = Yellow)
                }
            },
            containerColor = DarkBlue,
            shape = RoundedCornerShape(16.dp)
        )
    }
}

// ===== COMPONENTES AUXILIARES =====

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        color = Yellow,
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
            containerColor = Color.White.copy(alpha = 0.05f)
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
                tint = Yellow,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = subtitle,
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 13.sp
                )
            }

            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Yellow,
                    checkedTrackColor = Yellow.copy(alpha = 0.5f),
                    uncheckedThumbColor = Color.Gray,
                    uncheckedTrackColor = Color.Gray.copy(alpha = 0.3f)
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
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = Yellow,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = subtitle,
                    color = Yellow.copy(alpha = 0.8f),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Icon(
                imageVector = Icons.Filled.ChevronRight,
                contentDescription = "Cambiar",
                tint = Color.White.copy(alpha = 0.5f)
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
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = if (isDestructive) Color.Red else Yellow,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = if (isDestructive) Color.Red else Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = subtitle,
                    color = Color.White.copy(alpha = 0.6f),
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
            containerColor = Color.White.copy(alpha = 0.05f)
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
                tint = Yellow,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = subtitle,
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 13.sp
                )
            }
        }
    }
}
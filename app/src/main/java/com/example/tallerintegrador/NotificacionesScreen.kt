package com.example.tallerintegrador

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.VolumeUp
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
import androidx.core.content.edit
import androidx.navigation.NavController
import com.example.tallerintegrador.ui.theme.DarkBlue
import com.example.tallerintegrador.ui.theme.Yellow
import kotlinx.coroutines.launch

/**
 * ✅ PANTALLA DE NOTIFICACIONES FUNCIONAL
 *
 * Características:
 * - Gestión completa de preferencias de notificaciones
 * - Categorías personalizables
 * - Control de frecuencia y horarios
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificacionesScreen(navController: NavController?) {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("notif_prefs", Context.MODE_PRIVATE) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Estados de notificaciones
    var notificacionesGlobales by remember {
        mutableStateOf(prefs.getBoolean("global", true))
    }
    var nuevosEstrenos by remember {
        mutableStateOf(prefs.getBoolean("nuevos_estrenos", true))
    }
    var recomendaciones by remember {
        mutableStateOf(prefs.getBoolean("recomendaciones", true))
    }
    var proximosEpisodios by remember {
        mutableStateOf(prefs.getBoolean("proximos_episodios", true))
    }
    var ofertas by remember {
        mutableStateOf(prefs.getBoolean("ofertas", false))
    }
    var actualizaciones by remember {
        mutableStateOf(prefs.getBoolean("actualizaciones", true))
    }
    var noMolestar by remember {
        mutableStateOf(prefs.getBoolean("no_molestar", false))
    }
    var notificacionesSonido by remember {
        mutableStateOf(prefs.getBoolean("sonido", true))
    }
    var notificacionesVibracion by remember {
        mutableStateOf(prefs.getBoolean("vibracion", true))
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Notificaciones",
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
            // ACTIVACIÓN GLOBAL
            item {
                SectionHeader("Control General")
            }

            item {
                NotificationSwitchItem(
                    icon = Icons.Filled.Notifications,
                    title = "Activar Notificaciones",
                    subtitle = "Habilitar todas las notificaciones",
                    checked = notificacionesGlobales,
                    onCheckedChange = {
                        notificacionesGlobales = it
                        prefs.edit { putBoolean("global", it) }
                        scope.launch {
                            snackbarHostState.showSnackbar(
                                if (it) "Notificaciones activadas"
                                else "Notificaciones desactivadas"
                            )
                        }
                    },
                    prominent = true
                )
            }

            // CATEGORÍAS
            item {
                SectionHeader("Categorías de Contenido")
            }

            item {
                NotificationSwitchItem(
                    icon = Icons.Filled.NewReleases,
                    title = "Nuevos Estrenos",
                    subtitle = "Películas y series recién agregadas",
                    checked = nuevosEstrenos,
                    onCheckedChange = {
                        nuevosEstrenos = it
                        prefs.edit { putBoolean("nuevos_estrenos", it) }
                    },
                    enabled = notificacionesGlobales
                )
            }

            item {
                NotificationSwitchItem(
                    icon = Icons.Filled.Recommend,
                    title = "Recomendaciones",
                    subtitle = "Contenido personalizado para ti",
                    checked = recomendaciones,
                    onCheckedChange = {
                        recomendaciones = it
                        prefs.edit { putBoolean("recomendaciones", it) }
                    },
                    enabled = notificacionesGlobales
                )
            }

            item {
                NotificationSwitchItem(
                    icon = Icons.Filled.PlayCircle,
                    title = "Próximos Episodios",
                    subtitle = "Nuevos capítulos de series que sigues",
                    checked = proximosEpisodios,
                    onCheckedChange = {
                        proximosEpisodios = it
                        prefs.edit { putBoolean("proximos_episodios", it) }
                    },
                    enabled = notificacionesGlobales
                )
            }

            item {
                NotificationSwitchItem(
                    icon = Icons.Filled.LocalOffer,
                    title = "Ofertas y Promociones",
                    subtitle = "Descuentos especiales",
                    checked = ofertas,
                    onCheckedChange = {
                        ofertas = it
                        prefs.edit { putBoolean("ofertas", it) }
                    },
                    enabled = notificacionesGlobales
                )
            }

            item {
                NotificationSwitchItem(
                    icon = Icons.Filled.Update,
                    title = "Actualizaciones de la App",
                    subtitle = "Nuevas funciones y mejoras",
                    checked = actualizaciones,
                    onCheckedChange = {
                        actualizaciones = it
                        prefs.edit { putBoolean("actualizaciones", it) }
                    },
                    enabled = notificacionesGlobales
                )
            }

            // PREFERENCIAS
            item {
                SectionHeader("Preferencias")
            }

            item {
                NotificationSwitchItem(
                    icon = Icons.Filled.DoNotDisturb,
                    title = "Modo No Molestar",
                    subtitle = "22:00 - 08:00",
                    checked = noMolestar,
                    onCheckedChange = {
                        noMolestar = it
                        prefs.edit { putBoolean("no_molestar", it) }
                        scope.launch {
                            snackbarHostState.showSnackbar(
                                if (it) "No Molestar: 22:00 - 08:00"
                                else "No Molestar desactivado"
                            )
                        }
                    },
                    enabled = notificacionesGlobales
                )
            }

            item {
                NotificationSwitchItem(
                    icon = Icons.AutoMirrored.Filled.VolumeUp,
                    title = "Sonido",
                    subtitle = "Reproducir sonido de notificación",
                    checked = notificacionesSonido,
                    onCheckedChange = {
                        notificacionesSonido = it
                        prefs.edit { putBoolean("sonido", it) }
                    },
                    enabled = notificacionesGlobales
                )
            }

            item {
                NotificationSwitchItem(
                    icon = Icons.Filled.Vibration,
                    title = "Vibración",
                    subtitle = "Vibrar al recibir notificación",
                    checked = notificacionesVibracion,
                    onCheckedChange = {
                        notificacionesVibracion = it
                        prefs.edit { putBoolean("vibracion", it) }
                    },
                    enabled = notificacionesGlobales
                )
            }

            // INFORMACIÓN
            item {
                SectionHeader("Información")
            }

            item {
                InfoCard(
                    icon = Icons.Filled.Info,
                    title = "Acerca de las notificaciones",
                    description = "Las notificaciones te mantienen al día con nuevo contenido, " +
                            "recomendaciones personalizadas y actualizaciones importantes. " +
                            "Puedes personalizar qué tipo de notificaciones deseas recibir."
                )
            }

            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun NotificationSwitchItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    enabled: Boolean = true,
    prominent: Boolean = false
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (prominent)
                Yellow.copy(alpha = 0.1f)
            else
                Color.White.copy(alpha = 0.05f)
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
                tint = if (enabled) Yellow else Color.Gray,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = if (enabled) Color.White else Color.Gray,
                    fontSize = 16.sp,
                    fontWeight = if (prominent) FontWeight.Bold else FontWeight.Medium
                )
                Text(
                    text = subtitle,
                    color = if (enabled)
                        Color.White.copy(alpha = 0.6f)
                    else
                        Color.Gray.copy(alpha = 0.4f),
                    fontSize = 13.sp
                )
            }

            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                enabled = enabled,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Yellow,
                    checkedTrackColor = Yellow.copy(alpha = 0.5f),
                    uncheckedThumbColor = Color.Gray,
                    uncheckedTrackColor = Color.Gray.copy(alpha = 0.3f),
                    disabledCheckedThumbColor = Color.Gray,
                    disabledUncheckedThumbColor = Color.DarkGray
                )
            )
        }
    }
}

@Composable
fun InfoCard(
    icon: ImageVector,
    title: String,
    description: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Yellow.copy(alpha = 0.15f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = Yellow,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = title,
                    color = Yellow,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )
            }
        }
    }
}

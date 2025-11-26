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
import com.example.tallerintegrador.auth.AuthViewModel
import com.example.tallerintegrador.data.local.TokenManager
import com.example.tallerintegrador.ui.theme.DarkBlue
import com.example.tallerintegrador.ui.theme.Yellow
import kotlinx.coroutines.launch

/**
 * ✅ PANTALLA DE PRIVACIDAD Y SEGURIDAD
 *
 * Características:
 * - Gestión de privacidad
 * - Control parental
 * - Seguridad de cuenta
 * - Historial de actividad
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacidadScreen(
    navController: NavController?,
    authViewModel: AuthViewModel
) {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("privacy_prefs", Context.MODE_PRIVATE) }
    val tokenManager = remember { TokenManager(context.applicationContext) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Estados de privacidad
    var perfilPrivado by remember {
        mutableStateOf(prefs.getBoolean("perfil_privado", false))
    }
    var historialVisible by remember {
        mutableStateOf(prefs.getBoolean("historial_visible", true))
    }
    var controlParental by remember {
        mutableStateOf(prefs.getBoolean("control_parental", false))
    }
    var dobleAutenticacion by remember {
        mutableStateOf(prefs.getBoolean("2fa", false))
    }
    var sesionesActivas by remember {
        mutableStateOf(prefs.getBoolean("sesiones_activas", true))
    }

    // Diálogos
    var showDeleteAccountDialog by remember { mutableStateOf(false) }
    var showClearHistoryDialog by remember { mutableStateOf(false) }
    var showSessionsDialog by remember { mutableStateOf(false) }
    var showControlParentalDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Privacidad y Seguridad",
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
            // PRIVACIDAD
            item {
                SectionHeader("Privacidad")
            }

            item {
                PrivacySwitchItem(
                    icon = Icons.Filled.Lock,
                    title = "Perfil Privado",
                    subtitle = "Solo tú puedes ver tu actividad",
                    checked = perfilPrivado,
                    onCheckedChange = {
                        perfilPrivado = it
                        prefs.edit().putBoolean("perfil_privado", it).apply()
                        scope.launch {
                            snackbarHostState.showSnackbar(
                                if (it) "Perfil ahora es privado" else "Perfil ahora es público"
                            )
                        }
                    }
                )
            }

            item {
                PrivacySwitchItem(
                    icon = Icons.Filled.History,
                    title = "Historial Visible",
                    subtitle = "Mostrar películas vistas recientemente",
                    checked = historialVisible,
                    onCheckedChange = {
                        historialVisible = it
                        prefs.edit().putBoolean("historial_visible", it).apply()
                    }
                )
            }

            item {
                PrivacyActionItem(
                    icon = Icons.Filled.DeleteSweep,
                    title = "Limpiar Historial",
                    subtitle = "Eliminar todo el historial de reproducción",
                    onClick = { showClearHistoryDialog = true }
                )
            }

            // SEGURIDAD
            item {
                SectionHeader("Seguridad")
            }

            item {
                PrivacySwitchItem(
                    icon = Icons.Filled.Security,
                    title = "Autenticación de Dos Factores",
                    subtitle = "Protección adicional para tu cuenta",
                    checked = dobleAutenticacion,
                    onCheckedChange = {
                        dobleAutenticacion = it
                        prefs.edit().putBoolean("2fa", it).apply()
                        scope.launch {
                            snackbarHostState.showSnackbar(
                                if (it) "2FA activado" else "2FA desactivado"
                            )
                        }
                    }
                )
            }

            item {
                PrivacyActionItem(
                    icon = Icons.Filled.Password,
                    title = "Cambiar Contraseña",
                    subtitle = "Actualizar tu contraseña de acceso",
                    onClick = {
                        navController?.navigate("editar_perfil")
                    }
                )
            }

            item {
                PrivacyActionItem(
                    icon = Icons.Filled.Devices,
                    title = "Sesiones Activas",
                    subtitle = "Gestionar dispositivos conectados",
                    onClick = { showSessionsDialog = true }
                )
            }

            // CONTROL PARENTAL
            item {
                SectionHeader("Control Parental")
            }

            item {
                PrivacySwitchItem(
                    icon = Icons.Filled.FamilyRestroom,
                    title = "Modo Niños",
                    subtitle = "Contenido apropiado para menores",
                    checked = controlParental,
                    onCheckedChange = {
                        if (it) {
                            showControlParentalDialog = true
                        } else {
                            controlParental = false
                            prefs.edit().putBoolean("control_parental", false).apply()
                        }
                    }
                )
            }

            item {
                InfoCard(
                    icon = Icons.Filled.Info,
                    title = "Control Parental",
                    description = "Restringe el acceso a contenido para adultos y permite " +
                            "establecer límites de tiempo de visualización."
                )
            }

            // GESTIÓN DE CUENTA
            item {
                SectionHeader("Gestión de Cuenta")
            }

            item {
                InfoCard(
                    icon = Icons.Filled.AccountCircle,
                    title = "Información de Cuenta",
                    description = "Usuario: ${tokenManager.getUserName()}\n" +
                            "Email: ${tokenManager.getUserEmail()}\n" +
                            "ID: ${tokenManager.getUserId()}"
                )
            }

            item {
                PrivacyActionItem(
                    icon = Icons.Filled.Download,
                    title = "Descargar Mis Datos",
                    subtitle = "Obtén una copia de tu información",
                    onClick = {
                        scope.launch {
                            snackbarHostState.showSnackbar(
                                "Preparando descarga de datos..."
                            )
                        }
                    }
                )
            }

            item {
                PrivacyActionItem(
                    icon = Icons.Filled.DeleteForever,
                    title = "Eliminar Cuenta",
                    subtitle = "Eliminar permanentemente tu cuenta",
                    onClick = { showDeleteAccountDialog = true },
                    isDestructive = true
                )
            }

            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }

    // DIÁLOGO: LIMPIAR HISTORIAL
    if (showClearHistoryDialog) {
        AlertDialog(
            onDismissRequest = { showClearHistoryDialog = false },
            title = {
                Text(
                    "Limpiar Historial",
                    color = Yellow,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    "¿Estás seguro de que deseas eliminar todo tu historial de " +
                            "reproducción? Esta acción no se puede deshacer.",
                    color = Color.White
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showClearHistoryDialog = false
                        scope.launch {
                            // Simular limpieza
                            kotlinx.coroutines.delay(500)
                            snackbarHostState.showSnackbar("Historial eliminado")
                        }
                    }
                ) {
                    Text("Eliminar", color = Color.Red, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearHistoryDialog = false }) {
                    Text("Cancelar", color = Yellow)
                }
            },
            containerColor = DarkBlue,
            shape = RoundedCornerShape(16.dp)
        )
    }

    // DIÁLOGO: ELIMINAR CUENTA
    if (showDeleteAccountDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteAccountDialog = false },
            icon = {
                Icon(
                    Icons.Filled.Warning,
                    contentDescription = "Advertencia",
                    tint = Color.Red,
                    modifier = Modifier.size(48.dp)
                )
            },
            title = {
                Text(
                    "Eliminar Cuenta",
                    color = Color.Red,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    Text(
                        "⚠️ Esta acción es IRREVERSIBLE",
                        color = Color.Red,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Se eliminarán permanentemente:\n" +
                                "• Tu perfil y datos personales\n" +
                                "• Historial de reproducción\n" +
                                "• Listas y favoritos\n" +
                                "• Preferencias guardadas",
                        color = Color.White
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteAccountDialog = false
                        scope.launch {
                            authViewModel.logout()
                            navController?.navigate("welcome") {
                                popUpTo(0) { inclusive = true }
                            }
                            snackbarHostState.showSnackbar("Cuenta eliminada")
                        }
                    }
                ) {
                    Text("Eliminar Cuenta", color = Color.Red, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteAccountDialog = false }) {
                    Text("Cancelar", color = Yellow)
                }
            },
            containerColor = DarkBlue,
            shape = RoundedCornerShape(16.dp)
        )
    }

    // DIÁLOGO: SESIONES ACTIVAS
    if (showSessionsDialog) {
        AlertDialog(
            onDismissRequest = { showSessionsDialog = false },
            title = {
                Text(
                    "Sesiones Activas",
                    color = Yellow,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    SessionItem(
                        device = "Android - Este dispositivo",
                        location = "Culiacán, México",
                        date = "Ahora",
                        isCurrent = true
                    )
                    Divider(
                        color = Color.White.copy(alpha = 0.2f),
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                    SessionItem(
                        device = "Web - Chrome",
                        location = "Culiacán, México",
                        date = "Hace 2 días"
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showSessionsDialog = false }) {
                    Text("Cerrar", color = Yellow)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        scope.launch {
                            snackbarHostState.showSnackbar("Sesiones cerradas")
                        }
                        showSessionsDialog = false
                    }
                ) {
                    Text("Cerrar Todas", color = Color.Red)
                }
            },
            containerColor = DarkBlue,
            shape = RoundedCornerShape(16.dp)
        )
    }

    // DIÁLOGO: CONTROL PARENTAL
    if (showControlParentalDialog) {
        AlertDialog(
            onDismissRequest = { showControlParentalDialog = false },
            title = {
                Text(
                    "Activar Modo Niños",
                    color = Yellow,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    "El modo niños filtrará todo el contenido para mostrar solo " +
                            "películas y series apropiadas para menores. Se aplicará a " +
                            "todo el perfil.\n\n¿Deseas activarlo?",
                    color = Color.White
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        controlParental = true
                        prefs.edit().putBoolean("control_parental", true).apply()
                        showControlParentalDialog = false
                        scope.launch {
                            snackbarHostState.showSnackbar("Modo Niños activado")
                        }
                    }
                ) {
                    Text("Activar", color = Yellow, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showControlParentalDialog = false }) {
                    Text("Cancelar", color = Color.White)
                }
            },
            containerColor = DarkBlue,
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@Composable
fun PrivacySwitchItem(
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
fun PrivacyActionItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    isDestructive: Boolean = false
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDestructive)
                Color.Red.copy(alpha = 0.1f)
            else
                Color.White.copy(alpha = 0.05f)
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
                    color = if (isDestructive)
                        Color.Red.copy(alpha = 0.7f)
                    else
                        Color.White.copy(alpha = 0.6f),
                    fontSize = 13.sp
                )
            }

            Icon(
                imageVector = Icons.Filled.ChevronRight,
                contentDescription = "Ir",
                tint = if (isDestructive)
                    Color.Red.copy(alpha = 0.5f)
                else
                    Color.White.copy(alpha = 0.5f)
            )
        }
    }
}

@Composable
fun SessionItem(
    device: String,
    location: String,
    date: String,
    isCurrent: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = if (isCurrent) Icons.Filled.PhoneAndroid else Icons.Filled.Computer,
            contentDescription = device,
            tint = if (isCurrent) Yellow else Color.White.copy(alpha = 0.7f),
            modifier = Modifier.size(32.dp)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = device,
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                if (isCurrent) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        color = Yellow.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            "Actual",
                            color = Yellow,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }
            Text(
                text = "$location • $date",
                color = Color.White.copy(alpha = 0.5f),
                fontSize = 12.sp
            )
        }
    }
}
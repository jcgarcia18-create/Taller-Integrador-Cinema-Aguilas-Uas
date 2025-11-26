package com.example.tallerintegrador

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.tallerintegrador.auth.AuthViewModel
import com.example.tallerintegrador.data.local.TokenManager
import com.example.tallerintegrador.feature.favoritos.FavoritosViewModel
import com.example.tallerintegrador.ui.theme.DarkBlue
import com.example.tallerintegrador.ui.theme.Yellow

/**
 * ✅ PERFIL FUNCIONAL
 * - Carga datos reales del usuario
 * - Muestra estadísticas dinámicas
 * - Gestiona cierre de sesión correctamente
 */
@Composable
fun PerfilScreen(
    navController: NavController?,
    authViewModel: AuthViewModel,
    favoritosViewModel: FavoritosViewModel
) {
    var showLogoutDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val tokenManager = remember { TokenManager(context.applicationContext) }

    // ✅ Obtener información del usuario
    val userName = tokenManager.getUserName() ?: "Usuario"
    val userEmail = tokenManager.getUserEmail() ?: "usuario@ejemplo.com"
    val userId = tokenManager.getUserId()

    // ✅ Estadísticas reactivas
    val favoritos by favoritosViewModel.favoritos.collectAsState()
    val totalFavoritos = favoritos.size

    // ✅ Cargar favoritos al entrar
    LaunchedEffect(Unit) {
        favoritosViewModel.cargarFavoritos()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBlue)
    ) {
        // ===== HEADER DEL PERFIL =====
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Yellow.copy(alpha = 0.1f))
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(Yellow),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Person,
                    contentDescription = "Avatar",
                    tint = DarkBlue,
                    modifier = Modifier.size(60.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Nombre del usuario
            Text(
                text = userName,
                color = Yellow,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            // Email del usuario
            Text(
                text = userEmail,
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // ✅ ESTADÍSTICAS DINÁMICAS
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(label = "Usuario ID", value = userId.toString())

                VerticalDivider(
                    modifier = Modifier
                        .width(1.dp)
                        .height(40.dp),
                    color = Color.White.copy(alpha = 0.3f)
                )

                StatItem(label = "Favoritas", value = totalFavoritos.toString())

                VerticalDivider(
                    modifier = Modifier
                        .width(1.dp)
                        .height(40.dp),
                    color = Color.White.copy(alpha = 0.3f)
                )

                StatItem(label = "Listas", value = "1")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // ===== OPCIONES DEL PERFIL =====
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            item {
                ProfileOption(
                    icon = Icons.Filled.Person,
                    title = "Editar Perfil",
                    subtitle = "Actualiza tu información personal",
                    onClick = {
                        navController?.navigate("editar_perfil")
                    }
                )
            }

            item {
                ProfileOption(
                    icon = Icons.Filled.Favorite,
                    title = "Mis Favoritas",
                    subtitle = "$totalFavoritos películas guardadas",
                    onClick = {
                        navController?.navigate("home")
                        // Cambiar a tab de favoritos (necesitarías pasar el tab como argumento)
                    }
                )
            }

            item {
                ProfileOption(
                    icon = Icons.Filled.Notifications,
                    title = "Notificaciones",
                    subtitle = "Configura tus preferencias",
                    onClick = {
                        navController?.navigate("notificaciones")
                    }
                )
            }

            item {
                ProfileOption(
                    icon = Icons.Filled.Lock,
                    title = "Privacidad y Seguridad",
                    subtitle = "Gestiona tu cuenta",
                    onClick = {
                        navController?.navigate("privacidad")
                    }
                )
            }

            item {
                ProfileOption(
                    icon = Icons.Filled.Settings,
                    title = "Configuración",
                    subtitle = "Ajustes de la aplicación",
                    onClick = {
                        navController?.navigate("configuracion")
                    }
                )
            }

            item {
                ProfileOption(
                    icon = Icons.Filled.Info,
                    title = "Acerca de",
                    subtitle = "Versión 1.0.0",
                    onClick = {
                        navController?.navigate("acerca_de")
                    }
                )
            }

            item {
                ProfileOption(
                    icon = Icons.AutoMirrored.Filled.ExitToApp,
                    title = "Cerrar Sesión",
                    subtitle = "Salir de tu cuenta",
                    onClick = { showLogoutDialog = true },
                    isDestructive = true
                )
            }

            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }

    // ===== DIÁLOGO DE CONFIRMACIÓN =====
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = {
                Text(
                    text = "Cerrar Sesión",
                    color = Yellow,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    Text(
                        text = "¿Estás seguro que deseas cerrar sesión?",
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Tendrás que iniciar sesión nuevamente para acceder a tus favoritos.",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 14.sp
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLogoutDialog = false

                        // ✅ CERRAR SESIÓN COMPLETO
                        authViewModel.logout()
                        favoritosViewModel.clearCache()

                        // ✅ Navegar y limpiar stack
                        navController?.navigate("welcome") {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                ) {
                    Text("Cerrar Sesión", color = Color.Red, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancelar", color = Yellow)
                }
            },
            containerColor = DarkBlue,
            shape = RoundedCornerShape(16.dp)
        )
    }
}

/**
 * ✅ Componente de estadística
 */
@Composable
fun StatItem(label: String, value: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            color = Yellow,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            color = Color.White.copy(alpha = 0.7f),
            fontSize = 14.sp
        )
    }
}

/**
 * ✅ Componente de opción del perfil
 */
@Composable
fun ProfileOption(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    isDestructive: Boolean = false
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.05f)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        ),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Ícono
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(
                        if (isDestructive)
                            Color.Red.copy(alpha = 0.2f)
                        else
                            Yellow.copy(alpha = 0.2f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = if (isDestructive) Color.Red else Yellow,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Texto
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    color = if (isDestructive) Color.Red else Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subtitle,
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 13.sp
                )
            }

            // Flecha
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "Ir",
                tint = Color.White.copy(alpha = 0.5f)
            )
        }
    }
}
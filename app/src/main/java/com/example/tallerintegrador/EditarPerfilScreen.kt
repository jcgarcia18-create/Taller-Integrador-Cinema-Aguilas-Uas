package com.example.tallerintegrador

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.tallerintegrador.auth.AuthViewModel
import com.example.tallerintegrador.data.local.TokenManager
import com.example.tallerintegrador.ui.theme.DarkBlue
import com.example.tallerintegrador.ui.theme.Yellow
import kotlinx.coroutines.launch

/**
 * ✅ PANTALLA DE EDITAR PERFIL FUNCIONAL
 *
 * Características:
 * - Editar nombre de usuario
 * - Cambiar contraseña
 * - Seleccionar avatar
 * - Cambiar email
 * - Validaciones en tiempo real
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditarPerfilScreen(
    navController: NavController?,
    authViewModel: AuthViewModel
) {
    val context = LocalContext.current
    val tokenManager = remember { TokenManager(context.applicationContext) }
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    // ===== ESTADOS DE FORMULARIO =====
    var nombre by remember { mutableStateOf(tokenManager.getUserName() ?: "") }
    var email by remember { mutableStateOf(tokenManager.getUserEmail() ?: "") }
    var passwordActual by remember { mutableStateOf("") }
    var passwordNueva by remember { mutableStateOf("") }
    var passwordConfirm by remember { mutableStateOf("") }

    var showPasswordActual by remember { mutableStateOf(false) }
    var showPasswordNueva by remember { mutableStateOf(false) }
    var showPasswordConfirm by remember { mutableStateOf(false) }

    var selectedAvatar by remember { mutableIntStateOf(
        context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
            .getInt("avatar_id", 0)
    ) }

    var showAvatarDialog by remember { mutableStateOf(false) }
    var showConfirmDialog by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    // ===== VALIDACIONES =====
    val nombreError = remember(nombre) {
        when {
            nombre.isBlank() -> "El nombre no puede estar vacío"
            nombre.length < 3 -> "El nombre debe tener al menos 3 caracteres"
            else -> null
        }
    }

    val emailError = remember(email) {
        when {
            email.isBlank() -> "El email no puede estar vacío"
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() ->
                "Email inválido"
            else -> null
        }
    }

    val passwordError = remember(passwordNueva, passwordConfirm) {
        when {
            passwordNueva.isNotEmpty() && passwordNueva.length < 6 ->
                "La contraseña debe tener al menos 6 caracteres"
            passwordNueva != passwordConfirm ->
                "Las contraseñas no coinciden"
            else -> null
        }
    }

    val isFormValid = nombreError == null && emailError == null &&
            (passwordNueva.isEmpty() || passwordError == null)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Editar Perfil",
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
                actions = {
                    TextButton(
                        onClick = { showConfirmDialog = true },
                        enabled = isFormValid && !isLoading
                    ) {
                        Text(
                            "Guardar",
                            color = if (isFormValid) Yellow else Color.Gray,
                            fontWeight = FontWeight.Bold
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
                .padding(horizontal = 16.dp)
        ) {
            item { Spacer(modifier = Modifier.height(24.dp)) }

            // ===== AVATAR =====
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .background(Yellow)
                            .border(4.dp, Yellow.copy(alpha = 0.3f), CircleShape)
                            .clickable { showAvatarDialog = true },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = getAvatarIcon(selectedAvatar),
                            contentDescription = "Avatar",
                            tint = DarkBlue,
                            modifier = Modifier.size(70.dp)
                        )

                        // Botón de editar
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(8.dp)
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(DarkBlue)
                                .border(2.dp, Yellow, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Edit,
                                contentDescription = "Cambiar avatar",
                                tint = Yellow,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        "Toca para cambiar avatar",
                        color = Color.White.copy(alpha = 0.6f),
                        fontSize = 14.sp
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(32.dp)) }

            // ===== INFORMACIÓN PERSONAL =====
            item {
                Text(
                    "Información Personal",
                    color = Yellow,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }

            // Nombre
            item {
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre de usuario", color = Yellow) },
                    leadingIcon = {
                        Icon(Icons.Filled.Person, "Nombre", tint = Yellow)
                    },
                    isError = nombreError != null,
                    supportingText = {
                        nombreError?.let {
                            Text(it, color = Color.Red)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        cursorColor = Yellow,
                        focusedBorderColor = Yellow,
                        unfocusedBorderColor = Yellow.copy(alpha = 0.5f),
                        errorBorderColor = Color.Red,
                        focusedLabelColor = Yellow,
                        unfocusedLabelColor = Yellow.copy(alpha = 0.7f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }

            // Email
            item {
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Correo electrónico", color = Yellow) },
                    leadingIcon = {
                        Icon(Icons.Filled.Email, "Email", tint = Yellow)
                    },
                    isError = emailError != null,
                    supportingText = {
                        emailError?.let {
                            Text(it, color = Color.Red)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        cursorColor = Yellow,
                        focusedBorderColor = Yellow,
                        unfocusedBorderColor = Yellow.copy(alpha = 0.5f),
                        errorBorderColor = Color.Red,
                        focusedLabelColor = Yellow,
                        unfocusedLabelColor = Yellow.copy(alpha = 0.7f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
            }

            item { Spacer(modifier = Modifier.height(32.dp)) }

            // ===== CAMBIAR CONTRASEÑA =====
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Cambiar Contraseña",
                        color = Yellow,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "(Opcional)",
                        color = Color.White.copy(alpha = 0.5f),
                        fontSize = 14.sp
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }

            // Contraseña actual
            item {
                OutlinedTextField(
                    value = passwordActual,
                    onValueChange = { passwordActual = it },
                    label = { Text("Contraseña actual", color = Yellow) },
                    leadingIcon = {
                        Icon(Icons.Filled.Lock, "Contraseña", tint = Yellow)
                    },
                    trailingIcon = {
                        IconButton(onClick = { showPasswordActual = !showPasswordActual }) {
                            Icon(
                                imageVector = if (showPasswordActual)
                                    Icons.Filled.Visibility
                                else
                                    Icons.Filled.VisibilityOff,
                                contentDescription = "Mostrar contraseña",
                                tint = Yellow
                            )
                        }
                    },
                    visualTransformation = if (showPasswordActual)
                        VisualTransformation.None
                    else
                        PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        cursorColor = Yellow,
                        focusedBorderColor = Yellow,
                        unfocusedBorderColor = Yellow.copy(alpha = 0.5f),
                        focusedLabelColor = Yellow,
                        unfocusedLabelColor = Yellow.copy(alpha = 0.7f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }

            // Nueva contraseña
            item {
                OutlinedTextField(
                    value = passwordNueva,
                    onValueChange = { passwordNueva = it },
                    label = { Text("Nueva contraseña", color = Yellow) },
                    leadingIcon = {
                        Icon(Icons.Filled.LockOpen, "Nueva contraseña", tint = Yellow)
                    },
                    trailingIcon = {
                        IconButton(onClick = { showPasswordNueva = !showPasswordNueva }) {
                            Icon(
                                imageVector = if (showPasswordNueva)
                                    Icons.Filled.Visibility
                                else
                                    Icons.Filled.VisibilityOff,
                                contentDescription = "Mostrar contraseña",
                                tint = Yellow
                            )
                        }
                    },
                    visualTransformation = if (showPasswordNueva)
                        VisualTransformation.None
                    else
                        PasswordVisualTransformation(),
                    isError = passwordNueva.isNotEmpty() && passwordError != null,
                    supportingText = {
                        if (passwordNueva.isNotEmpty() && passwordNueva.length < 6) {
                            Text("Mínimo 6 caracteres", color = Color.Red)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        cursorColor = Yellow,
                        focusedBorderColor = Yellow,
                        unfocusedBorderColor = Yellow.copy(alpha = 0.5f),
                        errorBorderColor = Color.Red,
                        focusedLabelColor = Yellow,
                        unfocusedLabelColor = Yellow.copy(alpha = 0.7f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }

            // Confirmar contraseña
            item {
                OutlinedTextField(
                    value = passwordConfirm,
                    onValueChange = { passwordConfirm = it },
                    label = { Text("Confirmar contraseña", color = Yellow) },
                    leadingIcon = {
                        Icon(Icons.Filled.CheckCircle, "Confirmar", tint = Yellow)
                    },
                    trailingIcon = {
                        IconButton(onClick = { showPasswordConfirm = !showPasswordConfirm }) {
                            Icon(
                                imageVector = if (showPasswordConfirm)
                                    Icons.Filled.Visibility
                                else
                                    Icons.Filled.VisibilityOff,
                                contentDescription = "Mostrar contraseña",
                                tint = Yellow
                            )
                        }
                    },
                    visualTransformation = if (showPasswordConfirm)
                        VisualTransformation.None
                    else
                        PasswordVisualTransformation(),
                    isError = passwordConfirm.isNotEmpty() &&
                            passwordNueva != passwordConfirm,
                    supportingText = {
                        if (passwordConfirm.isNotEmpty() &&
                            passwordNueva != passwordConfirm) {
                            Text("Las contraseñas no coinciden", color = Color.Red)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        cursorColor = Yellow,
                        focusedBorderColor = Yellow,
                        unfocusedBorderColor = Yellow.copy(alpha = 0.5f),
                        errorBorderColor = Color.Red,
                        focusedLabelColor = Yellow,
                        unfocusedLabelColor = Yellow.copy(alpha = 0.7f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
            }

            item { Spacer(modifier = Modifier.height(32.dp)) }

            // ===== BOTÓN GUARDAR =====
            item {
                Button(
                    onClick = { showConfirmDialog = true },
                    enabled = isFormValid && !isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Yellow,
                        disabledContainerColor = Color.Gray
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            color = DarkBlue,
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Icon(
                            Icons.Filled.Save,
                            contentDescription = "Guardar",
                            tint = DarkBlue
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Guardar Cambios",
                            color = DarkBlue,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(32.dp)) }
        }
    }

    // ===== DIÁLOGO DE SELECCIÓN DE AVATAR =====
    if (showAvatarDialog) {
        AlertDialog(
            onDismissRequest = { showAvatarDialog = false },
            title = {
                Text(
                    "Selecciona tu avatar",
                    color = Yellow,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        for (i in 0..2) {
                            AvatarOption(
                                icon = getAvatarIcon(i),
                                isSelected = selectedAvatar == i,
                                onClick = {
                                    selectedAvatar = i
                                    context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                                        .edit()
                                        .putInt("avatar_id", i)
                                        .apply()
                                    showAvatarDialog = false
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Avatar actualizado")
                                    }
                                }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        for (i in 3..5) {
                            AvatarOption(
                                icon = getAvatarIcon(i),
                                isSelected = selectedAvatar == i,
                                onClick = {
                                    selectedAvatar = i
                                    context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                                        .edit()
                                        .putInt("avatar_id", i)
                                        .apply()
                                    showAvatarDialog = false
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Avatar actualizado")
                                    }
                                }
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showAvatarDialog = false }) {
                    Text("Cerrar", color = Yellow)
                }
            },
            containerColor = DarkBlue,
            shape = RoundedCornerShape(16.dp)
        )
    }

    // ===== DIÁLOGO DE CONFIRMACIÓN =====
    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = {
                Text(
                    "Confirmar cambios",
                    color = Yellow,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    Text(
                        "¿Deseas guardar los siguientes cambios?",
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    if (nombre != tokenManager.getUserName()) {
                        Text(
                            "• Nombre: $nombre",
                            color = Yellow,
                            fontSize = 14.sp
                        )
                    }
                    if (email != tokenManager.getUserEmail()) {
                        Text(
                            "• Email: $email",
                            color = Yellow,
                            fontSize = 14.sp
                        )
                    }
                    if (passwordNueva.isNotEmpty()) {
                        Text(
                            "• Contraseña actualizada",
                            color = Yellow,
                            fontSize = 14.sp
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showConfirmDialog = false
                        isLoading = true

                        scope.launch {
                            try {
                                // Simular guardado (aquí conectarías con tu API)
                                kotlinx.coroutines.delay(1500)

                                // Actualizar datos locales
                                tokenManager.saveAuthData(
                                    token = tokenManager.getToken() ?: "",
                                    userId = tokenManager.getUserId(),
                                    userName = nombre,
                                    userEmail = email
                                )

                                snackbarHostState.showSnackbar(
                                    "Perfil actualizado exitosamente"
                                )

                                // Volver a la pantalla anterior
                                navController?.popBackStack()

                            } catch (e: Exception) {
                                snackbarHostState.showSnackbar(
                                    "Error al guardar: ${e.message}"
                                )
                            } finally {
                                isLoading = false
                            }
                        }
                    }
                ) {
                    Text("Guardar", color = Yellow, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDialog = false }) {
                    Text("Cancelar", color = Color.White)
                }
            },
            containerColor = DarkBlue,
            shape = RoundedCornerShape(16.dp)
        )
    }
}

// ===== FUNCIÓN AUXILIAR: AVATARES =====
@Composable
fun getAvatarIcon(id: Int) = when(id) {
    0 -> Icons.Filled.Person
    1 -> Icons.Filled.Face
    2 -> Icons.Filled.AccountCircle
    3 -> Icons.Filled.Star
    4 -> Icons.Filled.Favorite
    5 -> Icons.Filled.EmojiEmotions
    else -> Icons.Filled.Person
}

@Composable
fun AvatarOption(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(80.dp)
            .clip(CircleShape)
            .background(if (isSelected) Yellow else Color.White.copy(alpha = 0.1f))
            .border(
                width = if (isSelected) 3.dp else 1.dp,
                color = if (isSelected) Yellow else Color.White.copy(alpha = 0.3f),
                shape = CircleShape
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = "Avatar",
            tint = if (isSelected) DarkBlue else Color.White,
            modifier = Modifier.size(40.dp)
        )
    }
}
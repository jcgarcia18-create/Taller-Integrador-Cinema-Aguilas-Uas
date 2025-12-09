package com.example.tallerintegrador

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.tallerintegrador.auth.AuthViewModel
import com.example.tallerintegrador.auth.state.AuthState
import com.example.tallerintegrador.data.model.*
import com.example.tallerintegrador.ui.theme.TallerIntegradorTheme
import kotlinx.coroutines.launch

@Composable
fun RegisterScreen(navController: NavController, authViewModel: AuthViewModel = viewModel()) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    var showConfirmPassword by remember { mutableStateOf(false) }

    val authState by authViewModel.authState
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Validaciones
    val nameError = remember(name) {
        when {
            name.isBlank() -> "El nombre es obligatorio"
            name.length < 3 -> "El nombre debe tener al menos 3 caracteres"
            else -> null
        }
    }

    val emailError = remember(email) {
        when {
            email.isBlank() -> "El email es obligatorio"
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> "Email inválido"
            else -> null
        }
    }

    val passwordError = remember(password) {
        when {
            password.isBlank() -> "La contraseña es obligatoria"
            password.length < 6 -> "Mínimo 6 caracteres"
            else -> null
        }
    }

    val confirmPasswordError = remember(password, confirmPassword) {
        when {
            confirmPassword.isBlank() -> "Confirma tu contraseña"
            password != confirmPassword -> "Las contraseñas no coinciden"
            else -> null
        }
    }

    val isFormValid = nameError == null && emailError == null &&
            passwordError == null && confirmPasswordError == null &&
            name.isNotBlank() && email.isNotBlank() &&
            password.isNotBlank() && confirmPassword.isNotBlank()

    LaunchedEffect(authState) {
        when (val state = authState) {
            is AuthState.Success -> {
                navController.navigate("home") {
                    popUpTo(navController.graph.startDestinationId) { inclusive = true }
                }
            }
            is AuthState.Error -> {
                scope.launch {
                    snackbarHostState.showSnackbar(state.message)
                }
            }
            else -> {}
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Fondo con imagen
            Image(
                painter = painterResource(id = R.drawable.login_background),
                contentDescription = "Background",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // Capa oscura ADAPTATIVA
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Black.copy(alpha = 0.6f),
                                MaterialTheme.colorScheme.background.copy(alpha = 0.85f),
                                MaterialTheme.colorScheme.background
                            )
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 32.dp)
                    .padding(vertical = 40.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(20.dp))

                // Logo/Título
                Text(
                    text = "CINEMA ÁGUILAS",
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Crea tu cuenta",
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(40.dp))

                // Campo Nombre
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = {
                        Text(
                            "Nombre completo",
                            color = MaterialTheme.colorScheme.primary
                        )
                    },
                    leadingIcon = {
                        Icon(
                            Icons.Filled.Person,
                            contentDescription = "Nombre",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    isError = name.isNotBlank() && nameError != null,
                    supportingText = {
                        if (name.isNotBlank() && nameError != null) {
                            Text(nameError, color = MaterialTheme.colorScheme.error)
                        }
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                        cursorColor = MaterialTheme.colorScheme.primary,
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                        errorBorderColor = MaterialTheme.colorScheme.error,
                        focusedLabelColor = MaterialTheme.colorScheme.primary,
                        unfocusedLabelColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                        errorLabelColor = MaterialTheme.colorScheme.error,
                        focusedLeadingIconColor = MaterialTheme.colorScheme.primary,
                        unfocusedLeadingIconColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                        errorLeadingIconColor = MaterialTheme.colorScheme.error
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Campo Email
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = {
                        Text(
                            "Email",
                            color = MaterialTheme.colorScheme.primary
                        )
                    },
                    leadingIcon = {
                        Icon(
                            Icons.Filled.Email,
                            contentDescription = "Email",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    isError = email.isNotBlank() && emailError != null,
                    supportingText = {
                        if (email.isNotBlank() && emailError != null) {
                            Text(emailError, color = MaterialTheme.colorScheme.error)
                        }
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                        cursorColor = MaterialTheme.colorScheme.primary,
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                        errorBorderColor = MaterialTheme.colorScheme.error,
                        focusedLabelColor = MaterialTheme.colorScheme.primary,
                        unfocusedLabelColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                        errorLabelColor = MaterialTheme.colorScheme.error,
                        focusedLeadingIconColor = MaterialTheme.colorScheme.primary,
                        unfocusedLeadingIconColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                        errorLeadingIconColor = MaterialTheme.colorScheme.error
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Campo Contraseña
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = {
                        Text(
                            "Contraseña",
                            color = MaterialTheme.colorScheme.primary
                        )
                    },
                    leadingIcon = {
                        Icon(
                            Icons.Filled.Lock,
                            contentDescription = "Contraseña",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    trailingIcon = {
                        IconButton(onClick = { showPassword = !showPassword }) {
                            Icon(
                                imageVector = if (showPassword)
                                    Icons.Filled.Visibility
                                else
                                    Icons.Filled.VisibilityOff,
                                contentDescription = if (showPassword)
                                    "Ocultar contraseña"
                                else
                                    "Mostrar contraseña",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    },
                    isError = password.isNotBlank() && passwordError != null,
                    supportingText = {
                        if (password.isNotBlank() && passwordError != null) {
                            Text(passwordError, color = MaterialTheme.colorScheme.error)
                        } else {
                            Text(
                                "Mínimo 6 caracteres",
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    },
                    singleLine = true,
                    visualTransformation = if (showPassword)
                        VisualTransformation.None
                    else
                        PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                        cursorColor = MaterialTheme.colorScheme.primary,
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                        errorBorderColor = MaterialTheme.colorScheme.error,
                        focusedLabelColor = MaterialTheme.colorScheme.primary,
                        unfocusedLabelColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                        errorLabelColor = MaterialTheme.colorScheme.error,
                        focusedLeadingIconColor = MaterialTheme.colorScheme.primary,
                        unfocusedLeadingIconColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                        errorLeadingIconColor = MaterialTheme.colorScheme.error,
                        focusedTrailingIconColor = MaterialTheme.colorScheme.primary,
                        unfocusedTrailingIconColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                        errorTrailingIconColor = MaterialTheme.colorScheme.error
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Campo Confirmar Contraseña
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = {
                        Text(
                            "Confirmar contraseña",
                            color = MaterialTheme.colorScheme.primary
                        )
                    },
                    leadingIcon = {
                        Icon(
                            Icons.Filled.CheckCircle,
                            contentDescription = "Confirmar",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    trailingIcon = {
                        IconButton(onClick = { showConfirmPassword = !showConfirmPassword }) {
                            Icon(
                                imageVector = if (showConfirmPassword)
                                    Icons.Filled.Visibility
                                else
                                    Icons.Filled.VisibilityOff,
                                contentDescription = if (showConfirmPassword)
                                    "Ocultar contraseña"
                                else
                                    "Mostrar contraseña",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    },
                    isError = confirmPassword.isNotBlank() && confirmPasswordError != null,
                    supportingText = {
                        if (confirmPassword.isNotBlank() && confirmPasswordError != null) {
                            Text(confirmPasswordError, color = MaterialTheme.colorScheme.error)
                        }
                    },
                    singleLine = true,
                    visualTransformation = if (showConfirmPassword)
                        VisualTransformation.None
                    else
                        PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                        cursorColor = MaterialTheme.colorScheme.primary,
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                        errorBorderColor = MaterialTheme.colorScheme.error,
                        focusedLabelColor = MaterialTheme.colorScheme.primary,
                        unfocusedLabelColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                        errorLabelColor = MaterialTheme.colorScheme.error,
                        focusedLeadingIconColor = MaterialTheme.colorScheme.primary,
                        unfocusedLeadingIconColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                        errorLeadingIconColor = MaterialTheme.colorScheme.error,
                        focusedTrailingIconColor = MaterialTheme.colorScheme.primary,
                        unfocusedTrailingIconColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                        errorTrailingIconColor = MaterialTheme.colorScheme.error
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Términos y condiciones
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Al registrarte aceptas nuestros ",
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                        fontSize = 12.sp
                    )
                    TextButton(
                        onClick = { /* TODO: Mostrar términos */ },
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text(
                            "Términos y Condiciones",
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Botón Registrarse
                Button(
                    onClick = {
                        authViewModel.register(RegisterRequest(name, email, password))
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    enabled = isFormValid && authState != AuthState.Loading,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    if (authState == AuthState.Loading) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            "Crear cuenta",
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Link a login
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "¿Ya tienes cuenta? ",
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                    )
                    TextButton(onClick = { navController.popBackStack() }) {
                        Text(
                            "Inicia sesión",
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterScreenPreview() {
    TallerIntegradorTheme {
        RegisterScreen(rememberNavController())
    }
}
package com.example.tallerintegrador

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.tallerintegrador.auth.AuthViewModel
import com.example.tallerintegrador.auth.state.AuthState
import com.example.tallerintegrador.data.model.*
import com.example.tallerintegrador.ui.theme.DarkBlue
import com.example.tallerintegrador.ui.theme.TallerIntegradorTheme
import com.example.tallerintegrador.ui.theme.Yellow
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(navController: NavController, authViewModel: AuthViewModel = viewModel()) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val authState by authViewModel.authState

    // --- LÍNEA CORRECTA ---
    val snackbarHostState = remember { SnackbarHostState() }

    val scope = rememberCoroutineScope()

    LaunchedEffect(authState) {
        when (val state = authState) {
            is AuthState.Success -> {
                // Navega a la pantalla principal y limpia el stack de navegación
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
    )
    { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues) // Padding del scaffold
        ) {
            Image(
                painter = painterResource(id = R.drawable.login_background),
                contentDescription = "Background",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.6f))
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 32.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // El resto de la UI
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email", color = Yellow) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedTextColor = Yellow,
                        focusedTextColor = Yellow,
                        cursorColor = Yellow,
                        focusedBorderColor = Yellow,
                        unfocusedBorderColor = Yellow.copy(alpha = 0.5f),
                        unfocusedLabelColor = Yellow, // Añadido para consistencia
                        focusedLabelColor = Yellow // Añadido para consistencia
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Contraseña", color = Yellow) },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    // Colores personalizados
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedTextColor = Yellow,
                        focusedTextColor = Yellow,cursorColor = Yellow,
                        focusedBorderColor = Yellow,
                        unfocusedBorderColor = Yellow.copy(alpha = 0.5f),
                        unfocusedLabelColor = Yellow, // Añadido para consistencia
                        focusedLabelColor = Yellow // Añadido para consistencia
                    )

                )

                Spacer(modifier = Modifier.height(8.dp))

                TextButton(
                    onClick = { /* TODO: Recuperar contraseña */ },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("¿Olvidaste tu contraseña?", color = Yellow)
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { authViewModel.login(LoginRequest(email, password)) },
                    colors = ButtonDefaults.buttonColors(containerColor = Yellow),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    enabled = authState != AuthState.Loading
                ) {
                    Text("Comenzar ahora", color = DarkBlue, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(16.dp))

                TextButton(onClick = { navController.navigate("register") }) {
                    Text("¿No tienes cuenta? Regístrate", color = Yellow)
                }
            }

            // Indicador de carga
            if (authState == AuthState.Loading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    TallerIntegradorTheme {
        LoginScreen(rememberNavController())
    }
}

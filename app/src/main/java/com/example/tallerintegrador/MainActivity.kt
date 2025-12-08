package com.example.tallerintegrador

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.tallerintegrador.auth.AuthViewModel
import com.example.tallerintegrador.feature.admin.AdminViewModel
import com.example.tallerintegrador.feature.peliculas.PeliculaViewModel
import com.example.tallerintegrador.feature.favoritos.FavoritosViewModel
import com.example.tallerintegrador.ui.theme.TallerIntegradorTheme
import dagger.hilt.android.AndroidEntryPoint
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // El tema ahora es reactivo y se actualiza automáticamente
            TallerIntegradorTheme {
                MainNavigation()
            }
        }
    }
}

@Composable
fun MainNavigation() {
    val navController = rememberNavController()

    NavHost(navController, startDestination = "welcome") {

        composable("welcome") {
            WelcomeScreen(navController)
        }

        composable("login") {
            val authViewModel: AuthViewModel = hiltViewModel()
            LoginScreen(
                navController = navController,
                authViewModel = authViewModel
            )
        }

        composable("register") {
            val authViewModel: AuthViewModel = hiltViewModel()
            RegisterScreen(
                navController = navController,
                authViewModel = authViewModel
            )
        }

        composable("home") {
            val authViewModel: AuthViewModel = hiltViewModel()
            val peliculaViewModel: PeliculaViewModel = hiltViewModel()
            val favoritosViewModel: FavoritosViewModel = hiltViewModel()

            HomeScreen(
                viewModel = peliculaViewModel,
                navController = navController,
                authViewModel = authViewModel,
                favoritosViewModel = favoritosViewModel
            )
        }

        composable(
            route = "detalle_pelicula/{peliculaId}",
            arguments = listOf(
                navArgument("peliculaId") {
                    type = NavType.IntType
                }
            )
        ) { backStackEntry ->
            val peliculaId = backStackEntry.arguments?.getInt("peliculaId") ?: 0
            val peliculaViewModel: PeliculaViewModel = hiltViewModel()
            val favoritosViewModel: FavoritosViewModel = hiltViewModel()

            DetallePeliculaScreen(
                peliculaId = peliculaId,
                viewModel = peliculaViewModel,
                navController = navController,
                favoritosViewModel = favoritosViewModel
            )
        }

        composable(
            route = "peliculas_por_genero/{genero}",
            arguments = listOf(
                navArgument("genero") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val generoEncoded = backStackEntry.arguments?.getString("genero") ?: ""
            val genero = URLDecoder.decode(generoEncoded, StandardCharsets.UTF_8.toString())

            val peliculaViewModel: PeliculaViewModel = hiltViewModel()
            val favoritosViewModel: FavoritosViewModel = hiltViewModel()

            PeliculasPorGeneroScreen(
                genero = genero,
                peliculaViewModel = peliculaViewModel,
                favoritosViewModel = favoritosViewModel,
                navController = navController
            )
        }

        composable("configuracion") {
            val peliculaViewModel: PeliculaViewModel = hiltViewModel()
            val favoritosViewModel: FavoritosViewModel = hiltViewModel()

            ConfiguracionScreen(
                navController = navController,
                peliculaViewModel = peliculaViewModel,
                favoritosViewModel = favoritosViewModel
            )
        }

        composable("editar_perfil") {
            val authViewModel: AuthViewModel = hiltViewModel()
            EditarPerfilScreen(
                navController = navController,
                authViewModel = authViewModel
            )
        }

        composable("notificaciones") {
            NotificacionesScreen(navController = navController)
        }

        composable("privacidad") {
            val authViewModel: AuthViewModel = hiltViewModel()
            PrivacidadScreen(
                navController = navController,
                authViewModel = authViewModel
            )
        }

        composable("acerca_de") {
            AcercaDeScreen(navController = navController)
        }

        composable("admin/dashboard") {
            val authViewModel: AuthViewModel = hiltViewModel()
            val adminViewModel: AdminViewModel = hiltViewModel()

            AdminDashboardScreen(
                navController = navController,
                adminViewModel = adminViewModel
            )
        }

        composable("admin/usuarios") {
            val adminViewModel: AdminViewModel = hiltViewModel()

            AdminUsuariosScreen(
                navController = navController,
                adminViewModel = adminViewModel
            )
        }

        composable("admin/peliculas") {
            val adminViewModel: AdminViewModel = hiltViewModel()

            AdminPeliculasScreen(
                navController = navController,
                adminViewModel = adminViewModel
            )
        }

        composable("admin/logs") {
            val adminViewModel: AdminViewModel = hiltViewModel()

            AdminLogsScreen(
                navController = navController,
                adminViewModel = adminViewModel
            )
        }

        composable("admin/peliculas/nueva") {
            val adminViewModel: AdminViewModel = hiltViewModel()

            LaunchedEffect(Unit) {
                adminViewModel.cargarPeliculas()
            }

            AdminPeliculaFormScreen(
                navController = navController,
                peliculaId = null,
                adminViewModel = adminViewModel
            )
        }

        composable(
            route = "admin/peliculas/editar/{peliculaId}",
            arguments = listOf(
                navArgument("peliculaId") {
                    type = NavType.IntType
                }
            )
        ) { backStackEntry ->
            val peliculaId = backStackEntry.arguments?.getInt("peliculaId") ?: return@composable
            val adminViewModel: AdminViewModel = hiltViewModel()

            LaunchedEffect(Unit) {
                adminViewModel.cargarPeliculas()
            }

            AdminPeliculaFormScreen(
                navController = navController,
                peliculaId = peliculaId,
                adminViewModel = adminViewModel
            )
        }
    }
}
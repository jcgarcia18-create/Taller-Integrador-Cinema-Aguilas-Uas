package com.example.tallerintegrador

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.navigation.NavController
import com.example.tallerintegrador.ui.theme.DarkBlue
import com.example.tallerintegrador.ui.theme.Yellow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AcercaDeScreen(navController: NavController) {
    val context = LocalContext.current
    var showLicensesDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Acerca de", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "Volver", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBlue)
            )
        },
        containerColor = DarkBlue
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            // TARJETA DE INFO
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.05f)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Text(
                            text = "Acerca de la aplicación",
                            color = Yellow,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Cinema Águilas UAS es una plataforma de streaming " +
                                    "desarrollada como proyecto académico para la " +
                                    "Universidad Autónoma de Sinaloa. Ofrece una experiencia " +
                                    "completa de navegación y reproducción de contenido multimedia.",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 14.sp,
                            lineHeight = 20.sp
                        )
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }

            // EQUIPO DE DESARROLLO
            item {
                SectionHeader("Equipo de Desarrollo")
            }

            item {
                TeamMemberCard(
                    name = "Proyecto Académico",
                    role = "Taller Integrador",
                    icon = Icons.Filled.School
                )
            }

            item {
                TeamMemberCard(
                    name = "Universidad Autónoma de Sinaloa",
                    role = "Institución Educativa",
                    icon = Icons.Filled.AccountBalance
                )
            }

            // TECNOLOGÍAS
            item {
                SectionHeader("Tecnologías Utilizadas")
            }

            item {
                TechnologyCard(
                    name = "Kotlin & Jetpack Compose",
                    description = "Framework moderno para UI de Android",
                    icon = Icons.Filled.Code
                )
            }

            item {
                TechnologyCard(
                    name = "Laravel & PHP",
                    description = "Backend API RESTful",
                    icon = Icons.Filled.Storage
                )
            }

            item {
                TechnologyCard(
                    name = "PostgreSQL",
                    description = "Base de datos relacional",
                    icon = Icons.Filled.DataObject
                )
            }

            item {
                TechnologyCard(
                    name = "Room Database",
                    description = "Cache local persistente",
                    icon = Icons.Filled.SaveAlt
                )
            }

            // ENLACES ÚTILES
            item {
                SectionHeader("Enlaces")
            }

            item {
                LinkCard(
                    icon = Icons.Filled.Policy,
                    title = "Política de Privacidad",
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW,
                            "https://uas.edu.mx/privacidad".toUri())
                        context.startActivity(intent)
                    }
                )
            }

            item {
                LinkCard(
                    icon = Icons.Filled.Gavel,
                    title = "Términos y Condiciones",
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW,
                            "https://uas.edu.mx/terminos".toUri())
                        context.startActivity(intent)
                    }
                )
            }

            item {
                LinkCard(
                    icon = Icons.Filled.Copyright,
                    title = "Licencias de Código Abierto",
                    onClick = { showLicensesDialog = true }
                )
            }

            item {
                LinkCard(
                    icon = Icons.Filled.BugReport,
                    title = "Reportar un Problema",
                    onClick = {
                        val intent = Intent(Intent.ACTION_SENDTO).apply {
                            data = "mailto:soporte@cinemaaguilas.com".toUri()
                            putExtra(Intent.EXTRA_SUBJECT, "Reporte de Problema - Cinema Águilas")
                        }
                        context.startActivity(intent)
                    }
                )
            }

            // REDES SOCIALES
            item {
                SectionHeader("Síguenos")
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    SocialButton(
                        icon = Icons.Filled.Facebook,
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW,
                                "https://www.facebook.com/FICuliacan/?locale=es_LA".toUri())
                            context.startActivity(intent)
                        }
                    )
                    SocialButton(
                        icon = Icons.AutoMirrored.Filled.Chat,
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW,
                                "https://x.com/uasoficialmx?lang=es".toUri())
                            context.startActivity(intent)
                        }
                    )
                    SocialButton(
                        icon = Icons.Filled.PhotoCamera,
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW,
                                "https://www.instagram.com/explore/locations/142496062813295/facultad-de-informatica-uas/recent/".toUri())
                            context.startActivity(intent)
                        }
                    )
                    SocialButton(
                        icon = Icons.Filled.PlayArrow,
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW,
                                "https://www.youtube.com/@UASoficial/featured".toUri())
                            context.startActivity(intent)
                        }
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(32.dp)) }

            // COPYRIGHT
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "© 2025 Cinema Águilas UAS",
                        color = Color.White.copy(alpha = 0.5f),
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "Universidad Autónoma de Sinaloa",
                        color = Color.White.copy(alpha = 0.4f),
                        fontSize = 11.sp,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Made with ❤️ in Culiacán, Sinaloa",
                        color = Yellow.copy(alpha = 0.6f),
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(32.dp)) }
        }
    }

    // DIÁLOGO DE LICENCIAS
    if (showLicensesDialog) {
        AlertDialog(
            onDismissRequest = { showLicensesDialog = false },
            title = {
                Text(
                    "Licencias de Código Abierto",
                    color = Yellow,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                LazyColumn(
                    modifier = Modifier.height(300.dp)
                ) {
                    item {
                        LicenseItem(
                            name = "Jetpack Compose",
                            license = "Apache License 2.0",
                            copyright = "© Google LLC"
                        )
                    }
                    item {
                        LicenseItem(
                            name = "Retrofit",
                            license = "Apache License 2.0",
                            copyright = "© Square, Inc."
                        )
                    }
                    item {
                        LicenseItem(
                            name = "Coil",
                            license = "Apache License 2.0",
                            copyright = "© Coil Contributors"
                        )
                    }
                    item {
                        LicenseItem(
                            name = "Room Database",
                            license = "Apache License 2.0",
                            copyright = "© Google LLC"
                        )
                    }
                    item {
                        LicenseItem(
                            name = "Material Icons",
                            license = "Apache License 2.0",
                            copyright = "© Google LLC"
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showLicensesDialog = false }) {
                    Text("Cerrar", color = Yellow)
                }
            },
            containerColor = DarkBlue,
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@Composable
fun TeamMemberCard(
    name: String,
    role: String,
    icon: ImageVector
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
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Yellow.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = name,
                    tint = Yellow,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = name,
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = role,
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 13.sp
                )
            }
        }
    }
}

@Composable
fun TechnologyCard(
    name: String,
    description: String,
    icon: ImageVector
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
            Icon(imageVector = icon, contentDescription = null, tint = Yellow, modifier = Modifier.size(28.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = name,
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = description,
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 13.sp
                )
            }
        }
    }
}

@Composable
fun LinkCard(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.05f)
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = icon, contentDescription = title, tint = Yellow)
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = title, color = Color.White, modifier = Modifier.weight(1f))
            Icon(imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = Color.White.copy(alpha = 0.5f))
        }
    }
}

@Composable
fun SocialButton(
    icon: ImageVector,
    onClick: () -> Unit
) {
    IconButton(onClick = onClick) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.White.copy(alpha = 0.7f),
            modifier = Modifier.size(28.dp)
        )
    }
}

@Composable
fun LicenseItem(
    name: String,
    license: String,
    copyright: String
) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(name, fontWeight = FontWeight.Bold, color = Color.White)
        Text("$license - $copyright", color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
    }
}

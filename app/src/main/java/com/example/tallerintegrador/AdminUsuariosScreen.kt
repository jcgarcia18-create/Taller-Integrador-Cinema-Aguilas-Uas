package com.example.tallerintegrador

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.tallerintegrador.feature.admin.AdminViewModel
import com.example.tallerintegrador.feature.admin.Usuario
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminUsuariosScreen(
    navController: NavController,
    adminViewModel: AdminViewModel = hiltViewModel()
) {
    val usuarios by adminViewModel.usuarios.collectAsState()
    val isLoading by adminViewModel.isLoading.collectAsState()
    val error by adminViewModel.error.collectAsState()

    var showDeleteDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showDetailsDialog by remember { mutableStateOf(false) }
    var selectedUsuario by remember { mutableStateOf<Usuario?>(null) }
    var searchQuery by remember { mutableStateOf("") }

    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    // Usuarios filtrados
    val usuariosFiltrados = remember(usuarios, searchQuery) {
        if (searchQuery.isEmpty()) {
            usuarios
        } else {
            usuarios.filter {
                it.name.contains(searchQuery, ignoreCase = true) ||
                        it.email.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    LaunchedEffect(error) {
        error?.let {
            snackbarHostState.showSnackbar(it)
            adminViewModel.limpiarError()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "Gestionar Usuarios",
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "${usuarios.size} usuarios registrados",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 12.sp
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { adminViewModel.refresh() }) {
                        Icon(
                            imageVector = Icons.Filled.Refresh,
                            contentDescription = "Actualizar",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (isLoading && usuarios.isEmpty()) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else if (usuarios.isEmpty()) {
                EmptyUsersState(modifier = Modifier.align(Alignment.Center))
            } else {
                Column(modifier = Modifier.fillMaxSize()) {
                    // Barra de búsqueda
                    SearchBar(
                        query = searchQuery,
                        onQueryChange = { searchQuery = it },
                        modifier = Modifier.padding(16.dp)
                    )

                    // Resumen de estadísticas
                    StatsRow(
                        totalUsuarios = usuarios.size,
                        usuariosActivos = usuarios.count { it.favoritosCount > 0 },
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Lista de usuarios
                    if (usuariosFiltrados.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    Icons.Filled.SearchOff,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f),
                                    modifier = Modifier.size(64.dp)
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    "No se encontraron usuarios",
                                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                                    fontSize = 16.sp
                                )
                            }
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(usuariosFiltrados) { usuario ->
                                UsuarioCard(
                                    usuario = usuario,
                                    onDetails = {
                                        selectedUsuario = usuario
                                        showDetailsDialog = true
                                    },
                                    onEdit = {
                                        selectedUsuario = usuario
                                        showEditDialog = true
                                    },
                                    onDelete = {
                                        selectedUsuario = usuario
                                        showDeleteDialog = true
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // DIÁLOGO DE DETALLES
    if (showDetailsDialog && selectedUsuario != null) {
        AlertDialog(
            onDismissRequest = { showDetailsDialog = false },
            icon = {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = selectedUsuario?.name?.take(1)?.uppercase() ?: "",
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            title = {
                Text(
                    "Detalles del Usuario",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    DetailRow("Nombre", selectedUsuario?.name ?: "")
                    DetailRow("Email", selectedUsuario?.email ?: "")
                    DetailRow("ID", selectedUsuario?.id.toString() ?: "")
                    DetailRow("Favoritos", "${selectedUsuario?.favoritosCount} películas")
                    DetailRow("Registro", selectedUsuario?.createdAt ?: "N/A")
                }
            },
            confirmButton = {
                TextButton(onClick = { showDetailsDialog = false }) {
                    Text("Cerrar", color = MaterialTheme.colorScheme.primary)
                }
            },
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(16.dp)
        )
    }

    // DIÁLOGO ELIMINAR
    if (showDeleteDialog && selectedUsuario != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            icon = {
                Icon(
                    Icons.Filled.Warning,
                    contentDescription = "Advertencia",
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(48.dp)
                )
            },
            title = {
                Text(
                    "Eliminar Usuario",
                    color = MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    Text(
                        "¿Estás seguro de eliminar a ${selectedUsuario?.name}?",
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        "Esta acción eliminará:",
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        BulletPoint("Cuenta del usuario")
                        BulletPoint("Sus ${selectedUsuario?.favoritosCount} favoritos")
                        BulletPoint("Todo su historial")
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        "⚠️ Esta acción no se puede deshacer",
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        selectedUsuario?.let { usuario ->
                            adminViewModel.eliminarUsuario(usuario.id)
                            scope.launch {
                                snackbarHostState.showSnackbar("Usuario eliminado: ${usuario.name}")
                            }
                        }
                        showDeleteDialog = false
                        selectedUsuario = null
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Eliminar", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancelar", color = MaterialTheme.colorScheme.primary)
                }
            },
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(16.dp)
        )
    }

    // DIÁLOGO EDITAR
    if (showEditDialog && selectedUsuario != null) {
        var nuevoNombre by remember { mutableStateOf(selectedUsuario?.name ?: "") }
        var nuevoEmail by remember { mutableStateOf(selectedUsuario?.email ?: "") }

        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = {
                Text(
                    "Editar Usuario",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = nuevoNombre,
                        onValueChange = { nuevoNombre = it },
                        label = { Text("Nombre", color = MaterialTheme.colorScheme.primary) },
                        leadingIcon = {
                            Icon(Icons.Filled.Person, null, tint = MaterialTheme.colorScheme.primary)
                        },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                            cursorColor = MaterialTheme.colorScheme.primary,
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                        )
                    )

                    OutlinedTextField(
                        value = nuevoEmail,
                        onValueChange = { nuevoEmail = it },
                        label = { Text("Email", color = MaterialTheme.colorScheme.primary) },
                        leadingIcon = {
                            Icon(Icons.Filled.Email, null, tint = MaterialTheme.colorScheme.primary)
                        },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                            cursorColor = MaterialTheme.colorScheme.primary,
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        selectedUsuario?.let { usuario ->
                            adminViewModel.actualizarUsuario(
                                usuario.id,
                                nuevoNombre,
                                nuevoEmail
                            )
                            scope.launch {
                                snackbarHostState.showSnackbar("Usuario actualizado")
                            }
                        }
                        showEditDialog = false
                        selectedUsuario = null
                    },
                    enabled = nuevoNombre.isNotBlank() && nuevoEmail.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(
                        "Guardar",
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) {
                    Text("Cancelar", color = MaterialTheme.colorScheme.onSurface)
                }
            },
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier.fillMaxWidth(),
        placeholder = {
            Text(
                "Buscar usuarios...",
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
            )
        },
        leadingIcon = {
            Icon(
                Icons.Filled.Search,
                contentDescription = "Buscar",
                tint = MaterialTheme.colorScheme.primary
            )
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(
                        Icons.Filled.Close,
                        contentDescription = "Limpiar",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = MaterialTheme.colorScheme.onSurface,
            unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
            cursorColor = MaterialTheme.colorScheme.primary,
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
        ),
        shape = RoundedCornerShape(12.dp),
        singleLine = true
    )
}

@Composable
fun StatsRow(
    totalUsuarios: Int,
    usuariosActivos: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatItem("Total", totalUsuarios.toString(), Icons.Filled.People)
            VerticalDivider(
                modifier = Modifier.height(40.dp),
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.3f)
            )
            StatItem("Activos", usuariosActivos.toString(), Icons.Filled.PersonOutline)
            VerticalDivider(
                modifier = Modifier.height(40.dp),
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.3f)
            )
            StatItem("Inactivos", (totalUsuarios - usuariosActivos).toString(), Icons.Filled.PersonOff)
        }
    }
}

@Composable
fun StatItem(label: String, value: String, icon: ImageVector) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            icon,
            contentDescription = label,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            value,
            color = MaterialTheme.colorScheme.primary,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            label,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            fontSize = 12.sp
        )
    }
}

@Composable
fun UsuarioCard(
    usuario: Usuario,
    onDetails: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Avatar
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = usuario.name.take(1).uppercase(),
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Info
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = usuario.name,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = usuario.email,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Filled.Favorite,
                            contentDescription = null,
                            tint = Color(0xFFE91E63),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            "${usuario.favoritosCount} favoritos",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Acciones
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onDetails,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(Icons.Filled.Info, null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Detalles", fontSize = 12.sp)
                }

                OutlinedButton(
                    onClick = onEdit,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFF2196F3)
                    )
                ) {
                    Icon(Icons.Filled.Edit, null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Editar", fontSize = 12.sp)
                }

                OutlinedButton(
                    onClick = onDelete,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Icon(Icons.Filled.Delete, null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Eliminar", fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(
            label,
            color = MaterialTheme.colorScheme.primary,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            value,
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 14.sp
        )
    }
}

@Composable
fun BulletPoint(text: String) {
    Row(verticalAlignment = Alignment.Top) {
        Text(
            "• ",
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
            fontSize = 14.sp
        )
        Text(
            text,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
            fontSize = 14.sp
        )
    }
}

@Composable
fun EmptyUsersState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            Icons.Filled.PeopleOutline,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f),
            modifier = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "No hay usuarios registrados",
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
            fontSize = 16.sp
        )
    }
}
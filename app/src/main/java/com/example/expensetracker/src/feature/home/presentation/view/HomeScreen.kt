package com.example.expensetracker.src.feature.home.presentation.view
import com.example.expensetracker.src.feature.home.di.DependencyContainer
import com.example.expensetracker.src.feature.home.presentation.viewModel.HomeViewModel

import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Snackbar
import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.expensetracker.src.core.network.NetworkModule
import com.example.expensetracker.src.home.presentation.view.components.ExpenseItem
import com.example.expensetracker.src.home.presentation.view.components.ModernExpenseDialog
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*


@Composable
fun HomeScreen(
) {
    val context = LocalContext.current


    val viewModel: HomeViewModel = viewModel(
        factory = DependencyContainer.getHomeViewModelFactory(context)
    )


    val snackbarHostState = remember { SnackbarHostState() }
    val saveStatusMessage = viewModel.saveStatusMessage

    var showExitDialog by remember { mutableStateOf(false) }
    var showPermissionDialog by remember { mutableStateOf(false) }

    val fabScale by animateFloatAsState(
        targetValue = if (showExitDialog) 0.9f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    )

    LaunchedEffect(Unit) {
        NetworkModule.setContext(context)

        DependencyContainer.getSyncService(context).startObservingSync { count ->
            if (count > 0) {
                snackbarHostState.showSnackbar(" $count gastos sincronizados con el servidor")
            }
        }
    }

    LaunchedEffect(saveStatusMessage) {
        saveStatusMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearSaveStatusMessage()
        }
    }


    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        viewModel.updateImageUri(uri)
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineLocationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        val coarseLocationGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false

        if (fineLocationGranted || coarseLocationGranted) {
            viewModel.getCurrentLocation()
        }
    }

    fun bitmapToUri(bitmap: Bitmap): Uri? {
        return try {
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val filename = "camera_photo_$timeStamp.jpg"
            val file = File(context.cacheDir, filename)

            val outputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
            outputStream.flush()
            outputStream.close()

            Uri.fromFile(file)
        } catch (e: Exception) {
            null
        }
    }

    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        bitmap?.let {
            val uri = bitmapToUri(it)
            viewModel.updateImageUri(uri)
        }
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            takePictureLauncher.launch(null)
        } else {
            showPermissionDialog = true
        }
    }

    fun checkAndRequestCameraPermission() {
        when {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                takePictureLauncher.launch(null)
            }
            else -> {
                cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0D1117),
                        Color(0xFF161B22),
                        Color(0xFF21262D)
                    )
                )
            )

    ) {
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 24.dp)
        ) { data ->
            Snackbar(
                snackbarData = data,
                containerColor = Color(0xFF10B981),
                contentColor = Color.White
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White.copy(alpha = 0.08f)
                ),
                shape = RoundedCornerShape(28.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    Color(0xFF6366F1).copy(alpha = 0.3f),
                                    Color(0xFF8B5CF6).copy(alpha = 0.3f),
                                    Color(0xFFEC4899).copy(alpha = 0.3f)
                                )
                            )
                        )
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            Icons.Filled.AccountBalanceWallet,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                        Text(
                            text = "Executive Tracker",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White,
                            letterSpacing = 1.2.sp
                        )
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                ExtendedFloatingActionButton(
                    onClick = { viewModel.showAddDialog() },
                    modifier = Modifier
                        .scale(fabScale)
                        .height(56.dp),
                    containerColor = Color(0xFF6366F1),
                    contentColor = Color.White,
                    shape = RoundedCornerShape(28.dp),
                    elevation = FloatingActionButtonDefaults.elevation(
                        defaultElevation = 12.dp,
                        pressedElevation = 16.dp
                    )
                ) {
                    Icon(
                        Icons.Filled.Add,
                        contentDescription = "Add Expense",
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Nueva Transacción",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    )
                }


                FloatingActionButton(
                    onClick = { showExitDialog = true },
                    modifier = Modifier
                        .scale(fabScale)
                        .size(56.dp),
                    containerColor = Color(0xFFEF4444),
                    contentColor = Color.White,
                    shape = CircleShape,
                    elevation = FloatingActionButtonDefaults.elevation(
                        defaultElevation = 12.dp,
                        pressedElevation = 16.dp
                    )
                ) {
                    Icon(
                        Icons.Filled.ExitToApp,
                        contentDescription = "Salir",
                        modifier = Modifier.size(24.dp)
                    )
                }
            }


            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White.copy(alpha = 0.06f)
                ),
                shape = RoundedCornerShape(32.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp)
                ) {
                    // Header with modern typography
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 20.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Historial Ejecutivo",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            letterSpacing = 0.5.sp
                        )

                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFF6366F1).copy(alpha = 0.2f)
                            ),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Text(
                                text = "${viewModel.expenses.size} items",
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                color = Color(0xFF6366F1),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    // Content Area
                    when {
                        viewModel.isLoading -> {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    CircularProgressIndicator(
                                        color = Color(0xFF6366F1),
                                        strokeWidth = 3.dp,
                                        modifier = Modifier.size(40.dp)
                                    )
                                    Text(
                                        "Cargando datos...",
                                        color = Color.White.copy(alpha = 0.7f),
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        }

                        viewModel.fetchError != null -> {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = Color(0xFFEF4444).copy(alpha = 0.1f)
                                ),
                                shape = RoundedCornerShape(20.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(20.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Icon(
                                        Icons.Filled.Warning,
                                        contentDescription = null,
                                        tint = Color(0xFFEF4444),
                                        modifier = Modifier.size(32.dp)
                                    )
                                    Text(
                                        text = "Error: ${viewModel.fetchError}",
                                        color = Color(0xFFEF4444),
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 14.sp
                                    )
                                    OutlinedButton(
                                        onClick = { viewModel.refreshExpenses() },
                                        colors = ButtonDefaults.outlinedButtonColors(
                                            contentColor = Color(0xFFEF4444)
                                        ),
                                        shape = RoundedCornerShape(16.dp)
                                    ) {
                                        Text("Reintentar")
                                    }
                                }
                            }
                        }

                        viewModel.expenses.isEmpty() -> {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    Icon(
                                        Icons.Filled.Receipt,
                                        contentDescription = null,
                                        tint = Color.White.copy(alpha = 0.3f),
                                        modifier = Modifier.size(64.dp)
                                    )
                                    Text(
                                        "No hay transacciones",
                                        color = Color.White.copy(alpha = 0.7f),
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Text(
                                        "Agrega tu primera transacción",
                                        color = Color.White.copy(alpha = 0.5f),
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        }

                        else -> {
                            LazyColumn(
                                verticalArrangement = Arrangement.spacedBy(12.dp),
                                contentPadding = PaddingValues(vertical = 8.dp)
                            ) {
                                items(viewModel.expenses) { expense ->
                                    ExpenseItem(
                                        expense = expense,
                                        onEditClick = { viewModel.showEditDialog(expense) },
                                        onDeleteClick = { viewModel.showDeleteDialog(expense) }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Bottom signature
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Executive Finance Suite™",
                fontSize = 12.sp,
                fontWeight = FontWeight.Light,
                color = Color.White.copy(alpha = 0.4f),
                letterSpacing = 1.sp
            )
        }
    }

    // Modern Dialogs
    if (viewModel.showAddDialog) {
        ModernExpenseDialog(
            title = "Agregar gasto",
            category = viewModel.category,
            description = viewModel.description,
            amount = viewModel.amount,
            date = viewModel.date,
            imageUri = viewModel.imageUri,
            currentLocation = viewModel.currentLocation,
            useCurrentLocation = viewModel.useCurrentLocation,
            isLoadingLocation = viewModel.isLoadingLocation,
            locationError = viewModel.locationError,
            errorMessage = viewModel.errorMessage,
            isLoading = viewModel.isLoading,
            onCategoryChange = { viewModel.updateCategory(it) },
            onDescriptionChange = { viewModel.updateDescription(it) },
            onAmountChange = { viewModel.updateAmount(it) },
            onDateChange = { viewModel.updateDate(it) },
            onPickFromGallery = { pickImageLauncher.launch("image/*") },
            onTakePhoto = { checkAndRequestCameraPermission() },
            onRemoveImage = { viewModel.updateImageUri(null) },
            onToggleLocation = { viewModel.toggleLocationUsage() },
            onRetryLocation = { viewModel.retryLocation() },
            onConfirm = { viewModel.onAddExpenseClick(context) },
            onDismiss = { viewModel.hideAddDialog() }
        )
    }

    if (viewModel.showEditDialog) {
        ModernExpenseDialog(
            title = "Editar gasto",
            category = viewModel.category,
            description = viewModel.description,
            amount = viewModel.amount,
            date = viewModel.date,
            imageUri = viewModel.imageUri,
            existingImageUrl = viewModel.getCurrentImageUrl(),
            currentLocation = viewModel.currentLocation,
            useCurrentLocation = viewModel.useCurrentLocation,
            isLoadingLocation = viewModel.isLoadingLocation,
            locationError = viewModel.locationError,
            errorMessage = viewModel.errorMessage,
            isLoading = viewModel.isLoading,
            onCategoryChange = { viewModel.updateCategory(it) },
            onDescriptionChange = { viewModel.updateDescription(it) },
            onAmountChange = { viewModel.updateAmount(it) },
            onDateChange = { viewModel.updateDate(it) },
            onPickFromGallery = { pickImageLauncher.launch("image/*") },
            onTakePhoto = { checkAndRequestCameraPermission() },
            onRemoveImage = { viewModel.removeCurrentImage() },
            onToggleLocation = { viewModel.toggleLocationUsage() },
            onRetryLocation = { viewModel.retryLocation() },
            onConfirm = { viewModel.onUpdateExpenseClick() },
            onDismiss = { viewModel.hideEditDialog() }
        )
    }

    // Executive style dialogs
    if (viewModel.showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.hideDeleteDialog() },
            containerColor = Color(0xFF1F2937),
            shape = RoundedCornerShape(24.dp),
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        Icons.Filled.Delete,
                        contentDescription = null,
                        tint = Color(0xFFEF4444),
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        "Eliminar Transacción",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            text = {
                Text(
                    "¿Confirma la eliminación de esta transacción? Esta acción no se puede deshacer.",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 14.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.onDeleteExpenseClick() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFEF4444)
                    ),
                    shape = RoundedCornerShape(16.dp),
                    enabled = !viewModel.isLoading
                ) {
                    if (viewModel.isLoading) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Confirmar", fontWeight = FontWeight.SemiBold)
                    }
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { viewModel.hideDeleteDialog() },
                    enabled = !viewModel.isLoading,
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color.White.copy(alpha = 0.8f)
                    )
                ) {
                    Text("Cancelar")
                }
            }
        )
    }

    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = { showExitDialog = false },
            containerColor = Color(0xFF1F2937),
            shape = RoundedCornerShape(24.dp),
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        Icons.Filled.ExitToApp,
                        contentDescription = null,
                        tint = Color(0xFFEF4444),
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        "Salir de la Aplicación",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            text = {
                Text(
                    "¿Está seguro que desea cerrar Executive Tracker?",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 14.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showExitDialog = false
                        (context as? Activity)?.finish()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFEF4444)
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("Salir", fontWeight = FontWeight.SemiBold)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showExitDialog = false },
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color.White.copy(alpha = 0.8f)
                    )
                ) {
                    Text("Cancelar")
                }
            }
        )
    }

    if (showPermissionDialog) {
        AlertDialog(
            onDismissRequest = { showPermissionDialog = false },
            containerColor = Color(0xFF1F2937),
            shape = RoundedCornerShape(24.dp),
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        Icons.Filled.CameraAlt,
                        contentDescription = null,
                        tint = Color(0xFFF59E0B),
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        "Permiso de Cámara",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            text = {
                Text(
                    "Se requiere acceso a la cámara para capturar imágenes de sus transacciones. Habilite el permiso en configuración.",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 14.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = { showPermissionDialog = false },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF6366F1)
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("Entendido", fontWeight = FontWeight.SemiBold)
                }
            }
        )
    }

    if (viewModel.showLocationPermissionDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.hideLocationPermissionDialog() },
            containerColor = Color(0xFF1F2937),
            shape = RoundedCornerShape(24.dp),
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        Icons.Filled.LocationOn,
                        contentDescription = null,
                        tint = Color(0xFF10B981),
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        "Permiso de Ubicación",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            text = {
                Text(
                    "Active la ubicación para agregar contexto geográfico a sus transacciones ejecutivas.",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 14.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.hideLocationPermissionDialog()
                        locationPermissionLauncher.launch(
                            arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            )
                        )
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF10B981)
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("Conceder Permiso", fontWeight = FontWeight.SemiBold)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { viewModel.hideLocationPermissionDialog() },
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color.White.copy(alpha = 0.8f)
                    )
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}
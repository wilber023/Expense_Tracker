package com.example.expensetracker.src.home.presentation.view
import com.example.expensetracker.src.home.presentation.view.components.ExpenseItem
import com.example.expensetracker.src.home.presentation.view.components.ModernExpenseDialog
import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.expensetracker.src.core.network.NetworkModule
import com.example.expensetracker.src.home.di.DependencyContainer
import com.example.expensetracker.src.home.presentation.viewModel.HomeViewModel
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HomeScreen(
    onLogout: () -> Unit = {}
) {
    val context = LocalContext.current
    var showExitDialog by remember { mutableStateOf(false) }
    var showPermissionDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        NetworkModule.setContext(context)
    }

    val viewModel: HomeViewModel = viewModel(
        factory = try {
            DependencyContainer.getHomeViewModelFactory(context)
        } catch (e: Exception) {
            DependencyContainer.homeViewModelFactory
        }
    )


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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(30.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "Expense Tracker Student",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(bottom = 32.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            FloatingActionButton(
                onClick = { viewModel.showAddDialog() },
                modifier = Modifier
                    .padding(top = 50.dp)
                    .size(60.dp)
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add Expense")
            }

            FloatingActionButton(
                onClick = { showExitDialog = true },
                modifier = Modifier
                    .padding(top = 50.dp)
                    .size(60.dp),
                containerColor = MaterialTheme.colorScheme.error
            ) {
                Icon(
                    Icons.Filled.ExitToApp,
                    contentDescription = "Salir de la aplicación",
                    tint = MaterialTheme.colorScheme.onError
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(5.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(15.dp)
            ) {
                Text(
                    text = "Historial de gastos",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                when {
                    viewModel.isLoading -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }

                    viewModel.fetchError != null -> {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Error: ${viewModel.fetchError}",
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.padding(8.dp)
                            )
                            Button(
                                onClick = { viewModel.refreshExpenses() },
                                modifier = Modifier.padding(top = 8.dp)
                            ) {
                                Text("Reintentar")
                            }
                        }
                    }

                    viewModel.expenses.isEmpty() -> {
                        Text(
                            "No hay gastos registrados",
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }

                    else -> {
                        Column {
                            viewModel.expenses.forEach { expense ->
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

        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = "Expense Tracker :)",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(top = 16.dp)
        )
    }


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
            onConfirm = { viewModel.onAddExpenseClick() },
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

    if (viewModel.showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.hideDeleteDialog() },
            title = { Text("Eliminar gasto") },
            text = { Text("¿Estás seguro de que quieres eliminar este gasto?") },
            confirmButton = {
                Button(
                    onClick = { viewModel.onDeleteExpenseClick() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    ),
                    enabled = !viewModel.isLoading
                ) {
                    if (viewModel.isLoading) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.onError,
                            modifier = Modifier.size(16.dp)
                        )
                    } else {
                        Text("Eliminar")
                    }
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { viewModel.hideDeleteDialog() },
                    enabled = !viewModel.isLoading
                ) {
                    Text("Cancelar")
                }
            }
        )
    }

    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = { showExitDialog = false },
            title = { Text("Salir de la aplicación") },
            text = { Text("¿Estás seguro de que quieres salir de la aplicación?") },
            confirmButton = {
                Button(
                    onClick = {
                        showExitDialog = false
                        (context as? Activity)?.finish()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Salir")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showExitDialog = false }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }


    if (showPermissionDialog) {
        AlertDialog(
            onDismissRequest = { showPermissionDialog = false },
            title = { Text("Permiso de cámara requerido") },
            text = {
                Text("Esta aplicación necesita acceso a la cámara para tomar fotos. Por favor, habilita el permiso en la configuración de la aplicación.")
            },
            confirmButton = {
                Button(
                    onClick = { showPermissionDialog = false }
                ) {
                    Text("Entendido")
                }
            }
        )
    }


    if (viewModel.showLocationPermissionDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.hideLocationPermissionDialog() },
            title = { Text("Permiso de ubicación requerido") },
            text = {
                Text("Esta aplicación necesita acceso a la ubicación para agregar la ubicación de tus gastos. Por favor, habilita el permiso de ubicación.")
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
                    }
                ) {
                    Text("Conceder permiso")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { viewModel.hideLocationPermissionDialog() }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}
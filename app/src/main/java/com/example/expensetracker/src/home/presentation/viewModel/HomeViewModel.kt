package com.example.expensetracker.src.home.presentation.viewModel

import android.net.Uri
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expensetracker.src.core.hardware.domain.LocationData
import com.example.expensetracker.src.home.domain.UseCase.AddExpenseUseCase
import com.example.expensetracker.src.home.domain.UseCase.GetExpenseUseCase
import com.example.expensetracker.src.home.domain.UseCase.UpdateExpenseUseCase
import com.example.expensetracker.src.home.domain.UseCase.DeleteExpenseUseCase
import com.example.expensetracker.src.home.domain.UseCase.GetLocationUseCase
import com.example.expensetracker.src.home.domain.repository.Expense
import kotlinx.coroutines.launch
import android.util.Log
class HomeViewModel(
    private val addExpenseUseCase: AddExpenseUseCase,
    private val getExpenseUseCase: GetExpenseUseCase,
    private val updateExpenseUseCase: UpdateExpenseUseCase,
    private val deleteExpenseUseCase: DeleteExpenseUseCase,
    private val getLocationUseCase: GetLocationUseCase
) : ViewModel() {

    var expenses by mutableStateOf<List<Expense>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set

    var fetchError by mutableStateOf<String?>(null)
        private set

    var category by mutableStateOf("")
        private set

    var description by mutableStateOf("")
        private set

    var amount by mutableStateOf("")
        private set

    var date by mutableStateOf("")
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    var showAddDialog by mutableStateOf(false)
        private set

    var showEditDialog by mutableStateOf(false)
        private set

    var showDeleteDialog by mutableStateOf(false)
        private set

    private var currentExpense: Expense? = null

    var imageUri by mutableStateOf<Uri?>(null)
        private set

    // Variables para rastrear si se debe mantener la imagen existente durante la edición
    var keepExistingImage by mutableStateOf(false)
        private set

    // Variables para GPS
    var currentLocation by mutableStateOf<LocationData?>(null)
        private set

    var isLoadingLocation by mutableStateOf(false)
        private set

    var locationError by mutableStateOf<String?>(null)
        private set

    var showLocationPermissionDialog by mutableStateOf(false)
        private set

    var useCurrentLocation by mutableStateOf(false)
        private set

    init {
        loadExpenses()
    }

    fun updateImageUri(newUri: Uri?) {
        imageUri = newUri
        keepExistingImage = false // Al seleccionar nueva imagen, no mantener la existente
        errorMessage = null
    }

    fun removeCurrentImage() {
        imageUri = null
        keepExistingImage = false
        errorMessage = null
    }

    fun updateCategory(newCategory: String) {
        category = newCategory
        errorMessage = null
    }

    fun updateDescription(newDescription: String) {
        description = newDescription
        errorMessage = null
    }

    fun updateAmount(newAmount: String) {
        amount = newAmount
        errorMessage = null
    }

    fun updateDate(newDate: String) {
        date = newDate
        errorMessage = null
    }

    fun showAddDialog() {
        clearFields()
        showAddDialog = true
    }

    fun hideAddDialog() {
        showAddDialog = false
        clearFields()
    }

    fun showEditDialog(expense: Expense) {
        currentExpense = expense
        category = expense.category
        description = expense.description
        amount = expense.amount.toString()
        date = expense.date
        imageUri = null // No mostrar imagen existente como URI local
        keepExistingImage = expense.imageUrl != null // Mantener imagen existente si la hay

        // Cargar ubicación existente si la hay
        if (expense.latitude != null && expense.longitude != null) {
            currentLocation = LocationData(
                latitude = expense.latitude,
                longitude = expense.longitude,
                address = expense.address
            )
            useCurrentLocation = true
        } else {
            currentLocation = null
            useCurrentLocation = false
        }

        showEditDialog = true
    }

    fun hideEditDialog() {
        showEditDialog = false
        currentExpense = null
        clearFields()
    }

    fun showDeleteDialog(expense: Expense) {
        currentExpense = expense
        showDeleteDialog = true
    }

    fun hideDeleteDialog() {
        showDeleteDialog = false
        currentExpense = null
    }

    fun clearError() {
        errorMessage = null
        fetchError = null
        locationError = null
    }

    // Función para obtener la URL de imagen a mostrar en la UI
    fun getCurrentImageUrl(): String? {
        return currentExpense?.imageUrl
    }

    // Funciones para GPS
    fun toggleLocationUsage() {
        useCurrentLocation = !useCurrentLocation
        if (useCurrentLocation) {
            getCurrentLocation()
        } else {
            currentLocation = null
            locationError = null
        }
    }

    fun getCurrentLocation() {
        Log.d("ViewModel", "Iniciando captura GPS...")
        if (!getLocationUseCase.hasPermission()) {
            showLocationPermissionDialog = true
            useCurrentLocation = false
            return
        }

        if (!getLocationUseCase.isGPSEnabled()) {
            locationError = "GPS deshabilitado. Habilítalo en configuración."
            useCurrentLocation = false
            return
        }

        viewModelScope.launch {
            try {
                isLoadingLocation = true
                locationError = null

                val result = getLocationUseCase()
                if (result.isSuccess) {
                    currentLocation = result.getOrNull()
                    locationError = null
                } else {
                    locationError = result.exceptionOrNull()?.message ?: "Error desconocido al obtener ubicación"
                    useCurrentLocation = false
                }
            } catch (e: Exception) {
                locationError = "Error al obtener ubicación: ${e.message}"
                useCurrentLocation = false
            } finally {
                isLoadingLocation = false
            }
        }
    }

    fun hideLocationPermissionDialog() {
        showLocationPermissionDialog = false
        useCurrentLocation = false
    }

    fun retryLocation() {
        getCurrentLocation()
    }

    fun onAddExpenseClick() {
        if (category.isBlank() || description.isBlank() || amount.isBlank() || date.isBlank()) {
            errorMessage = "Todos los campos son obligatorios"
            return
        }

        val amountValue = amount.toDoubleOrNull()
        if (amountValue == null || amountValue <= 0) {
            errorMessage = "El monto debe ser un número válido mayor a 0"
            return
        }

        viewModelScope.launch {
            try {
                isLoading = true
                errorMessage = null

                Log.d("ViewModel", "Enviando ubicación: $currentLocation")

                addExpenseUseCase(
                    category = category,
                    description = description,
                    amount = amountValue,
                    date = date,
                    imageUri = imageUri,
                    location = currentLocation
                )

                clearFields()
                showAddDialog = false
                loadExpenses()

            } catch (e: Exception) {
                errorMessage = "Error al agregar gasto: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    fun onUpdateExpenseClick() {
        if (category.isBlank() || description.isBlank() || amount.isBlank() || date.isBlank()) {
            errorMessage = "Todos los campos son obligatorios"
            return
        }

        val amountValue = amount.toDoubleOrNull()
        if (amountValue == null || amountValue <= 0) {
            errorMessage = "El monto debe ser un número válido mayor a 0"
            return
        }

        val expense = currentExpense ?: return

        viewModelScope.launch {
            try {
                isLoading = true
                errorMessage = null

                // Solo enviar imageUri si se seleccionó una nueva imagen
                val imageToSend = if (!keepExistingImage) imageUri else null

                updateExpenseUseCase(
                    id = expense.id ?: "",
                    category = category,
                    description = description,
                    amount = amountValue,
                    date = date,
                    imageUri = imageToSend
                    // TODO: Agregar ubicación cuando se actualice el use case
                )

                clearFields()
                showEditDialog = false
                currentExpense = null
                loadExpenses()

            } catch (e: Exception) {
                errorMessage = "Error al actualizar gasto: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    fun onDeleteExpenseClick() {
        val expense = currentExpense ?: return

        viewModelScope.launch {
            try {
                isLoading = true
                errorMessage = null

                deleteExpenseUseCase(expense.id ?: "")

                showDeleteDialog = false
                currentExpense = null
                loadExpenses()

            } catch (e: Exception) {
                errorMessage = "Error al eliminar gasto: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    private fun clearFields() {
        category = ""
        description = ""
        amount = ""
        date = ""
        errorMessage = null
        imageUri = null
        keepExistingImage = false
        currentLocation = null
        useCurrentLocation = false
        locationError = null
    }

    fun loadExpenses() {
        viewModelScope.launch {
            isLoading = true
            fetchError = null
            try {
                val result = getExpenseUseCase()
                expenses = result
            } catch (e: Exception) {
                fetchError = "Error al cargar los gastos: ${e.message}"
                expenses = emptyList()
            } finally {
                isLoading = false
            }
        }
    }

    fun refreshExpenses() {
        loadExpenses()
    }
}
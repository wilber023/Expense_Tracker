package com.example.expensetracker.src.home.presentation.viewModel

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expensetracker.src.home.domain.UseCase.AddExpenseUseCase
import com.example.expensetracker.src.home.domain.UseCase.GetExpenseUseCase
import com.example.expensetracker.src.home.domain.UseCase.UpdateExpenseUseCase
import com.example.expensetracker.src.home.domain.UseCase.DeleteExpenseUseCase
import com.example.expensetracker.src.home.domain.repository.Expense
import kotlinx.coroutines.launch

class HomeViewModel(
    private val addExpenseUseCase: AddExpenseUseCase,
    private val getExpenseUseCase: GetExpenseUseCase,
    private val updateExpenseUseCase: UpdateExpenseUseCase,
    private val deleteExpenseUseCase: DeleteExpenseUseCase
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

    // Gasto que se está editando o eliminando
    private var currentExpense: Expense? = null

    init {
        loadExpenses()
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

                addExpenseUseCase(
                    category = category,
                    description = description,
                    amount = amountValue,
                    date = date
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

                updateExpenseUseCase(
                    id = expense.id ?: "",
                    category = category,
                    description = description,
                    amount = amountValue,
                    date = date
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
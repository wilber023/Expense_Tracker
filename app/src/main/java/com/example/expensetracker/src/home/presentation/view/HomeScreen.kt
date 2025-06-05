package com.example.expensetracker.src.home.presentation.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.app.Activity
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.expensetracker.src.home.di.DependencyContainer
import com.example.expensetracker.src.home.domain.repository.Expense
import com.example.expensetracker.src.home.presentation.viewModel.HomeViewModel

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = viewModel(factory = DependencyContainer.homeViewModelFactory)
) {
    var showExitDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

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
                    .padding(top=50.dp)
                    .size(60.dp)
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add Expense")
            }

            FloatingActionButton(
                onClick = { showExitDialog = true },
                modifier = Modifier
                    .padding(top=50.dp)
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
                if (viewModel.isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                else if (viewModel.fetchError != null) {
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
                else if (viewModel.expenses.isEmpty()) {
                    Text(
                        "No hay gastos registrados",
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
                else {
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

        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = "Expense Tracker :)",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(top = 16.dp)
        )
    }
    if (viewModel.showAddDialog) {
        ExpenseDialog(
            title = "Agregar gasto",
            category = viewModel.category,
            description = viewModel.description,
            amount = viewModel.amount,
            date = viewModel.date,
            errorMessage = viewModel.errorMessage,
            isLoading = viewModel.isLoading,
            onCategoryChange = { viewModel.updateCategory(it) },
            onDescriptionChange = { viewModel.updateDescription(it) },
            onAmountChange = { viewModel.updateAmount(it) },
            onDateChange = { viewModel.updateDate(it) },
            onConfirm = { viewModel.onAddExpenseClick() },
            onDismiss = { viewModel.hideAddDialog() }
        )
    }
    if (viewModel.showEditDialog) {
        ExpenseDialog(
            title = "Editar gasto",
            category = viewModel.category,
            description = viewModel.description,
            amount = viewModel.amount,
            date = viewModel.date,
            errorMessage = viewModel.errorMessage,
            isLoading = viewModel.isLoading,
            onCategoryChange = { viewModel.updateCategory(it) },
            onDescriptionChange = { viewModel.updateDescription(it) },
            onAmountChange = { viewModel.updateAmount(it) },
            onDateChange = { viewModel.updateDate(it) },
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
}

@Composable
fun ExpenseItem(
    expense: Expense,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Categoría: ${expense.category}", fontWeight = FontWeight.Bold)
                    Text("Descripción: ${expense.description}")
                    Text("Monto: $${expense.amount}")
                    Text("Fecha: ${expense.date}")
                }

                Row {
                    IconButton(onClick = onEditClick) {
                        Icon(
                            Icons.Filled.Edit,
                            contentDescription = "Editar gasto",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(onClick = onDeleteClick) {
                        Icon(
                            Icons.Filled.Delete,
                            contentDescription = "Eliminar gasto",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ExpenseDialog(
    title: String,
    category: String,
    description: String,
    amount: String,
    date: String,
    errorMessage: String?,
    isLoading: Boolean,
    onCategoryChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onAmountChange: (String) -> Unit,
    onDateChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column {
                errorMessage?.let { error ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Text(
                            text = error,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.padding(8.dp),
                            fontSize = 14.sp
                        )
                    }
                }

                OutlinedTextField(
                    value = category,
                    onValueChange = onCategoryChange,
                    label = { Text("Categoría") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { Icon(Icons.Filled.ShoppingCart, contentDescription = null) },
                    enabled = !isLoading
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = description,
                    onValueChange = onDescriptionChange,
                    label = { Text("Descripción") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { Icon(Icons.Filled.Edit, contentDescription = null) },
                    enabled = !isLoading
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = amount,
                    onValueChange = onAmountChange,
                    label = { Text("Monto") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { Icon(Icons.Filled.Info, contentDescription = null) },
                    enabled = !isLoading
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = date,
                    onValueChange = onDateChange,
                    label = { Text("Fecha") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { Icon(Icons.Filled.DateRange, contentDescription = null) },
                    enabled = !isLoading
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                } else {
                    Text(if (title.contains("Agregar")) "Agregar gasto" else "Actualizar gasto")
                }
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                enabled = !isLoading
            ) {
                Text("Cancelar")
            }
        }
    )
}
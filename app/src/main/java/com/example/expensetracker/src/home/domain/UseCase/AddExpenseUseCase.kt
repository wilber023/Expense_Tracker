package com.example.expensetracker.src.home.domain.UseCase

 import com.example.expensetracker.src.home.data.repository.ExpenseRepositoryImpl
 import com.example.expensetracker.src.home.domain.repository.Expense

class AddExpenseUseCase(
    private val repository: ExpenseRepositoryImpl
) {
    suspend operator fun invoke(
        category: String,
        description: String,
        amount: Double,
        date: String
    ) {
        val expense = Expense(
            category = category,
            description = description,
            amount = amount,
            date = date
        )
        repository.addExpense(expense)
    }
}
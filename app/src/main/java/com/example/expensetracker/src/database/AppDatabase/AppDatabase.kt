package com.example.expensetracker.src.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.expensetracker.src.database.converters.ExpenseConverters
import com.example.expensetracker.src.database.dao.ExpenseDao
import com.example.expensetracker.src.database.entity.ExpenseEntity

@Database(
    entities = [ExpenseEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(ExpenseConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun expenseDao(): ExpenseDao

    companion object {
        const val DATABASE_NAME = "expenses_database"
    }
}
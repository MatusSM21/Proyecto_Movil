package com.example.finansmart

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "finansmart.db"
        const val DATABASE_VERSION = 3

        // Tabla de transacciones
        const val TABLE_TRANSACTIONS = "transacciones"
        const val COLUMN_ID = "id"
        const val COLUMN_TYPE = "tipo"
        const val COLUMN_AMOUNT = "monto"
        const val COLUMN_CATEGORY = "categoria"
        const val COLUMN_DATE = "fecha"

        // Tabla de usuarios
        const val TABLE_USERS = "users"
        const val COLUMN_USER_ID = "id"
        const val COLUMN_USERNAME = "username"
        const val COLUMN_EMAIL = "email"
        const val COLUMN_PASSWORD = "password"
    }

    override fun onCreate(db: SQLiteDatabase) {
        // Crear tabla de transacciones
        val createTransactionsTable = """
            CREATE TABLE $TABLE_TRANSACTIONS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_TYPE TEXT,
                $COLUMN_AMOUNT REAL,
                $COLUMN_CATEGORY TEXT,
                $COLUMN_DATE TEXT
            )
        """
        db.execSQL(createTransactionsTable)

        // Crear tabla de usuarios
        val createUsersTable = """
            CREATE TABLE $TABLE_USERS (
                $COLUMN_USER_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_USERNAME TEXT UNIQUE,
                $COLUMN_EMAIL TEXT UNIQUE,
                $COLUMN_PASSWORD TEXT
            )
        """
        db.execSQL(createUsersTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_TRANSACTIONS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        onCreate(db)
    }

    // Métodos para la tabla de transacciones
    fun insertTransaction(type: String, amount: Double, category: String, date: String): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_TYPE, type)
            put(COLUMN_AMOUNT, amount)
            put(COLUMN_CATEGORY, category)
            put(COLUMN_DATE, date)
        }
        return db.insert(TABLE_TRANSACTIONS, null, values)
    }

    fun getAllTransactions(): List<Map<String, Any>> {
        val transactions = mutableListOf<Map<String, Any>>()
        val db = readableDatabase
        val cursor = db.query(TABLE_TRANSACTIONS, null, null, null, null, null, "$COLUMN_DATE DESC")
        while (cursor.moveToNext()) {
            val transaction = mapOf(
                COLUMN_ID to cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                COLUMN_TYPE to cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TYPE)),
                COLUMN_AMOUNT to cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_AMOUNT)),
                COLUMN_CATEGORY to cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY)),
                COLUMN_DATE to cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE))
            )
            transactions.add(transaction)
        }
        cursor.close()
        return transactions
    }

    // Limpiar todas las transacciones
    fun clearAllTransactions() {
        val db = writableDatabase
        db.delete(TABLE_TRANSACTIONS, null, null)
        db.close()
    }

    // Métodos para la tabla de usuarios
    fun registerUser(username: String, email: String, password: String): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_USERNAME, username)
            put(COLUMN_EMAIL, email)
            put(COLUMN_PASSWORD, password)
        }
        return try {
            db.insert(TABLE_USERS, null, values) > 0
        } catch (e: Exception) {
            false
        }
    }

    fun authenticateUser(username: String, password: String): Boolean {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_USERS,
            null,
            "$COLUMN_USERNAME = ? AND $COLUMN_PASSWORD = ?",
            arrayOf(username, password),
            null, null, null
        )
        val isAuthenticated = cursor.count > 0
        cursor.close()
        return isAuthenticated
    }
}

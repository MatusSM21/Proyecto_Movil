package com.example.finansmart

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var databaseHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        databaseHelper = DatabaseHelper(this)

        // Inicializar vistas
        val textViewBalance = findViewById<TextView>(R.id.textViewBalance)
        val editTextAmount = findViewById<EditText>(R.id.editTextAmount)
        val editTextCategory = findViewById<EditText>(R.id.editTextCategory)
        val buttonIncome = findViewById<Button>(R.id.buttonIncome)
        val buttonExpense = findViewById<Button>(R.id.buttonExpense)
        val buttonClearTransactions = findViewById<Button>(R.id.buttonClearTransactions)
        val buttonViewResults = findViewById<Button>(R.id.buttonViewResults)
        val textViewTransactionList = findViewById<TextView>(R.id.textViewTransactionList)
        val textViewIncomeAmount = findViewById<TextView>(R.id.textViewIncomeAmount)
        val textViewExpenseAmount = findViewById<TextView>(R.id.textViewExpenseAmount)
        val textViewAlert = findViewById<TextView>(R.id.textViewAlert)

        // Botón para registrar ingreso
        buttonIncome.setOnClickListener {
            val amount = editTextAmount.text.toString().toDoubleOrNull()
            val category = editTextCategory.text.toString()
            if (amount != null && category.isNotEmpty()) {
                databaseHelper.insertTransaction("ingreso", amount, category, System.currentTimeMillis().toString())
                updateUI(textViewBalance, textViewTransactionList, textViewIncomeAmount, textViewExpenseAmount, textViewAlert)
                editTextAmount.text.clear()
                editTextCategory.text.clear()
            } else {
                Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
            }
        }

        // Botón para registrar gasto
        buttonExpense.setOnClickListener {
            val amount = editTextAmount.text.toString().toDoubleOrNull()
            val category = editTextCategory.text.toString()
            if (amount != null && category.isNotEmpty()) {
                databaseHelper.insertTransaction("gasto", amount, category, System.currentTimeMillis().toString())
                updateUI(textViewBalance, textViewTransactionList, textViewIncomeAmount, textViewExpenseAmount, textViewAlert)
                editTextAmount.text.clear()
                editTextCategory.text.clear()
            } else {
                Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
            }
        }

        // Botón para limpiar transacciones y balance
        buttonClearTransactions.setOnClickListener {
            databaseHelper.clearAllTransactions()
            updateUI(textViewBalance, textViewTransactionList, textViewIncomeAmount, textViewExpenseAmount, textViewAlert)
            Toast.makeText(this, "Transacciones eliminadas con éxito", Toast.LENGTH_SHORT).show()
        }

        // Botón para ver resultados
        buttonViewResults.setOnClickListener {
            startActivity(Intent(this, ResultsActivity::class.java))
        }

        // Actualizar la interfaz de usuario
        updateUI(textViewBalance, textViewTransactionList, textViewIncomeAmount, textViewExpenseAmount, textViewAlert)
    }

    private fun updateUI(
        textViewBalance: TextView,
        textViewTransactionList: TextView,
        textViewIncomeAmount: TextView,
        textViewExpenseAmount: TextView,
        textViewAlert: TextView
    ) {
        val transactions = databaseHelper.getAllTransactions()

        // Calcular ingresos, gastos y balance general
        val totalIncome = transactions.filter { it[DatabaseHelper.COLUMN_TYPE] == "ingreso" }
            .sumOf { it[DatabaseHelper.COLUMN_AMOUNT] as Double }
        val totalExpense = transactions.filter { it[DatabaseHelper.COLUMN_TYPE] == "gasto" }
            .sumOf { it[DatabaseHelper.COLUMN_AMOUNT] as Double }
        val balance = totalIncome - totalExpense

        // Actualizar balance general
        textViewBalance.text = "Balance General: $balance"

        // Actualizar tarjetas de ingresos y gastos
        textViewIncomeAmount.text = "$$totalIncome"
        textViewExpenseAmount.text = "$$totalExpense"

        // Mostrar alerta si los gastos superan un límite
        if (totalExpense > 500000) { // Puedes ajustar este límite
            textViewAlert.text = "¡Alerta! Los gastos son elevados."
            textViewAlert.visibility = TextView.VISIBLE
        } else {
            textViewAlert.visibility = TextView.GONE
        }

        // Mostrar transacciones recientes
        val transactionListText = transactions.joinToString("\n") {
            "${it[DatabaseHelper.COLUMN_TYPE]}: ${it[DatabaseHelper.COLUMN_AMOUNT]} (${it[DatabaseHelper.COLUMN_CATEGORY]})"
        }
        textViewTransactionList.text = transactionListText.ifEmpty { "No hay transacciones registradas aún." }
    }
}

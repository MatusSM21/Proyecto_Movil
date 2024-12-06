package com.example.finansmart

import android.os.Bundle
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ResultsActivity : AppCompatActivity() {

    private lateinit var databaseHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_results)

        databaseHelper = DatabaseHelper(this)

        val tableLayout = findViewById<TableLayout>(R.id.tableLayoutResults)
        val textViewMessage = findViewById<TextView>(R.id.textViewMessage)

        // Obtener transacciones
        val transactions = databaseHelper.getAllTransactions()

        var totalIncome = 0.0
        var totalExpense = 0.0

        // Poblar la tabla con datos
        transactions.forEach { transaction ->
            val type = transaction[DatabaseHelper.COLUMN_TYPE] as String
            val amount = transaction[DatabaseHelper.COLUMN_AMOUNT] as Double
            val category = transaction[DatabaseHelper.COLUMN_CATEGORY] as String

            // Crear una fila con líneas divisorias
            val tableRow = TableRow(this)
            tableRow.setPadding(8, 8, 8, 8)
            tableRow.setBackgroundResource(R.drawable.table_row_border) // Agregar bordes a las filas

            // Crear celdas con texto más grande
            val textType = TextView(this)
            textType.text = type
            textType.textSize = 16f // Tamaño del texto aumentado
            textType.setTextColor(if (type == "ingreso") getColor(R.color.green) else getColor(R.color.red))

            val textAmount = TextView(this)
            textAmount.text = "$${String.format("%.2f", amount)}"
            textAmount.textSize = 16f
            textAmount.setTextColor(if (type == "ingreso") getColor(R.color.green) else getColor(R.color.red))

            val textCategory = TextView(this)
            textCategory.text = category
            textCategory.textSize = 16f

            // Agregar celdas a la fila
            tableRow.addView(textType)
            tableRow.addView(textAmount)
            tableRow.addView(textCategory)

            // Agregar fila a la tabla
            tableLayout.addView(tableRow)

            // Calcular totales
            if (type == "ingreso") {
                totalIncome += amount
            } else {
                totalExpense += amount
            }
        }

        // Mostrar mensaje dinámico
        val balance = totalIncome - totalExpense
        textViewMessage.text = when {
            balance > 0 -> "¡Estás economizando! Tu balance es positivo."
            balance == 0.0 -> "Tu balance es neutro. Considera ahorrar más."
            else -> "Cuidado, estás gastando más de lo que ingresas."
        }
        textViewMessage.setTextColor(if (balance >= 0) getColor(R.color.green) else getColor(R.color.red))
    }
}

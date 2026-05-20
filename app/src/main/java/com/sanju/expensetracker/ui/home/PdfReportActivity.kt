package com.sanju.expensetracker.ui.home

import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.sanju.expensetracker.R
import com.sanju.expensetracker.data.repository.ExpenseRepository
import com.sanju.expensetracker.databinding.ActivityPdfReportBinding
import com.sanju.expensetracker.utils.CurrencyUtils
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

class PdfReportActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPdfReportBinding

    private lateinit var expenseRepository: ExpenseRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        expenseRepository = ExpenseRepository(applicationContext)

        binding = ActivityPdfReportBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnGeneratePdf.setOnClickListener {
            generatePdf()
        }
    }

    private fun generatePdf() {
        lifecycleScope.launch {
            val result = expenseRepository.getUserExpenses()

            result.onSuccess { expenses ->
                try {
                    val pdfDocument = PdfDocument()

                    val pageInfo = PdfDocument.PageInfo.Builder(
                        1080,
                        1920,
                        1
                    ).create()

                    val page = pdfDocument.startPage(pageInfo)
                    val canvas = page.canvas
                    val paint = Paint()

                    var y = 100

                    paint.textSize = 40f
                    paint.isFakeBoldText = true

                    canvas.drawText(
                        getString(R.string.expense_report),
                        50f,
                        y.toFloat(),
                        paint
                    )

                    y += 100

                    paint.textSize = 28f
                    paint.isFakeBoldText = false

                    var totalIncome = 0.0
                    var totalExpense = 0.0

                    expenses.forEach { expense ->
                        val line = getString(
                            R.string.pdf_expense_line,
                            expense.title,
                            CurrencyUtils.formatAmount(expense.amount),
                            expense.type
                        )

                        canvas.drawText(
                            line,
                            50f,
                            y.toFloat(),
                            paint
                        )

                        y += 50

                        if (expense.type == getString(R.string.income)) {
                            totalIncome += expense.amount
                        } else {
                            totalExpense += expense.amount
                        }
                    }

                    val balance = totalIncome - totalExpense

                    y += 100

                    paint.textSize = 32f
                    paint.isFakeBoldText = true

                    canvas.drawText(
                        getString(
                            R.string.pdf_total_income,
                            CurrencyUtils.formatAmount(totalIncome)
                        ),
                        50f,
                        y.toFloat(),
                        paint
                    )

                    y += 60

                    canvas.drawText(
                        getString(
                            R.string.pdf_total_expense,
                            CurrencyUtils.formatAmount(totalExpense)
                        ),
                        50f,
                        y.toFloat(),
                        paint
                    )

                    y += 60

                    canvas.drawText(
                        getString(
                            R.string.pdf_balance,
                            CurrencyUtils.formatAmount(balance)
                        ),
                        50f,
                        y.toFloat(),
                        paint
                    )

                    pdfDocument.finishPage(page)

                    val downloadsFolder =
                        Environment.getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_DOWNLOADS
                        )

                    val file = File(
                        downloadsFolder,
                        getString(R.string.expense_report_file_name)
                    )

                    pdfDocument.writeTo(
                        FileOutputStream(file)
                    )

                    pdfDocument.close()

                    Toast.makeText(
                        this@PdfReportActivity,
                        getString(R.string.pdf_saved_downloads),
                        Toast.LENGTH_LONG
                    ).show()

                } catch (e: Exception) {
                    Toast.makeText(
                        this@PdfReportActivity,
                        e.message,
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            result.onFailure {
                Toast.makeText(
                    this@PdfReportActivity,
                    it.message,
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}
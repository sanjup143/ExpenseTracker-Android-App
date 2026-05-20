package com.sanju.expensetracker.ui.home

import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.sanju.expensetracker.R
import com.sanju.expensetracker.data.repository.ExpenseRepository
import com.sanju.expensetracker.databinding.ActivityChartBinding
import com.sanju.expensetracker.utils.CurrencyUtils
import kotlinx.coroutines.launch

class ChartActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChartBinding

    private lateinit var expenseRepository: ExpenseRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        expenseRepository = ExpenseRepository(applicationContext)

        binding = ActivityChartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadChartData()
    }

    private fun loadChartData() {
        lifecycleScope.launch {
            val result = expenseRepository.getExpenseSummary()

            result.onSuccess { summary ->
                showPieChart(
                    income = summary.first,
                    expense = summary.second
                )
            }

            result.onFailure {
                Toast.makeText(
                    this@ChartActivity,
                    it.message ?: getString(R.string.failed_to_load_chart),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun showPieChart(
        income: Double,
        expense: Double
    ) {
        val entries = ArrayList<PieEntry>()

        if (income > 0) {
            entries.add(
                PieEntry(
                    income.toFloat(),
                    getString(R.string.income)
                )
            )
        }

        if (expense > 0) {
            entries.add(
                PieEntry(
                    expense.toFloat(),
                    getString(R.string.expense)
                )
            )
        }

        if (entries.isEmpty()) {
            Toast.makeText(
                this,
                getString(R.string.no_data_available_for_chart),
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val dataSet = PieDataSet(
            entries,
            getString(R.string.income_vs_expense)
        )

        dataSet.colors = listOf(
            Color.rgb(76, 175, 80),
            Color.rgb(244, 67, 54)
        )

        dataSet.valueTextSize = 12f
        dataSet.valueTextColor = Color.WHITE

        val pieData = PieData(dataSet)

        pieData.setValueFormatter(object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return CurrencyUtils.formatAmount(value.toDouble())
            }
        })

        binding.pieChart.data = pieData
        val textColor = if (
            resources.configuration.uiMode and
            android.content.res.Configuration.UI_MODE_NIGHT_MASK ==
            android.content.res.Configuration.UI_MODE_NIGHT_YES
        ) {
            Color.WHITE
        } else {
            Color.BLACK
        }

        binding.pieChart.setEntryLabelColor(textColor)
        binding.pieChart.legend.textColor = textColor
        binding.pieChart.setCenterTextColor(textColor)
        binding.pieChart.description.isEnabled = false
        binding.pieChart.centerText = getString(R.string.income_vs_expense_center)
        binding.pieChart.setCenterTextSize(18f)
        binding.pieChart.animateY(1000)
        binding.pieChart.invalidate()
    }
}
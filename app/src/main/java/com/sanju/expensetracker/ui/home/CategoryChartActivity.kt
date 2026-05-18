package com.sanju.expensetracker.ui.home

import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.sanju.expensetracker.R
import com.sanju.expensetracker.data.repository.ExpenseRepository
import com.sanju.expensetracker.databinding.ActivityCategoryChartBinding
import com.sanju.expensetracker.utils.CurrencyUtils
import kotlinx.coroutines.launch

class CategoryChartActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCategoryChartBinding

    private val expenseRepository = ExpenseRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCategoryChartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadCategoryChart()
    }

    private fun loadCategoryChart() {
        lifecycleScope.launch {
            val result = expenseRepository.getExpenseByCategory()

            result.onSuccess { categoryMap ->
                showBarChart(categoryMap)
            }

            result.onFailure {
                Toast.makeText(
                    this@CategoryChartActivity,
                    it.message ?: getString(R.string.failed_to_load_category_chart),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun showBarChart(categoryMap: Map<String, Double>) {
        if (categoryMap.isEmpty()) {
            Toast.makeText(
                this,
                getString(R.string.no_expense_data_available),
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val categories = categoryMap.keys.toList()
        val entries = ArrayList<BarEntry>()

        categories.forEachIndexed { index, category ->
            entries.add(
                BarEntry(
                    index.toFloat(),
                    categoryMap[category]?.toFloat() ?: 0f
                )
            )
        }

        val dataSet = BarDataSet(
            entries,
            getString(R.string.expenses_by_category)
        )

        dataSet.color = Color.rgb(103, 58, 183)
        dataSet.valueTextSize = 10f
        dataSet.valueTextColor = Color.BLACK

        val barData = BarData(dataSet)
        barData.barWidth = 0.6f

        barData.setValueFormatter(object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return CurrencyUtils.formatAmount(value.toDouble())
            }
        })

        binding.barChart.data = barData
        binding.barChart.description.isEnabled = false
        binding.barChart.animateY(1000)

        binding.barChart.xAxis.valueFormatter =
            IndexAxisValueFormatter(categories)

        binding.barChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        binding.barChart.xAxis.granularity = 1f
        binding.barChart.xAxis.setDrawGridLines(false)

        binding.barChart.axisLeft.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return CurrencyUtils.formatAmount(value.toDouble())
            }
        }

        binding.barChart.axisRight.isEnabled = false
        val textColor = if (
            resources.configuration.uiMode and
            android.content.res.Configuration.UI_MODE_NIGHT_MASK ==
            android.content.res.Configuration.UI_MODE_NIGHT_YES
        ) {
            Color.WHITE
        } else {
            Color.BLACK
        }
        binding.barChart.xAxis.textColor = textColor
        binding.barChart.axisLeft.textColor = textColor
        binding.barChart.legend.textColor = textColor
        binding.barChart.invalidate()
    }
}
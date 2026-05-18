package com.sanju.expensetracker.ui.home

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.sanju.expensetracker.R
import com.sanju.expensetracker.data.repository.ExpenseRepository
import com.sanju.expensetracker.databinding.ActivityHomeBinding
import com.sanju.expensetracker.ui.auth.LoginActivity
import com.sanju.expensetracker.utils.CurrencyUtils
import com.sanju.expensetracker.utils.ReminderScheduler
import kotlinx.coroutines.launch

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    private val firebaseAuth = FirebaseAuth.getInstance()
    private val expenseRepository = ExpenseRepository()

    private val notificationPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted) {
                enableDailyReminder()
            } else {
                Toast.makeText(
                    this,
                    getString(R.string.notification_permission_denied),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        showUserInfo()
        setupClickListeners()
    }

    override fun onResume() {
        super.onResume()
        loadSummary()
    }

    private fun showUserInfo() {
        val email = firebaseAuth.currentUser?.email ?: getString(R.string.user)

        binding.tvWelcome.text = getString(R.string.welcome)
        binding.tvSubtitle.text = email
    }

    private fun setupClickListeners() {
        binding.btnAddExpense.setOnClickListener {
            openScreen(AddExpenseActivity::class.java)
        }

        binding.btnViewExpenses.setOnClickListener {
            openScreen(ExpenseListActivity::class.java)
        }

        binding.btnViewCharts.setOnClickListener {
            openScreen(ChartActivity::class.java)
        }

        binding.btnCategoryAnalytics.setOnClickListener {
            openScreen(CategoryChartActivity::class.java)
        }

        binding.btnMonthlySummary.setOnClickListener {
            openScreen(MonthlySummaryActivity::class.java)
        }

        binding.btnBudget.setOnClickListener {
            openScreen(BudgetActivity::class.java)
        }

        binding.btnPdfReport.setOnClickListener {
            openScreen(PdfReportActivity::class.java)
        }

        binding.btnEnableReminder.setOnClickListener {
            handleReminderPermission()
        }

        binding.btnLogout.setOnClickListener {
            logoutUser()
        }
    }

    private fun openScreen(activityClass: Class<*>) {
        startActivity(
            Intent(
                this,
                activityClass
            )
        )
    }

    private fun handleReminderPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                enableDailyReminder()
            } else {
                notificationPermissionLauncher.launch(
                    Manifest.permission.POST_NOTIFICATIONS
                )
            }
        } else {
            enableDailyReminder()
        }
    }

    private fun enableDailyReminder() {
        ReminderScheduler.scheduleDailyReminder(this)

        Toast.makeText(
            this,
            getString(R.string.daily_reminder_enabled),
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun loadSummary() {
        lifecycleScope.launch {
            val result = expenseRepository.getExpenseSummary()

            result.onSuccess { summary ->
                val income = summary.first
                val expense = summary.second
                val balance = summary.third

                binding.tvIncome.text = CurrencyUtils.formatAmount(income)
                binding.tvExpense.text = CurrencyUtils.formatAmount(expense)
                binding.tvBalance.text = CurrencyUtils.formatAmount(balance)
            }

            result.onFailure {
                Toast.makeText(
                    this@HomeActivity,
                    it.message ?: getString(R.string.failed_to_load_summary),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun logoutUser() {
        firebaseAuth.signOut()

        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }
}
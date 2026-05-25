package com.sanju.expensetracker.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sanju.expensetracker.R
import com.sanju.expensetracker.data.model.Expense
import com.sanju.expensetracker.databinding.ItemExpenseBinding
import com.sanju.expensetracker.utils.CategoryIconUtils
import com.sanju.expensetracker.utils.CurrencyUtils

class ExpenseAdapter(
    private val onEditClick: (Expense) -> Unit,
    private val onDeleteClick: (Expense) -> Unit
) : RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder>() {

    private val expenses = mutableListOf<Expense>()

    inner class ExpenseViewHolder(
        private val binding: ItemExpenseBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(expense: Expense) {
            binding.tvCategoryIcon.text =
                CategoryIconUtils.getCategoryIcon(expense.category)

            binding.tvTitle.text = expense.title
            binding.tvAmount.text = CurrencyUtils.formatAmount(expense.amount)

            binding.tvCategory.text = binding.root.context.getString(
                R.string.expense_category_type,
                expense.category,
                expense.type
            )

            binding.btnEdit.setOnClickListener {
                onEditClick(expense)
            }

            binding.btnDelete.setOnClickListener {
                onDeleteClick(expense)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ExpenseViewHolder {
        val binding = ItemExpenseBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return ExpenseViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ExpenseViewHolder,
        position: Int
    ) {
        holder.bind(expenses[position])
    }

    override fun getItemCount(): Int {
        return expenses.size
    }

    fun updateExpenses(newExpenses: List<Expense>) {
        expenses.clear()
        expenses.addAll(newExpenses)
        notifyDataSetChanged()
    }
}
package org.cedarstar.android.data.repository

import kotlinx.coroutines.flow.StateFlow
import org.cedarstar.android.data.model.PocketMoneyState
import org.cedarstar.android.data.model.PocketMoneyTransaction
import org.cedarstar.android.data.model.IncomeCategory
import org.cedarstar.android.data.model.ExpenseCategory
import org.cedarstar.android.data.model.LoveSubCategory
import org.cedarstar.android.data.model.PocketMoneyConfig

interface PocketMoneyRepository {
    val state: StateFlow<PocketMoneyState>
    suspend fun getTransactions(startTime: Long? = null, endTime: Long? = null): List<PocketMoneyTransaction>
    suspend fun addIncome(amount: Double, category: IncomeCategory, note: String = "", timestampUtcMillis: Long? = null)
    suspend fun addExpense(
        amount: Double,
        category: ExpenseCategory,
        subCategory: LoveSubCategory? = null,
        note: String = "",
        timestampUtcMillis: Long? = null,
    )
    suspend fun deleteTransaction(id: String)
    suspend fun updateConfig(config: PocketMoneyConfig)
}

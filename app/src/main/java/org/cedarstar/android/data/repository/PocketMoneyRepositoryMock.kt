package org.cedarstar.android.data.repository

import java.time.YearMonth
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.util.Locale
import java.util.UUID
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.cedarstar.android.data.model.ExpenseCategory
import org.cedarstar.android.data.model.IncomeCategory
import org.cedarstar.android.data.model.LoveSubCategory
import org.cedarstar.android.data.model.PocketMoneyConfig
import org.cedarstar.android.data.model.PocketMoneyState
import org.cedarstar.android.data.model.PocketMoneyTransaction
import org.cedarstar.android.data.model.TransactionType

class PocketMoneyRepositoryMock @Inject constructor() : PocketMoneyRepository {

    private val config = PocketMoneyConfig()

    private val interestDaily: Double =
        String.format(
            Locale.US,
            "%.4f",
            config.monthlyAllowance * config.annualInterestRate / 365.0,
        ).toDouble()

    private val initialState: PocketMoneyState = run {
        val ym = YearMonth.of(2026, 5)
        val days = ym.lengthOfMonth()
        fun z(day: Int, hour: Int, minute: Int): Long =
            ZonedDateTime.of(ym.year, ym.monthValue, day, hour, minute, 0, 0, ZoneOffset.UTC)
                .toInstant()
                .toEpochMilli()

        val pending = mutableListOf<Pending>()
        pending += Pending(
            timestamp = z(1, 0, 0),
            amount = config.monthlyAllowance,
            type = TransactionType.INCOME,
            incomeCategory = IncomeCategory.ALLOWANCE,
        )
        for (day in 1..days) {
            pending += Pending(
                timestamp = z(day, 12, 0),
                amount = interestDaily,
                type = TransactionType.INCOME,
                incomeCategory = IncomeCategory.INTEREST,
            )
        }
        pending += Pending(
            timestamp = z(5, 15, 30),
            amount = 12.0,
            type = TransactionType.EXPENSE,
            expenseCategory = ExpenseCategory.LOVE,
            loveSubCategory = LoveSubCategory.SNACK,
        )
        pending += Pending(
            timestamp = z(12, 18, 0),
            amount = 28.0,
            type = TransactionType.EXPENSE,
            expenseCategory = ExpenseCategory.LOVE,
            loveSubCategory = LoveSubCategory.GIFT,
        )
        pending += Pending(
            timestamp = z(17, 9, 0),
            amount = 8.5,
            type = TransactionType.EXPENSE,
            expenseCategory = ExpenseCategory.DAILY,
        )
        pending += Pending(
            timestamp = z(24, 14, 0),
            amount = 5.0,
            type = TransactionType.EXPENSE,
            expenseCategory = ExpenseCategory.FINE,
        )

        val sorted = pending.sortedBy { it.timestamp }
        var balance = 0.0
        val transactions = sorted.mapIndexed { index, p ->
            balance =
                when (p.type) {
                    TransactionType.INCOME -> balance + p.amount
                    TransactionType.EXPENSE -> balance - p.amount
                }
            PocketMoneyTransaction(
                id = "pm_${index + 1}",
                amount = p.amount,
                type = p.type,
                incomeCategory = p.incomeCategory,
                expenseCategory = p.expenseCategory,
                loveSubCategory = p.loveSubCategory,
                note = p.note,
                timestamp = p.timestamp,
                balanceAfter = balance,
                requestedByAi = false,
            )
        }
        PocketMoneyState(
            balance = balance,
            transactions = transactions,
            config = config,
        )
    }

    private val _state = MutableStateFlow(initialState)

    override val state: StateFlow<PocketMoneyState> = _state

    override suspend fun getTransactions(startTime: Long?, endTime: Long?): List<PocketMoneyTransaction> {
        return _state.value.transactions.filter { tx ->
            (startTime == null || tx.timestamp >= startTime) &&
                (endTime == null || tx.timestamp <= endTime)
        }
    }

    override suspend fun addIncome(
        amount: Double,
        category: IncomeCategory,
        note: String,
        timestampUtcMillis: Long?,
    ) {
        val cur = _state.value
        val ts = timestampUtcMillis ?: System.currentTimeMillis()
        val tx =
            PocketMoneyTransaction(
                id = UUID.randomUUID().toString(),
                amount = amount,
                type = TransactionType.INCOME,
                incomeCategory = category,
                expenseCategory = null,
                loveSubCategory = null,
                note = note,
                timestamp = ts,
                balanceAfter = 0.0,
                requestedByAi = false,
            )
        _state.value = replayState(cur.transactions + tx, cur.config)
    }

    override suspend fun addExpense(
        amount: Double,
        category: ExpenseCategory,
        subCategory: LoveSubCategory?,
        note: String,
        timestampUtcMillis: Long?,
    ) {
        val cur = _state.value
        val ts = timestampUtcMillis ?: System.currentTimeMillis()
        val tx =
            PocketMoneyTransaction(
                id = UUID.randomUUID().toString(),
                amount = amount,
                type = TransactionType.EXPENSE,
                incomeCategory = null,
                expenseCategory = category,
                loveSubCategory = if (category == ExpenseCategory.LOVE) subCategory else null,
                note = note,
                timestamp = ts,
                balanceAfter = 0.0,
                requestedByAi = false,
            )
        _state.value = replayState(cur.transactions + tx, cur.config)
    }

    override suspend fun deleteTransaction(id: String) {
        val cur = _state.value
        val remaining = cur.transactions.filter { it.id != id }
        _state.value = replayState(remaining, cur.config)
    }

    override suspend fun updateConfig(config: PocketMoneyConfig) {
        val cur = _state.value
        _state.value = replayState(cur.transactions, config)
    }

    private fun replayState(transactions: List<PocketMoneyTransaction>, config: PocketMoneyConfig): PocketMoneyState {
        val sorted = transactions.sortedWith(compareBy({ it.timestamp }, { it.id }))
        var balance = 0.0
        val replayed =
            sorted.map { tx ->
                balance =
                    when (tx.type) {
                        TransactionType.INCOME -> balance + tx.amount
                        TransactionType.EXPENSE -> balance - tx.amount
                    }
                tx.copy(balanceAfter = balance)
            }
        return PocketMoneyState(balance = balance, transactions = replayed, config = config)
    }

    private data class Pending(
        val timestamp: Long,
        val amount: Double,
        val type: TransactionType,
        val incomeCategory: IncomeCategory? = null,
        val expenseCategory: ExpenseCategory? = null,
        val loveSubCategory: LoveSubCategory? = null,
        val note: String = "",
    )
}

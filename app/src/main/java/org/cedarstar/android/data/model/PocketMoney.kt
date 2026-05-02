package org.cedarstar.android.data.model

enum class TransactionType { INCOME, EXPENSE }

enum class IncomeCategory {
    ALLOWANCE,  // 零花钱
    EARNING,    // 创收
    REWARD,     // 打赏
    INTEREST    // 利息（每日自动）
}

enum class ExpenseCategory {
    LOVE,   // 恋爱开销
    DAILY,  // 日常开销
    FINE    // 罚款
}

enum class LoveSubCategory { SNACK, GIFT }

data class PocketMoneyTransaction(
    val id: String,
    val amount: Double,
    val type: TransactionType,
    val incomeCategory: IncomeCategory? = null,
    val expenseCategory: ExpenseCategory? = null,
    val loveSubCategory: LoveSubCategory? = null,
    val note: String = "",
    val timestamp: Long,
    val balanceAfter: Double,
    val requestedByAi: Boolean = false
)

data class PocketMoneyConfig(
    val monthlyAllowance: Double = 50.0,
    val annualInterestRate: Double = 0.015
)

data class PocketMoneyState(
    val balance: Double = 0.0,
    val transactions: List<PocketMoneyTransaction> = emptyList(),
    val config: PocketMoneyConfig = PocketMoneyConfig()
)

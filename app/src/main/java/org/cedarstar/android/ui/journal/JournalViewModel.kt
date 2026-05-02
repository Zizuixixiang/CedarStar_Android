package org.cedarstar.android.ui.journal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.cedarstar.android.data.model.ExpenseCategory
import org.cedarstar.android.data.model.IncomeCategory
import org.cedarstar.android.data.model.LoveSubCategory
import org.cedarstar.android.data.model.PocketMoneyConfig
import org.cedarstar.android.data.model.PocketMoneyState
import org.cedarstar.android.data.repository.PocketMoneyRepository

@HiltViewModel
class JournalViewModel @Inject constructor(
    private val pocketMoneyRepository: PocketMoneyRepository,
) : ViewModel() {
    val pocketMoneyState =
        pocketMoneyRepository.state.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            PocketMoneyState(),
        )

    fun addIncome(amount: Double, category: IncomeCategory, note: String = "", timestampUtcMillis: Long? = null) {
        viewModelScope.launch {
            pocketMoneyRepository.addIncome(amount, category, note, timestampUtcMillis)
        }
    }

    fun addExpense(
        amount: Double,
        category: ExpenseCategory,
        subCategory: LoveSubCategory? = null,
        note: String = "",
        timestampUtcMillis: Long? = null,
    ) {
        viewModelScope.launch {
            pocketMoneyRepository.addExpense(amount, category, subCategory, note, timestampUtcMillis)
        }
    }

    fun deleteTransaction(id: String) {
        viewModelScope.launch {
            pocketMoneyRepository.deleteTransaction(id)
        }
    }

    fun updateConfig(config: PocketMoneyConfig) {
        viewModelScope.launch {
            pocketMoneyRepository.updateConfig(config)
        }
    }
}

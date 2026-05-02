@file:OptIn(ExperimentalFoundationApi::class)

package org.cedarstar.android.ui.journal

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale
import org.cedarstar.android.data.model.ExpenseCategory
import org.cedarstar.android.data.model.IncomeCategory
import org.cedarstar.android.data.model.LoveSubCategory
import org.cedarstar.android.data.model.PocketMoneyTransaction
import org.cedarstar.android.data.model.TransactionType

private val IncomeGreen = Color(0xFF2E7D32)
private val ExpenseRed = Color(0xFFC62828)
private val ChartLineColor = Color(0xFF5B8DEF)

private fun filterTransactions(
    transactions: List<PocketMoneyTransaction>,
    period: String,
): List<PocketMoneyTransaction> {
    val calendar = Calendar.getInstance()
    return when (period) {
        "day" -> {
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            val startOfDay = calendar.timeInMillis
            calendar.add(Calendar.DAY_OF_MONTH, 1)
            val endOfDay = calendar.timeInMillis
            transactions.filter { it.timestamp in startOfDay until endOfDay }
        }
        "month" -> {
            calendar.set(Calendar.DAY_OF_MONTH, 1)
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            val startOfMonth = calendar.timeInMillis
            calendar.add(Calendar.MONTH, 1)
            val endOfMonth = calendar.timeInMillis
            transactions.filter { it.timestamp in startOfMonth until endOfMonth }
        }
        "year" -> {
            calendar.set(Calendar.DAY_OF_YEAR, 1)
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            val startOfYear = calendar.timeInMillis
            calendar.add(Calendar.YEAR, 1)
            val endOfYear = calendar.timeInMillis
            transactions.filter { it.timestamp in startOfYear until endOfYear }
        }
        else -> transactions
    }
}

private fun categoryDisplayName(tx: PocketMoneyTransaction): String =
    when (tx.type) {
        TransactionType.INCOME ->
            when (tx.incomeCategory) {
                IncomeCategory.ALLOWANCE -> "零花钱"
                IncomeCategory.EARNING -> "创收"
                IncomeCategory.REWARD -> "打赏"
                IncomeCategory.INTEREST -> "利息"
                null -> "收入"
            }
        TransactionType.EXPENSE ->
            when (tx.expenseCategory) {
                ExpenseCategory.LOVE ->
                    when (tx.loveSubCategory) {
                        LoveSubCategory.SNACK -> "恋爱开销（零食）"
                        LoveSubCategory.GIFT -> "恋爱开销（礼物）"
                        null -> "恋爱开销"
                    }
                ExpenseCategory.DAILY -> "日常开销"
                ExpenseCategory.FINE -> "罚款"
                null -> "支出"
            }
    }

private fun formatMoney(value: Double): String =
    String.format(Locale.getDefault(), "%.2f", value)

private fun formatTransactionAmount(tx: PocketMoneyTransaction): String =
    if (tx.type == TransactionType.INCOME && tx.incomeCategory == IncomeCategory.INTEREST) {
        String.format(Locale.getDefault(), "%.4f", tx.amount)
    } else {
        String.format(Locale.getDefault(), "%.2f", tx.amount)
    }

private val timeFormatter: DateTimeFormatter =
    DateTimeFormatter.ofPattern("HH:mm", Locale.getDefault())

private val dateTimeFormatter: DateTimeFormatter =
    DateTimeFormatter.ofPattern("MM/dd HH:mm", Locale.getDefault())

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PocketMoneyScreen(viewModel: JournalViewModel) {
    val pocketMoney by viewModel.pocketMoneyState.collectAsStateWithLifecycle()
    var period by remember { mutableStateOf("month") }
    var showSheet by remember { mutableStateOf(false) }
    var showSettings by remember { mutableStateOf(false) }
    var transactionPendingDelete by remember { mutableStateOf<PocketMoneyTransaction?>(null) }

    if (showSettings) {
        PocketMoneySettingsScreen(
            onBack = { showSettings = false },
            viewModel = viewModel,
        )
        return
    }

    val filtered =
        remember(period, pocketMoney.transactions) {
            filterTransactions(pocketMoney.transactions, period)
        }
    val chartSeries =
        remember(filtered) {
            filtered.sortedBy { it.timestamp }
        }
    val transactionsNewestFirst =
        remember(filtered) {
            filtered.sortedByDescending { it.timestamp }
        }

    val totalIncome =
        remember(filtered) {
            filtered.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
        }
    val totalExpense =
        remember(filtered) {
            filtered.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }
        }

    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .fillMaxSize(),
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(top = 16.dp, bottom = 88.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item {
                BalanceCard(
                    balance = pocketMoney.balance,
                    monthlyAllowance = pocketMoney.config.monthlyAllowance,
                    annualRate = pocketMoney.config.annualInterestRate,
                    onClick = { showSettings = true },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                )
            }
            item {
                PeriodFilterRow(
                    period = period,
                    onPeriodChange = { period = it },
                    modifier = Modifier.padding(horizontal = 16.dp),
                )
            }
            item {
                IncomeExpenseStatsRow(
                    income = totalIncome,
                    expense = totalExpense,
                    modifier = Modifier.padding(horizontal = 16.dp),
                )
            }
            item {
                BalanceLineChart(
                    transactions = chartSeries,
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                )
            }
            items(
                items = transactionsNewestFirst,
                key = { it.id },
            ) { tx ->
                TransactionRow(
                    tx = tx,
                    useShortTime = period == "day",
                    onLongClick = { transactionPendingDelete = tx },
                    modifier = Modifier.padding(horizontal = 16.dp),
                )
            }
        }
        FloatingActionButton(
            onClick = { showSheet = true },
            modifier =
                Modifier
                    .align(Alignment.BottomEnd)
                    .padding(24.dp),
        ) {
            Icon(Icons.Filled.Add, contentDescription = "添加记账")
        }
    }

    if (showSheet) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ModalBottomSheet(
            onDismissRequest = { showSheet = false },
            sheetState = sheetState,
        ) {
            AddTransactionSheet(
                viewModel = viewModel,
                onDismiss = { showSheet = false },
            )
        }
    }

    transactionPendingDelete?.let { tx ->
        AlertDialog(
            onDismissRequest = { transactionPendingDelete = null },
            title = { Text("确认删除") },
            text = { Text("确认删除这条记录？") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteTransaction(tx.id)
                        transactionPendingDelete = null
                    },
                ) {
                    Text("确认")
                }
            },
            dismissButton = {
                TextButton(onClick = { transactionPendingDelete = null }) {
                    Text("取消")
                }
            },
        )
    }
}

@Composable
private fun BalanceCard(
    balance: Double,
    monthlyAllowance: Double,
    annualRate: Double,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier =
            modifier
                .fillMaxWidth()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onClick,
                ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Column(Modifier.padding(20.dp)) {
            Text(
                text = "余额",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = formatMoney(balance),
                style = MaterialTheme.typography.displaySmall,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text =
                    "月度额度 ${formatMoney(monthlyAllowance)} · 年利率 ${
                        String.format(
                            Locale.getDefault(),
                            "%.2f",
                            annualRate * 100,
                        )
                    }%",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun PeriodFilterRow(
    period: String,
    onPeriodChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        FilterChip(
            selected = period == "day",
            onClick = { onPeriodChange("day") },
            label = { Text("日") },
            modifier = Modifier.weight(1f),
        )
        FilterChip(
            selected = period == "month",
            onClick = { onPeriodChange("month") },
            label = { Text("月") },
            modifier = Modifier.weight(1f),
        )
        FilterChip(
            selected = period == "year",
            onClick = { onPeriodChange("year") },
            label = { Text("年") },
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun IncomeExpenseStatsRow(
    income: Double,
    expense: Double,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Column(Modifier.weight(1f)) {
            Text("本期收入", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(
                text = "+${formatMoney(income)}",
                style = MaterialTheme.typography.titleMedium,
                color = IncomeGreen,
            )
        }
        Column(Modifier.weight(1f), horizontalAlignment = Alignment.End) {
            Text("本期支出", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(
                text = "-${formatMoney(expense)}",
                style = MaterialTheme.typography.titleMedium,
                color = ExpenseRed,
                textAlign = TextAlign.End,
            )
        }
    }
}

@Composable
private fun BalanceLineChart(
    transactions: List<PocketMoneyTransaction>,
    modifier: Modifier = Modifier,
) {
    val pointsData =
        remember(transactions) {
            val sorted = transactions.sortedBy { it.timestamp }
            when {
                sorted.isEmpty() -> emptyList()
                sorted.size == 1 -> listOf(sorted[0], sorted[0])
                else -> sorted
            }
        }
    Column(modifier) {
        Text(
            "余额走势",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 4.dp),
        )
        val chartHeight = 120.dp
        if (pointsData.isEmpty()) {
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(chartHeight)
                        .background(
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                            RoundedCornerShape(8.dp),
                        ),
                contentAlignment = Alignment.Center,
            ) {
                Text("暂无数据", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            Canvas(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(chartHeight)
                    .background(
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
                        RoundedCornerShape(8.dp),
                    ),
        ) {
            val pad = 8.dp.toPx()
            val w = size.width - pad * 2
            val h = size.height - pad * 2
            val balances = pointsData.map { it.balanceAfter }
            var minB = balances.minOrNull()!!
            var maxB = balances.maxOrNull()!!
            if (minB == maxB) {
                minB -= 1.0
                maxB += 1.0
            }
            val times = pointsData.map { it.timestamp.toDouble() }
            var minT = times.minOrNull()!!
            var maxT = times.maxOrNull()!!
            if (minT == maxT) {
                minT -= 86_400_000.0
                maxT += 86_400_000.0
            }

            fun xAt(i: Int): Float =
                pad + ((times[i] - minT) / (maxT - minT)).toFloat() * w

            val offsets =
                pointsData.indices.map { i ->
                    val x = xAt(i)
                    val b = balances[i]
                    val tNorm = ((b - minB) / (maxB - minB)).toFloat()
                    val y = pad + h - tNorm * h
                    Offset(x, y)
                }

            if (offsets.size >= 2) {
                val fillPath = Path().apply {
                    moveTo(offsets.first().x, offsets.first().y)
                    for (i in 1 until offsets.size) {
                        lineTo(offsets[i].x, offsets[i].y)
                    }
                    lineTo(offsets.last().x, size.height - pad)
                    lineTo(offsets.first().x, size.height - pad)
                    close()
                }
                drawPath(
                    path = fillPath,
                    brush =
                        Brush.verticalGradient(
                            colors =
                                listOf(
                                    ChartLineColor.copy(alpha = 0.45f),
                                    Color.Transparent,
                                ),
                            startY = pad,
                            endY = size.height - pad,
                        ),
                )
            }

            val linePath = Path().apply {
                moveTo(offsets.first().x, offsets.first().y)
                for (i in 1 until offsets.size) {
                    lineTo(offsets[i].x, offsets[i].y)
                }
            }
            drawPath(
                path = linePath,
                color = ChartLineColor,
                style =
                    Stroke(
                        width = 2.dp.toPx(),
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round,
                    ),
            )
        }
        }
    }
}

@Composable
private fun TransactionRow(
    tx: PocketMoneyTransaction,
    useShortTime: Boolean,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val zone = ZoneId.systemDefault()
    val zdt = remember(tx.timestamp) { Instant.ofEpochMilli(tx.timestamp).atZone(zone) }
    val timeText =
        remember(tx.timestamp, useShortTime) {
            if (useShortTime) {
                timeFormatter.format(zdt)
            } else {
                dateTimeFormatter.format(zdt)
            }
        }
    val iconColor =
        if (tx.type == TransactionType.INCOME) {
            IncomeGreen
        } else {
            ExpenseRed
        }
    val amountPrefix = if (tx.type == TransactionType.INCOME) "+" else "-"
    val amountColor = iconColor

    Surface(
        modifier =
            modifier
                .fillMaxWidth()
                .combinedClickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = {},
                    onLongClick = onLongClick,
                ),
        shape = RoundedCornerShape(12.dp),
        tonalElevation = 1.dp,
        color = MaterialTheme.colorScheme.surface,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                modifier = Modifier.weight(0.38f),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier =
                        Modifier
                            .size(12.dp)
                            .background(iconColor, CircleShape),
                )
                Spacer(Modifier.width(10.dp))
                Text(
                    text = categoryDisplayName(tx),
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Column(
                modifier = Modifier.weight(0.34f),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                if (tx.note.isNotBlank()) {
                    Text(
                        text = tx.note,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Center,
                    )
                }
                if (tx.requestedByAi) {
                    Spacer(Modifier.height(4.dp))
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = MaterialTheme.colorScheme.tertiaryContainer,
                    ) {
                        Text(
                            "小克申请",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onTertiaryContainer,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        )
                    }
                }
            }
            Column(
                modifier = Modifier.weight(0.28f),
                horizontalAlignment = Alignment.End,
            ) {
                Text(
                    text = "$amountPrefix${formatTransactionAmount(tx)}",
                    style = MaterialTheme.typography.titleSmall,
                    color = amountColor,
                )
                Text(
                    text = timeText,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

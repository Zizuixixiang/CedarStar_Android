package org.cedarstar.android.ui.journal

import android.app.DatePickerDialog
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import org.cedarstar.android.data.model.ExpenseCategory
import org.cedarstar.android.data.model.IncomeCategory
import org.cedarstar.android.data.model.LoveSubCategory

private val IncomeGreen = Color(0xFF2E7D32)
private val ExpenseRed = Color(0xFFC62828)

private enum class SheetTab { INCOME, EXPENSE }

private sealed class ExpenseGridChoice {
    data object Snack : ExpenseGridChoice()

    data object Gift : ExpenseGridChoice()

    data object Daily : ExpenseGridChoice()

    data object Fine : ExpenseGridChoice()
}

private data class IncomeGridItem(
    val category: IncomeCategory,
    val label: String,
    val emoji: String,
)

private data class ExpenseGridItem(
    val choice: ExpenseGridChoice,
    val label: String,
    val emoji: String,
)

private val incomeGridItems =
    listOf(
        IncomeGridItem(IncomeCategory.ALLOWANCE, "零花钱", "💰"),
        IncomeGridItem(IncomeCategory.EARNING, "创收", "💼"),
        IncomeGridItem(IncomeCategory.REWARD, "打赏", "🎁"),
        IncomeGridItem(IncomeCategory.INTEREST, "利息", "📈"),
    )

private val expenseGridItems =
    listOf(
        ExpenseGridItem(ExpenseGridChoice.Snack, "零食", "🍡"),
        ExpenseGridItem(ExpenseGridChoice.Gift, "礼物", "🎀"),
        ExpenseGridItem(ExpenseGridChoice.Daily, "日常开销", "🛒"),
        ExpenseGridItem(ExpenseGridChoice.Fine, "罚款", "⚡"),
    )

private val dateDisplayFormatter: DateTimeFormatter =
    DateTimeFormatter.ofPattern("MM/dd")

private fun LocalDate.noonUtcEpochMillis(): Long =
    ZonedDateTime.of(year, monthValue, dayOfMonth, 12, 0, 0, 0, ZoneOffset.UTC)
        .toInstant()
        .toEpochMilli()

private fun parseAmount(raw: String): Double {
    if (raw.isEmpty() || raw == ".") return 0.0
    return raw.toDoubleOrNull() ?: 0.0
}

private fun appendAmountDigit(current: String, digit: Char): String {
    if (digit == '.') {
        if (current.contains('.')) return current
        return if (current.isEmpty()) "0." else current + '.'
    }
    val dot = current.indexOf('.')
    if (dot >= 0) {
        val frac = current.length - dot - 1
        if (frac >= 2) return current
    }
    if (current == "0" && digit != '.') return digit.toString()
    return current + digit
}

@Composable
fun AddTransactionSheet(
    viewModel: JournalViewModel,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val density = LocalDensity.current
    val imeVisible = WindowInsets.ime.getBottom(density) > 0
    val showNumpad = !imeVisible
    var selectedTab by remember { mutableStateOf(SheetTab.INCOME) }
    var incomeCategory by remember { mutableStateOf(IncomeCategory.ALLOWANCE) }
    var expenseChoice by remember { mutableStateOf<ExpenseGridChoice>(ExpenseGridChoice.Snack) }
    var amountRaw by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }

    val parsed = remember(amountRaw) { parseAmount(amountRaw) }
    val canSave = parsed > 0
    val amountColor =
        when (selectedTab) {
            SheetTab.INCOME -> IncomeGreen
            SheetTab.EXPENSE -> ExpenseRed
        }

    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .imePadding()
                .padding(horizontal = 16.dp)
                .padding(bottom = 24.dp),
    ) {
        IncomeExpenseTabRow(
            selected = selectedTab,
            onSelect = { selectedTab = it },
            modifier = Modifier.fillMaxWidth(),
        )
        when (selectedTab) {
            SheetTab.INCOME ->
                IncomeCategoryGrid(
                    selected = incomeCategory,
                    onSelect = { incomeCategory = it },
                )
            SheetTab.EXPENSE ->
                ExpenseCategoryGrid(
                    selected = expenseChoice,
                    onSelect = { expenseChoice = it },
                )
        }
        Spacer(Modifier.height(12.dp))
        DateNoteActionRow(
            selectedDate = selectedDate,
            note = note,
            showNumpad = showNumpad,
            onNoteChange = { note = it },
            onPickDate = {
                focusManager.clearFocus()
                DatePickerDialog(
                    context,
                    { _, y, m, d ->
                        selectedDate = LocalDate.of(y, m + 1, d)
                    },
                    selectedDate.year,
                    selectedDate.monthValue - 1,
                    selectedDate.dayOfMonth,
                ).show()
            },
            onClearFocusClick = { focusManager.clearFocus() },
        )
        Spacer(Modifier.height(12.dp))
        AnimatedVisibility(visible = showNumpad) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = if (amountRaw.isEmpty()) "0" else amountRaw,
                    fontSize = 36.sp,
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                    textAlign = TextAlign.End,
                    color = amountColor,
                )
                Spacer(Modifier.height(8.dp))
                AmountKeypad(
                    canSave = canSave,
                    onDigit = { ch ->
                        focusManager.clearFocus()
                        amountRaw = appendAmountDigit(amountRaw, ch)
                    },
                    onBackspace = {
                        focusManager.clearFocus()
                        if (amountRaw.isNotEmpty()) {
                            amountRaw = amountRaw.dropLast(1)
                        }
                    },
                    onSave = {
                        if (!canSave) return@AmountKeypad
                        focusManager.clearFocus()
                        val ts = selectedDate.noonUtcEpochMillis()
                        when (selectedTab) {
                            SheetTab.INCOME ->
                                viewModel.addIncome(
                                    amount = parsed,
                                    category = incomeCategory,
                                    note = note.trim(),
                                    timestampUtcMillis = ts,
                                )
                            SheetTab.EXPENSE -> {
                                val (cat, sub) =
                                    when (expenseChoice) {
                                        ExpenseGridChoice.Snack ->
                                            ExpenseCategory.LOVE to LoveSubCategory.SNACK
                                        ExpenseGridChoice.Gift ->
                                            ExpenseCategory.LOVE to LoveSubCategory.GIFT
                                        ExpenseGridChoice.Daily ->
                                            ExpenseCategory.DAILY to null
                                        ExpenseGridChoice.Fine ->
                                            ExpenseCategory.FINE to null
                                    }
                                viewModel.addExpense(
                                    amount = parsed,
                                    category = cat,
                                    subCategory = sub,
                                    note = note.trim(),
                                    timestampUtcMillis = ts,
                                )
                            }
                        }
                        onDismiss()
                    },
                )
            }
        }
    }
}

@Composable
private fun IncomeExpenseTabRow(
    selected: SheetTab,
    onSelect: (SheetTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    val underlineColor = MaterialTheme.colorScheme.primary
    Row(
        modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(0.dp),
        verticalAlignment = Alignment.Top,
    ) {
        TabLabel(
            text = "收入",
            selected = selected == SheetTab.INCOME,
            underlineColor = underlineColor,
            onClick = { onSelect(SheetTab.INCOME) },
            modifier = Modifier.weight(1f),
        )
        TabLabel(
            text = "支出",
            selected = selected == SheetTab.EXPENSE,
            underlineColor = underlineColor,
            onClick = { onSelect(SheetTab.EXPENSE) },
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun TabLabel(
    text: String,
    selected: Boolean,
    underlineColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .clickable(onClick = onClick)
                .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            color =
                if (selected) {
                    MaterialTheme.colorScheme.onSurface
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                },
        )
        Spacer(Modifier.height(4.dp))
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(2.dp)
                    .background(if (selected) underlineColor else Color.Transparent),
        )
    }
}

@Composable
private fun IncomeCategoryGrid(
    selected: IncomeCategory,
    onSelect: (IncomeCategory) -> Unit,
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(4),
        modifier =
            Modifier
                .fillMaxWidth()
                .height(120.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        items(incomeGridItems, key = { it.category.name }) { item ->
            CategoryCell(
                emoji = item.emoji,
                label = item.label,
                selected = selected == item.category,
                onClick = { onSelect(item.category) },
            )
        }
    }
}

@Composable
private fun ExpenseCategoryGrid(
    selected: ExpenseGridChoice,
    onSelect: (ExpenseGridChoice) -> Unit,
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(4),
        modifier =
            Modifier
                .fillMaxWidth()
                .height(120.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        items(expenseGridItems, key = { it.label }) { item ->
            CategoryCell(
                emoji = item.emoji,
                label = item.label,
                selected = selected == item.choice,
                onClick = { onSelect(item.choice) },
            )
        }
    }
}

@Composable
private fun CategoryCell(
    emoji: String,
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val ringColor =
        if (selected) {
            MaterialTheme.colorScheme.primary
        } else {
            Color.Transparent
        }
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .border(width = if (selected) 2.dp else 0.dp, color = ringColor, shape = RoundedCornerShape(12.dp))
                .clickable(onClick = onClick)
                .padding(vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier =
                Modifier
                    .size(48.dp)
                    .background(MaterialTheme.colorScheme.secondaryContainer, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Text(text = emoji, style = MaterialTheme.typography.headlineSmall)
        }
        Spacer(Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Composable
private fun DateNoteActionRow(
    selectedDate: LocalDate,
    note: String,
    showNumpad: Boolean,
    onNoteChange: (String) -> Unit,
    onPickDate: () -> Unit,
    onClearFocusClick: () -> Unit,
) {
    val today = LocalDate.now()
    val dateLabel =
        if (selectedDate == today) {
            "📅 今天"
        } else {
            "📅 ${dateDisplayFormatter.format(selectedDate)}"
        }
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = dateLabel,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier =
                Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable(onClick = onPickDate)
                    .padding(vertical = 8.dp, horizontal = 4.dp),
        )
        BasicTextField(
            value = note,
            onValueChange = onNoteChange,
            modifier =
                Modifier
                    .weight(1f)
                    .padding(start = 16.dp),
            textStyle =
                MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.End,
                ),
            singleLine = true,
            keyboardOptions =
                KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences,
                ),
            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
            decorationBox = { innerTextField ->
                Box(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp, horizontal = 4.dp),
                    contentAlignment = Alignment.CenterEnd,
                ) {
                    if (note.isEmpty() && showNumpad) {
                        Text(
                            text = "添加备注",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.End,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                    innerTextField()
                }
            },
        )
        Spacer(Modifier.width(8.dp))
        Box(
            modifier =
                Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .clickable(onClick = onClearFocusClick),
        )
    }
}

@Composable
private fun AmountKeypad(
    canSave: Boolean,
    onDigit: (Char) -> Unit,
    onBackspace: () -> Unit,
    onSave: () -> Unit,
) {
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(232.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        KeypadRow(
            modifier = Modifier.weight(1f),
            cells =
                listOf(
                    KeypadCell.Digit('1'),
                    KeypadCell.Digit('2'),
                    KeypadCell.Digit('3'),
                    KeypadCell.Backspace,
                ),
            canSave = canSave,
            onDigit = onDigit,
            onBackspace = onBackspace,
            onSave = onSave,
        )
        KeypadRow(
            modifier = Modifier.weight(1f),
            cells =
                listOf(
                    KeypadCell.Digit('4'),
                    KeypadCell.Digit('5'),
                    KeypadCell.Digit('6'),
                    KeypadCell.Empty,
                ),
            canSave = canSave,
            onDigit = onDigit,
            onBackspace = onBackspace,
            onSave = onSave,
        )
        KeypadRow(
            modifier = Modifier.weight(1f),
            cells =
                listOf(
                    KeypadCell.Digit('7'),
                    KeypadCell.Digit('8'),
                    KeypadCell.Digit('9'),
                    KeypadCell.Empty,
                ),
            canSave = canSave,
            onDigit = onDigit,
            onBackspace = onBackspace,
            onSave = onSave,
        )
        KeypadRow(
            modifier = Modifier.weight(1f),
            cells =
                listOf(
                    KeypadCell.Digit('0'),
                    KeypadCell.Digit('.'),
                    KeypadCell.Empty,
                    KeypadCell.Save,
                ),
            canSave = canSave,
            onDigit = onDigit,
            onBackspace = onBackspace,
            onSave = onSave,
        )
    }
}

private sealed class KeypadCell {
    data class Digit(val c: Char) : KeypadCell()

    data object Backspace : KeypadCell()

    data object Empty : KeypadCell()

    data object Save : KeypadCell()
}

@Composable
private fun KeypadRow(
    modifier: Modifier = Modifier,
    cells: List<KeypadCell>,
    canSave: Boolean,
    onDigit: (Char) -> Unit,
    onBackspace: () -> Unit,
    onSave: () -> Unit,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .fillMaxHeight(),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        cells.forEach { cell ->
            KeypadCellContent(
                cell = cell,
                canSave = canSave,
                modifier = Modifier.weight(1f),
                onDigit = onDigit,
                onBackspace = onBackspace,
                onSave = onSave,
            )
        }
    }
}

@Composable
private fun KeypadCellContent(
    cell: KeypadCell,
    canSave: Boolean,
    onDigit: (Char) -> Unit,
    onBackspace: () -> Unit,
    onSave: () -> Unit,
    modifier: Modifier = Modifier,
) {
    when (cell) {
        KeypadCell.Empty ->
            Box(modifier.fillMaxHeight())
        KeypadCell.Backspace ->
            Box(
                modifier =
                    modifier
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .clickable(onClick = onBackspace),
                contentAlignment = Alignment.Center,
            ) {
                Text("退格", style = MaterialTheme.typography.titleSmall)
            }
        KeypadCell.Save ->
            Box(
                modifier =
                    modifier
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            if (canSave) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.surfaceVariant
                            },
                        )
                        .clickable(enabled = canSave, onClick = onSave),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    "保存",
                    style = MaterialTheme.typography.titleSmall,
                    color =
                        if (canSave) {
                            MaterialTheme.colorScheme.onPrimary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        },
                )
            }
        is KeypadCell.Digit ->
            Box(
                modifier =
                    modifier
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .clickable { onDigit(cell.c) },
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = cell.c.toString(),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
    }
}

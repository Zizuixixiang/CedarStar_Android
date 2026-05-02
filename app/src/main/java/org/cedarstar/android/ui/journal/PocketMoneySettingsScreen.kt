@file:OptIn(ExperimentalMaterial3Api::class)

package org.cedarstar.android.ui.journal

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import java.util.Locale
import org.cedarstar.android.data.model.PocketMoneyConfig

private fun formatMoney(value: Double): String =
    String.format(Locale.getDefault(), "%.2f", value)

private fun formatPercentDisplay(rate: Double): String =
    String.format(Locale.getDefault(), "%.2f", rate * 100)

@Composable
fun PocketMoneySettingsScreen(
    onBack: () -> Unit,
    viewModel: JournalViewModel,
) {
    val pocketMoney by viewModel.pocketMoneyState.collectAsStateWithLifecycle()
    val config = pocketMoney.config

    var showAllowanceDialog by remember { mutableStateOf(false) }
    var showRateDialog by remember { mutableStateOf(false) }
    var allowanceDraft by remember { mutableStateOf("") }
    var ratePercentDraft by remember { mutableStateOf("") }

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
    ) {
        SettingsTopBar(onBack = onBack)
        Spacer(Modifier.height(8.dp))
        SettingMoneyCard(
            title = "每月零花钱",
            subtitle = "每月1号自动入账",
            valueText = formatMoney(config.monthlyAllowance),
            onClick = {
                allowanceDraft = formatMoney(config.monthlyAllowance)
                showAllowanceDialog = true
            },
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        )
        SettingMoneyCard(
            title = "年利率",
            subtitle = "每天自动计息入账",
            valueText = "${formatPercentDisplay(config.annualInterestRate)}%",
            onClick = {
                ratePercentDraft = formatPercentDisplay(config.annualInterestRate)
                showRateDialog = true
            },
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        )
        Text(
            text = "调整下月生效，本月不受影响",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp),
        )
    }

    if (showAllowanceDialog) {
        AlertDialog(
            onDismissRequest = { showAllowanceDialog = false },
            title = { Text("每月零花钱") },
            text = {
                OutlinedTextField(
                    value = allowanceDraft,
                    onValueChange = { allowanceDraft = it },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val v = allowanceDraft.toDoubleOrNull()
                        if (v != null && v >= 0) {
                            viewModel.updateConfig(
                                PocketMoneyConfig(
                                    monthlyAllowance = v,
                                    annualInterestRate = config.annualInterestRate,
                                ),
                            )
                            showAllowanceDialog = false
                        }
                    },
                ) {
                    Text("确定")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAllowanceDialog = false }) {
                    Text("取消")
                }
            },
        )
    }

    if (showRateDialog) {
        AlertDialog(
            onDismissRequest = { showRateDialog = false },
            title = { Text("年利率") },
            text = {
                Column {
                    OutlinedTextField(
                        value = ratePercentDraft,
                        onValueChange = { ratePercentDraft = it },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        supportingText = {
                            Text("输入数字表示百分比，例如 1.5 表示 1.5%")
                        },
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val percent = ratePercentDraft.toDoubleOrNull()
                        if (percent != null && percent >= 0) {
                            val rate = percent / 100.0
                            viewModel.updateConfig(
                                PocketMoneyConfig(
                                    monthlyAllowance = config.monthlyAllowance,
                                    annualInterestRate = rate,
                                ),
                            )
                            showRateDialog = false
                        }
                    },
                ) {
                    Text("确定")
                }
            },
            dismissButton = {
                TextButton(onClick = { showRateDialog = false }) {
                    Text("取消")
                }
            },
        )
    }
}

@Composable
private fun SettingsTopBar(onBack: () -> Unit) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onBack) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "返回",
            )
        }
        Text(
            text = "零花钱设置",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Composable
private fun SettingMoneyCard(
    title: String,
    subtitle: String,
    valueText: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        onClick = onClick,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Text(
                text = valueText,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
            )
        }
    }
}

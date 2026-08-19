package com.kgpay.app.ui.send

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.kgpay.app.data.repository.PayeeRepository
import com.kgpay.app.domain.model.TransactionCategory
import com.kgpay.app.ui.components.FavoriteChip
import com.kgpay.app.ui.components.PrimaryButton
import com.kgpay.app.ui.theme.Strings

@Composable
fun SendMoneyScreen(
    strings: Strings,
    viewModel: PaymentViewModel,
    payeeRepository: PayeeRepository,
    prefillUpiId: String?,
    onScanQr: () -> Unit,
    onReview: () -> Unit,
) {
    LaunchedEffect(prefillUpiId) {
        if (!prefillUpiId.isNullOrBlank()) viewModel.payeeUpiId = prefillUpiId
    }
    val payees by payeeRepository.observeAll().collectAsState(initial = emptyList())
    var categoryExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(strings.payTo, style = MaterialTheme.typography.titleMedium)
        Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
            OutlinedTextField(
                value = viewModel.payeeUpiId,
                onValueChange = { viewModel.payeeUpiId = it },
                label = { Text(strings.enterUpiId) },
                singleLine = true,
                modifier = Modifier.weight(1f),
            )
            IconButton(onClick = onScanQr) {
                Icon(Icons.Default.QrCodeScanner, contentDescription = strings.scanQr)
            }
        }

        if (payees.isNotEmpty()) {
            Text(strings.orChooseFavorite, style = MaterialTheme.typography.bodyMedium)
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
            ) {
                payees.forEach { p ->
                    FavoriteChip(p.nickname) {
                        viewModel.payeeUpiId = p.upiId
                        viewModel.payeeName = p.nickname
                    }
                }
            }
        }

        OutlinedTextField(
            value = viewModel.amount,
            onValueChange = { viewModel.amount = it.filter { c -> c.isDigit() || c == '.' } },
            label = { Text(strings.amount) },
            leadingIcon = { Text("₹", style = MaterialTheme.typography.titleMedium) },
            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )

        OutlinedTextField(
            value = viewModel.remarks,
            onValueChange = { viewModel.remarks = it },
            label = { Text(strings.remarksOptional) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )

        ExposedDropdownMenuBox(expanded = categoryExpanded, onExpandedChange = { categoryExpanded = it }) {
            OutlinedTextField(
                value = viewModel.category.name.lowercase().replaceFirstChar { it.uppercase() },
                onValueChange = {},
                readOnly = true,
                label = { Text("Category") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                modifier = Modifier.fillMaxWidth().menuAnchor(),
            )
            DropdownMenu(expanded = categoryExpanded, onDismissRequest = { categoryExpanded = false }) {
                TransactionCategory.entries.forEach { cat ->
                    DropdownMenuItem(
                        text = { Text(cat.name.lowercase().replaceFirstChar { it.uppercase() }) },
                        onClick = { viewModel.category = cat; categoryExpanded = false },
                    )
                }
            }
        }

        PrimaryButton(
            strings.reviewAndConfirm,
            enabled = viewModel.payeeUpiId.isNotBlank() && (viewModel.amount.toDoubleOrNull() ?: 0.0) > 0.0,
            onClick = {
                viewModel.purpose = PaymentPurpose.SEND_MONEY
                if (viewModel.payeeName.isBlank()) viewModel.payeeName = viewModel.payeeUpiId
                onReview()
            },
        )
    }
}

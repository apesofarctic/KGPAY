package com.kgpay.app.ui.history

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kgpay.app.data.repository.TransactionRepository
import com.kgpay.app.data.ussd.Codes
import com.kgpay.app.data.ussd.UssdHistoryLine
import com.kgpay.app.data.ussd.UssdHistoryParser
import com.kgpay.app.data.ussd.UssdLauncher
import com.kgpay.app.data.ussd.UssdResult
import com.kgpay.app.domain.model.TransactionStatus
import com.kgpay.app.ui.components.EmptyState
import com.kgpay.app.ui.home.TransactionRow
import com.kgpay.app.ui.theme.Strings

private enum class Filter { ALL, SUCCESS, FAILED }

@Composable
fun TransactionHistoryScreen(strings: Strings, transactionRepository: TransactionRepository, simSlot: Int) {
    val transactions by transactionRepository.observeAll().collectAsState(initial = emptyList())
    var query by remember { mutableStateOf("") }
    var filter by remember { mutableStateOf(Filter.ALL) }
    var syncedLines by remember { mutableStateOf<List<UssdHistoryLine>?>(null) }
    var syncing by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val filtered = transactions
        .filter { filter == Filter.ALL || (filter == Filter.SUCCESS) == (it.status == TransactionStatus.SUCCESS) }
        .filter { query.isBlank() || it.payeeName.contains(query, ignoreCase = true) || it.payeeUpiId.contains(query, ignoreCase = true) }

    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp, vertical = 12.dp)) {
        Text(
            strings.transactionHistory,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 12.dp),
        )
        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            label = { Text(strings.searchTransactions) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )
        Row(modifier = Modifier.padding(vertical = 12.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(selected = filter == Filter.ALL, onClick = { filter = Filter.ALL }, label = { Text(strings.filterAll) })
            FilterChip(selected = filter == Filter.SUCCESS, onClick = { filter = Filter.SUCCESS }, label = { Text(strings.filterSuccess) })
            FilterChip(selected = filter == Filter.FAILED, onClick = { filter = Filter.FAILED }, label = { Text(strings.filterFailed) })
        }
        Row(modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(
                strings.fromUssd,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
            )
            if (syncing) {
                CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
            } else {
                Text(
                    "Sync from bank",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clickable {
                        val activity = context as? android.app.Activity
                        if (activity != null) {
                            syncing = true
                            UssdLauncher.dial(
                                activity,
                                UssdLauncher.Request(ussdCode = "*99*6*1#", simSlot = simSlot, serviceCode = Codes.SERVICE_TRANSACTIONS_HISTORY),
                            ) { result ->
                                syncing = false
                                UssdLauncher.stop(activity)
                                if (result is UssdResult.TransactionHistory) {
                                    syncedLines = UssdHistoryParser.parse(result.rawStatus)
                                }
                            }
                        }
                    },
                )
            }
        }

        if (syncedLines != null && syncedLines!!.isNotEmpty()) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.padding(bottom = 12.dp)) {
                syncedLines!!.forEach { line ->
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column {
                            Text(line.receiver, style = MaterialTheme.typography.bodyMedium)
                            Text("${line.date}, ${line.time}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                        }
                        Text(line.amount, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                    }
                }
            }
        }

        if (filtered.isEmpty()) {
            EmptyState(strings.noTransactionsYet)
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(filtered, key = { it.id }) { TransactionRow(it) }
            }
        }
    }
}

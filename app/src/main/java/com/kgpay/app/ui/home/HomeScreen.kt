package com.kgpay.app.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kgpay.app.data.repository.PayeeRepository
import com.kgpay.app.data.repository.TransactionRepository
import com.kgpay.app.data.ussd.NetworkQuality
import com.kgpay.app.data.ussd.NetworkQualityChecker
import com.kgpay.app.domain.model.Transaction
import com.kgpay.app.domain.model.TransactionStatus
import com.kgpay.app.ui.components.EmptyState
import com.kgpay.app.ui.components.FavoriteChip
import com.kgpay.app.ui.components.NetworkBanner
import com.kgpay.app.ui.components.QuickActionTile
import com.kgpay.app.ui.components.SectionHeader
import com.kgpay.app.ui.theme.KGPayError
import com.kgpay.app.ui.theme.KGPaySuccess
import com.kgpay.app.ui.theme.Strings
import com.kgpay.app.util.formatDateTime
import com.kgpay.app.util.formatInr
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    strings: Strings,
    transactionRepository: TransactionRepository,
    payeeRepository: PayeeRepository,
    onSend: () -> Unit,
    onScanAndPay: () -> Unit,
    onCheckBalance: () -> Unit,
    onHistory: () -> Unit,
    onPayFavorite: (upiId: String, name: String) -> Unit,
) {
    val transactions by transactionRepository.observeAll().collectAsState(initial = emptyList())
    val payees by payeeRepository.observeAll().collectAsState(initial = emptyList())
    val favorites = payees.filter { it.isFavorite }

    var quality by remember { mutableStateOf<NetworkQuality?>(null) }
    val scope = rememberCoroutineScope()
    LaunchedEffect(Unit) {
        scope.launch { quality = NetworkQualityChecker().check() }
    }

    Column(
        modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState()).padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        Text(strings.homeGreeting, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)

        quality?.let {
            if (it != NetworkQuality.STABLE) {
                NetworkBanner(strings.networkPoorBanner, poor = true)
            }
        }

        // Quick actions grid
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            SectionHeader(strings.quickActions)
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                QuickActionTile(strings.send, { Icon(Icons.Default.Send, null, tint = MaterialTheme.colorScheme.primary) }, Modifier.weight(1f), onSend)
                QuickActionTile(strings.scanAndPay, { Icon(Icons.Default.QrCodeScanner, null, tint = MaterialTheme.colorScheme.primary) }, Modifier.weight(1f), onScanAndPay)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                QuickActionTile(strings.checkBalance, { Icon(Icons.Default.AccountBalanceWallet, null, tint = MaterialTheme.colorScheme.primary) }, Modifier.weight(1f), onCheckBalance)
                QuickActionTile(strings.history, { Icon(Icons.Default.Receipt, null, tint = MaterialTheme.colorScheme.primary) }, Modifier.weight(1f), onHistory)
            }
        }

        if (favorites.isNotEmpty()) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                SectionHeader(strings.favorites)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                ) {
                    favorites.forEach { p ->
                        FavoriteChip(p.nickname) { onPayFavorite(p.upiId, p.nickname) }
                    }
                }
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            SectionHeader(strings.recentActivity, strings.seeAll, onHistory)
            if (transactions.isEmpty()) {
                EmptyState(strings.noTransactionsYet)
            } else {
                transactions.take(4).forEach { TransactionRow(it) }
            }
        }
    }
}

@Composable
fun TransactionRow(transaction: Transaction) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp))
            .padding(14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Column {
            Text(transaction.payeeName, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
            Text(
                formatDateTime(transaction.timestampMillis),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            )
        }
        Text(
            (if (transaction.status == TransactionStatus.SUCCESS) "- " else "") + formatInr(transaction.amount),
            color = if (transaction.status == TransactionStatus.SUCCESS) KGPaySuccess else KGPayError,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

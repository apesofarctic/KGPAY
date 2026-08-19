package com.kgpay.app.ui.insights

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kgpay.app.data.repository.TransactionRepository
import com.kgpay.app.domain.model.Transaction
import com.kgpay.app.domain.model.TransactionCategory
import com.kgpay.app.domain.model.TransactionStatus
import com.kgpay.app.ui.components.EmptyState
import com.kgpay.app.ui.components.SectionHeader
import com.kgpay.app.ui.theme.Strings
import com.kgpay.app.util.formatInr
import java.util.Calendar

private val CategoryColors = mapOf(
    TransactionCategory.FOOD to Color(0xFF66BB6A),
    TransactionCategory.TRAVEL to Color(0xFF42A5F5),
    TransactionCategory.SHOPPING to Color(0xFFFFA726),
    TransactionCategory.BILLS to Color(0xFFAB47BC),
    TransactionCategory.RENT to Color(0xFFEF5350),
    TransactionCategory.ENTERTAINMENT to Color(0xFF26C6DA),
    TransactionCategory.OTHER to Color(0xFF9E9E9E),
)

@Composable
fun InsightsScreen(strings: Strings, transactionRepository: TransactionRepository) {
    val transactions by transactionRepository.observeAll().collectAsState(initial = emptyList())
    val monthStart = rememberMonthStart()
    val thisMonth = transactions.filter { it.status == TransactionStatus.SUCCESS && it.timestampMillis >= monthStart }

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        Text(strings.spendInsights, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)

        if (thisMonth.isEmpty()) {
            EmptyState(strings.noTransactionsYet)
            return@Column
        }

        Column(
            modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surface, RoundedCornerShape(16.dp)).padding(20.dp),
        ) {
            Text(strings.totalSpend, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
            Text(formatInr(thisMonth.sumOf { it.amount }), style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Text(strings.thisMonth, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
        }

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            SectionHeader(strings.byCategory)
            val byCategory = thisMonth.groupBy { it.category }.mapValues { it.value.sumOf { t -> t.amount } }
            val total = byCategory.values.sum().takeIf { it > 0 } ?: 1.0
            Row(
                modifier = Modifier.fillMaxWidth().height(14.dp).clipRounded(),
            ) {
                byCategory.forEach { (cat, amt) ->
                    Row(modifier = Modifier.weight((amt / total).toFloat().coerceAtLeast(0.001f)).fillMaxSize().background(CategoryColors[cat] ?: Color.Gray)) {}
                }
            }
            byCategory.entries.sortedByDescending { it.value }.forEach { (cat, amt) ->
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        androidx.compose.foundation.layout.Box(
                            modifier = Modifier.size(10.dp).background(CategoryColors[cat] ?: Color.Gray, CircleShape),
                        )
                        Text(cat.name.lowercase().replaceFirstChar { it.uppercase() })
                    }
                    Text(formatInr(amt), fontWeight = FontWeight.Medium)
                }
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            SectionHeader(strings.topPayees)
            thisMonth.groupBy { it.payeeName }
                .mapValues { it.value.sumOf { t -> t.amount } }
                .entries.sortedByDescending { it.value }
                .take(5)
                .forEach { (name, amt) ->
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(name)
                        Text(formatInr(amt), fontWeight = FontWeight.Medium)
                    }
                }
        }
    }
}

@Composable
private fun rememberMonthStart(): Long = androidx.compose.runtime.remember {
    Calendar.getInstance().apply {
        set(Calendar.DAY_OF_MONTH, 1)
        set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
    }.timeInMillis
}

private fun Modifier.clipRounded(): Modifier = this.background(Color.Transparent, RoundedCornerShape(8.dp))

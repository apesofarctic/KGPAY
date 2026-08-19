package com.kgpay.app.ui.send

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kgpay.app.ui.components.PrimaryButton
import com.kgpay.app.ui.components.SecondaryButton
import com.kgpay.app.ui.theme.KGPayError
import com.kgpay.app.ui.theme.KGPaySuccess
import com.kgpay.app.ui.theme.Strings
import com.kgpay.app.util.formatInr

@Composable
fun PaymentResultScreen(
    strings: Strings,
    viewModel: PaymentViewModel,
    onSaveFavorite: () -> Unit,
    onBackHome: () -> Unit,
    onTryAgain: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        when (val state = viewModel.uiState) {
            PaymentUiState.InFlight -> {
                CircularProgressIndicator()
                Text("Dialling *99#…", modifier = Modifier.padding(top = 16.dp))
            }
            is PaymentUiState.Success -> {
                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = KGPaySuccess, modifier = Modifier.size(64.dp))
                Text(strings.paymentSuccessful, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 16.dp))
                Text(formatInr(viewModel.amount), style = MaterialTheme.typography.headlineMedium, color = KGPaySuccess)
                Text("${strings.paidTo} ${viewModel.payeeName}", modifier = Modifier.padding(top = 4.dp))
                state.referenceId?.let {
                    Text("${strings.referenceId}: $it", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f), modifier = Modifier.padding(top = 4.dp))
                }
                Column(modifier = Modifier.padding(top = 28.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    SecondaryButton(strings.saveAsFavorite, onClick = onSaveFavorite)
                    PrimaryButton(strings.backToHome, onClick = onBackHome)
                }
            }
            is PaymentUiState.BalanceKnown -> {
                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = KGPaySuccess, modifier = Modifier.size(64.dp))
                Text(strings.yourBalance, style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 16.dp))
                Text(formatInr(state.amountText), style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                PrimaryButton(strings.backToHome, modifier = Modifier.padding(top = 28.dp), onClick = onBackHome)
            }
            is PaymentUiState.Failed -> {
                Icon(Icons.Default.Error, contentDescription = null, tint = KGPayError, modifier = Modifier.size(64.dp))
                Text(strings.paymentFailed, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 16.dp))
                Text(
                    when (state.reasonKey) {
                        "wrong_pin" -> strings.wrongPin
                        "connection_error" -> strings.connectionError
                        else -> strings.paymentFailed
                    },
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    modifier = Modifier.padding(top = 4.dp),
                )
                Column(modifier = Modifier.padding(top = 28.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    PrimaryButton(strings.tryAgain, onClick = onTryAgain)
                    SecondaryButton(strings.backToHome, onClick = onBackHome)
                }
            }
            PaymentUiState.Idle -> {
                CircularProgressIndicator()
            }
        }
    }
}

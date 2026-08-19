package com.kgpay.app.ui.send

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kgpay.app.ui.components.PrimaryButton
import com.kgpay.app.ui.components.SecondaryButton
import com.kgpay.app.ui.theme.Strings
import com.kgpay.app.util.formatInr

/**
 * The explicit human-in-the-loop step before any USSD automation starts: the user sees exactly
 * who and how much, and must tap Confirm & Send. Nothing is dialed until they do.
 */
@Composable
fun ConfirmSendScreen(strings: Strings, viewModel: PaymentViewModel, onEditDetails: () -> Unit, onConfirmed: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(20.dp),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text(strings.confirmTitle, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Text(strings.confirmBody, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))

            Column(
                modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surface, RoundedCornerShape(16.dp)).padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                SummaryRow(strings.payTo, viewModel.payeeName)
                SummaryRow("UPI ID", viewModel.payeeUpiId)
                SummaryRow(strings.amount, formatInr(viewModel.amount))
                if (viewModel.remarks.isNotBlank()) SummaryRow(strings.remarksOptional, viewModel.remarks)
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            PrimaryButton(strings.confirmAndSend, onClick = onConfirmed)
            SecondaryButton(strings.editDetails, onClick = onEditDetails)
        }
    }
}

@Composable
private fun SummaryRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
        Text(value, fontWeight = FontWeight.Medium)
    }
}

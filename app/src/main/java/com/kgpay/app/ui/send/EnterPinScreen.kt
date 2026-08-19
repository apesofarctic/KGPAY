package com.kgpay.app.ui.send

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.kgpay.app.ui.components.PrimaryButton
import com.kgpay.app.ui.theme.Strings

@Composable
fun EnterPinScreen(strings: Strings, viewModel: PaymentViewModel, simSlot: Int, onSubmitted: () -> Unit) {
    val context = LocalContext.current

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(Icons.Default.Lock, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        Text(
            strings.enterUpiPin,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 12.dp, bottom = 4.dp),
        )
        Text(strings.pinNeverStored, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f), modifier = Modifier.padding(bottom = 20.dp))

        OutlinedTextField(
            value = viewModel.pin,
            onValueChange = { if (it.length <= 6) viewModel.setPin(it.filter { c -> c.isDigit() }) },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
            singleLine = true,
            modifier = Modifier.padding(bottom = 24.dp),
        )

        PrimaryButton(
            strings.submit,
            enabled = viewModel.pin.length in 4..6,
            onClick = {
                val activity = context as? android.app.Activity
                if (activity != null) {
                    viewModel.submit(activity, simSlot)
                    onSubmitted()
                }
            },
        )
    }
}

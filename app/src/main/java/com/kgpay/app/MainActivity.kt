package com.kgpay.app

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.fragment.app.FragmentActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.kgpay.app.data.repository.AppSettings
import com.kgpay.app.ui.LocalAppContainer
import com.kgpay.app.ui.LocalAppSettings
import com.kgpay.app.ui.LocalStrings
import com.kgpay.app.ui.components.PrimaryButton
import com.kgpay.app.ui.navigation.KGPayNavHost
import com.kgpay.app.ui.theme.KGPayTheme
import com.kgpay.app.ui.theme.stringsFor

class MainActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val app = application as KGPayApp

        // upi://pay?pa=<upi-id>&am=<amount>&tn=<note> deep link (e.g. from a QR-code sharing app).
        val deepLinkUpiId = intent?.data?.takeIf { it.scheme == "upi" }?.getQueryParameter("pa")

        setContent {
            val settings by app.settingsRepository.settings.collectAsState(initial = AppSettings())
            val strings = stringsFor(settings.language)

            KGPayTheme(darkTheme = settings.darkMode) {
                CompositionLocalProvider(
                    LocalAppContainer provides app,
                    LocalStrings provides strings,
                    LocalAppSettings provides settings,
                ) {
                    Surface(color = MaterialTheme.colorScheme.background) {
                        var unlocked by remember { mutableStateOf(!settings.appLockEnabled) }
                        if (unlocked) {
                            KGPayNavHost(app = app, settings = settings, strings = strings, deepLinkUpiId = deepLinkUpiId)
                        } else {
                            AppLockGate(onUnlock = {
                                val canAuth = BiometricManager.from(this@MainActivity)
                                    .canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK or BiometricManager.Authenticators.DEVICE_CREDENTIAL)
                                if (canAuth != BiometricManager.BIOMETRIC_SUCCESS) {
                                    unlocked = true // no lock configured on this device — don't block access
                                } else {
                                    val prompt = BiometricPrompt(
                                        this@MainActivity,
                                        ContextCompat.getMainExecutor(this@MainActivity),
                                        object : BiometricPrompt.AuthenticationCallback() {
                                            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                                                unlocked = true
                                            }
                                        },
                                    )
                                    val promptInfo = BiometricPrompt.PromptInfo.Builder()
                                        .setTitle(strings.appLock)
                                        .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_WEAK or BiometricManager.Authenticators.DEVICE_CREDENTIAL)
                                        .build()
                                    prompt.authenticate(promptInfo)
                                }
                            })
                        }
                    }
                }
            }
        }
    }
}

@androidx.compose.runtime.Composable
private fun AppLockGate(onUnlock: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.padding(bottom = 16.dp))
        Text("KGPay is locked", style = MaterialTheme.typography.titleLarge)
        PrimaryButton("Unlock", modifier = Modifier.padding(top = 20.dp).fillMaxWidth(), onClick = onUnlock)
    }
}

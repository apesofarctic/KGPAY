package com.kgpay.app.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kgpay.app.data.repository.AppLanguage
import com.kgpay.app.data.repository.AppSettings
import com.kgpay.app.data.repository.SettingsRepository
import com.kgpay.app.ui.components.SectionHeader
import com.kgpay.app.ui.theme.Strings
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(strings: Strings, settings: AppSettings, repo: SettingsRepository) {
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        Text(strings.settings, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)

        SettingsCard {
            SectionHeader(strings.language)
            Row(modifier = Modifier.fillMaxWidth().padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                LanguageOption("English", settings.language == AppLanguage.ENGLISH) {
                    scope.launch { repo.setLanguage(AppLanguage.ENGLISH) }
                }
                LanguageOption("हिन्दी", settings.language == AppLanguage.HINDI) {
                    scope.launch { repo.setLanguage(AppLanguage.HINDI) }
                }
            }
        }

        SettingsCard {
            ToggleRow(strings.darkMode, settings.darkMode) { checked -> scope.launch { repo.setDarkMode(checked) } }
        }

        SettingsCard {
            ToggleRow(strings.appLock, settings.appLockEnabled) { checked -> scope.launch { repo.setAppLockEnabled(checked) } }
            Text(
                strings.appLockBody,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                modifier = Modifier.padding(top = 4.dp),
            )
        }

        SettingsCard {
            ToggleRow(strings.lowBalanceAlerts, settings.lowBalanceAlerts) { checked -> scope.launch { repo.setLowBalanceAlerts(checked) } }
        }

        SettingsCard {
            SectionHeader(strings.simCard)
            Row(modifier = Modifier.fillMaxWidth().padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                LanguageOption(strings.sim1, settings.simSlot == 0) { scope.launch { repo.setSimSlot(0) } }
                LanguageOption(strings.sim2, settings.simSlot == 1) { scope.launch { repo.setSimSlot(1) } }
            }
        }

        SettingsCard {
            Text(strings.helpAndSupport, fontWeight = FontWeight.Medium)
            Text(
                "${strings.about} · ${strings.version} 2.0",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                modifier = Modifier.padding(top = 6.dp),
            )
        }
    }
}

@Composable
private fun SettingsCard(content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surface, RoundedCornerShape(16.dp)).padding(16.dp),
        content = content,
    )
}

@Composable
private fun ToggleRow(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text(label, style = MaterialTheme.typography.bodyLarge)
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Composable
private fun LanguageOption(label: String, selected: Boolean, onClick: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        RadioButton(selected = selected, onClick = onClick)
        Text(label)
    }
}

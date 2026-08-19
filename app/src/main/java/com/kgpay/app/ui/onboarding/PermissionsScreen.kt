package com.kgpay.app.ui.onboarding

import android.content.Intent
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kgpay.app.data.ussd.PermissionsHelper
import com.kgpay.app.ui.components.PrimaryButton
import com.kgpay.app.ui.theme.Strings

@Composable
fun PermissionsScreen(strings: Strings, onContinue: () -> Unit) {
    val context = LocalContext.current
    var runtimeGranted by remember { mutableStateOf(PermissionsHelper.runtimePermissionsGranted(context)) }
    var accessibilityOn by remember { mutableStateOf(PermissionsHelper.isAccessibilityServiceEnabled(context)) }

    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
        runtimeGranted = PermissionsHelper.runtimePermissionsGranted(context)
    }

    // Re-check accessibility status whenever the user returns to this screen (e.g. from Settings).
    LaunchedEffect(Unit) {
        accessibilityOn = PermissionsHelper.isAccessibilityServiceEnabled(context)
    }

    Column(
        modifier = Modifier.fillMaxWidth().padding(24.dp),
        verticalArrangement = Arrangement.Center,
    ) {
        Text(strings.grantPermissions, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Text(
            strings.permissionsBody,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f),
            modifier = Modifier.padding(top = 8.dp, bottom = 24.dp),
        )

        PermissionRow(strings.allowRuntimePerms, runtimeGranted) {
            permissionLauncher.launch(PermissionsHelper.RUNTIME_PERMISSIONS)
        }
        PermissionRow(strings.enableAccessibility, accessibilityOn) {
            context.startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
        }
        Text(
            strings.accessibilityBody,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            modifier = Modifier.padding(top = 4.dp, bottom = 24.dp),
        )

        PrimaryButton(strings.continueLabel, enabled = runtimeGranted && accessibilityOn, onClick = onContinue)
    }
}

@Composable
private fun PermissionRow(label: String, granted: Boolean, onGrantClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            if (granted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
            contentDescription = null,
            tint = if (granted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
            modifier = Modifier.size(22.dp),
        )
        Text(label, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(start = 12.dp).weight(1f))
        if (!granted) {
            Text(
                "Enable",
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(start = 8.dp).clickable(onClick = onGrantClick),
            )
        }
    }
}

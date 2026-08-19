package com.kgpay.app.ui.contacts

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.kgpay.app.data.repository.PayeeRepository
import com.kgpay.app.domain.model.Payee
import com.kgpay.app.ui.components.EmptyState
import com.kgpay.app.ui.components.PrimaryButton
import com.kgpay.app.ui.theme.Strings
import kotlinx.coroutines.launch

@Composable
fun ContactsScreen(strings: Strings, payeeRepository: PayeeRepository) {
    val payees by payeeRepository.observeAll().collectAsState(initial = emptyList())
    var showAddDialog by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) { Icon(Icons.Default.Add, contentDescription = strings.addContact) }
        },
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(20.dp)) {
            Text(strings.contacts, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 16.dp))
            if (payees.isEmpty()) {
                EmptyState(strings.noTransactionsYet)
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(payees, key = { it.id }) { payee ->
                        ContactRow(payee) { scope.launch { payeeRepository.toggleFavorite(payee) } }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddContactDialog(
            strings = strings,
            onDismiss = { showAddDialog = false },
            onSave = { nickname, upiId ->
                scope.launch { payeeRepository.addPayee(nickname, upiId) }
                showAddDialog = false
            },
        )
    }
}

@Composable
private fun ContactRow(payee: Payee, onToggleFavorite: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp)).padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(
                modifier = Modifier.size(36.dp).background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Text(payee.nickname.take(1).uppercase(), color = MaterialTheme.colorScheme.onPrimaryContainer, fontWeight = FontWeight.Bold)
            }
            Column {
                Text(payee.nickname, fontWeight = FontWeight.Medium)
                Text(payee.upiId, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
            }
        }
        IconButton(onClick = onToggleFavorite) {
            Icon(
                if (payee.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                contentDescription = null,
                tint = if (payee.isFavorite) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
            )
        }
    }
}

@Composable
private fun AddContactDialog(strings: Strings, onDismiss: () -> Unit, onSave: (String, String) -> Unit) {
    var nickname by remember { mutableStateOf("") }
    var upiId by remember { mutableStateOf("") }
    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier.background(MaterialTheme.colorScheme.surface, RoundedCornerShape(16.dp)).padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(strings.addContact, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            OutlinedTextField(value = nickname, onValueChange = { nickname = it }, label = { Text(strings.nickname) }, singleLine = true)
            OutlinedTextField(value = upiId, onValueChange = { upiId = it }, label = { Text(strings.enterUpiId) }, singleLine = true)
            PrimaryButton(strings.save, enabled = nickname.isNotBlank() && upiId.isNotBlank()) { onSave(nickname, upiId) }
        }
    }
}

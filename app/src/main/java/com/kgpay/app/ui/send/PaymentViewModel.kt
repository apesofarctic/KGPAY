package com.kgpay.app.ui.send

import android.app.Activity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.kgpay.app.KGPayApp
import com.kgpay.app.data.repository.PayeeRepository
import com.kgpay.app.data.repository.TransactionRepository
import com.kgpay.app.data.ussd.Codes
import com.kgpay.app.data.ussd.UssdLauncher
import com.kgpay.app.data.ussd.UssdResult
import com.kgpay.app.domain.model.Transaction
import com.kgpay.app.domain.model.TransactionCategory
import com.kgpay.app.domain.model.TransactionStatus
import kotlinx.coroutines.launch

enum class PaymentPurpose { SEND_MONEY, CHECK_BALANCE }

sealed class PaymentUiState {
    object Idle : PaymentUiState()
    object InFlight : PaymentUiState()
    data class Success(val referenceId: String?) : PaymentUiState()
    data class BalanceKnown(val amountText: String) : PaymentUiState()
    data class Failed(val reasonKey: String) : PaymentUiState()
}

/**
 * Shared across the Send-money / Check-balance / Enter-PIN / Result screens so the in-flight
 * payment details survive navigation without ever touching Room or DataStore. [pin] is cleared
 * the moment the USSD session reports back, whichever way it goes.
 */
class PaymentViewModel(
    private val transactionRepository: TransactionRepository,
    private val payeeRepository: PayeeRepository,
) : ViewModel() {

    var purpose by mutableStateOf(PaymentPurpose.SEND_MONEY)
    var payeeUpiId by mutableStateOf("")
    var payeeName by mutableStateOf("")
    var amount by mutableStateOf("")
    var remarks by mutableStateOf("")
    var category by mutableStateOf(TransactionCategory.OTHER)
    var pin by mutableStateOf("")
        private set
    var uiState: PaymentUiState by mutableStateOf(PaymentUiState.Idle)
        private set

    fun setPin(value: String) {
        pin = value
    }

    fun resetForNewPayment() {
        payeeUpiId = ""; payeeName = ""; amount = ""; remarks = ""
        category = TransactionCategory.OTHER
        pin = ""
        uiState = PaymentUiState.Idle
    }

    fun submit(activity: Activity, simSlot: Int) {
        uiState = PaymentUiState.InFlight
        val request = when (purpose) {
            PaymentPurpose.SEND_MONEY -> UssdLauncher.Request(
                ussdCode = "*99*1*3#",
                simSlot = simSlot,
                serviceCode = Codes.SERVICE_TRANSFER_UPI,
                upiPin = pin,
                upiId = payeeUpiId,
                amount = amount,
                remarks = remarks,
            )
            PaymentPurpose.CHECK_BALANCE -> UssdLauncher.Request(
                ussdCode = "*99*3#",
                simSlot = simSlot,
                serviceCode = Codes.SERVICE_CHECK_BALANCE,
                upiPin = pin,
            )
        }
        UssdLauncher.dial(activity, request) { result -> onResult(activity, result) }
    }

    private fun onResult(activity: Activity, result: UssdResult) {
        pin = "" // wipe immediately, regardless of outcome
        UssdLauncher.stop(activity)
        when (result) {
            is UssdResult.TransferSuccess -> {
                uiState = PaymentUiState.Success(parseReferenceId(result.rawStatus))
                viewModelScope.launch {
                    transactionRepository.record(
                        Transaction(
                            payeeUpiId = payeeUpiId,
                            payeeName = payeeName.ifBlank { payeeUpiId },
                            amount = amount.toDoubleOrNull() ?: 0.0,
                            remarks = remarks,
                            timestampMillis = System.currentTimeMillis(),
                            status = TransactionStatus.SUCCESS,
                            category = category,
                            referenceId = parseReferenceId(result.rawStatus),
                        )
                    )
                    payeeRepository.recordPayment(payeeUpiId, payeeName.ifBlank { payeeUpiId })
                }
            }
            is UssdResult.CheckBalanceSuccess -> {
                uiState = PaymentUiState.BalanceKnown(parseBalance(result.rawStatus))
            }
            UssdResult.InvalidPin -> {
                uiState = PaymentUiState.Failed("wrong_pin")
                recordFailureIfTransfer()
            }
            UssdResult.ConnectionError -> {
                uiState = PaymentUiState.Failed("connection_error")
                recordFailureIfTransfer()
            }
            is UssdResult.UnknownError -> uiState = PaymentUiState.Failed("unknown")
            is UssdResult.TransactionHistory -> Unit // not relevant to this ViewModel
        }
    }

    private fun recordFailureIfTransfer() {
        if (purpose != PaymentPurpose.SEND_MONEY) return
        viewModelScope.launch {
            transactionRepository.record(
                Transaction(
                    payeeUpiId = payeeUpiId,
                    payeeName = payeeName.ifBlank { payeeUpiId },
                    amount = amount.toDoubleOrNull() ?: 0.0,
                    remarks = remarks,
                    timestampMillis = System.currentTimeMillis(),
                    status = TransactionStatus.FAILED,
                    category = category,
                    referenceId = null,
                )
            )
        }
    }

    private fun parseReferenceId(status: String): String? {
        val flat = status.replace('\n', ' ')
        val idx = flat.indexOf("RefId:")
        if (idx == -1) return null
        val rest = flat.substring(idx + "RefId:".length)
        return rest.takeWhile { it != ')' }.trim().ifBlank { null }
    }

    private fun parseBalance(status: String): String {
        val idx = status.indexOf("Rs.")
        return if (idx == -1) "--" else status.substring(idx + 3).takeWhile { it.isDigit() || it == '.' }
    }

    companion object {
        fun factory(app: KGPayApp) = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T =
                PaymentViewModel(app.transactionRepository, app.payeeRepository) as T
        }
    }
}

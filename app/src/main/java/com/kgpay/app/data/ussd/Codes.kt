package com.kgpay.app.data.ussd

/**
 * Ported 1:1 from the original `CODES.java`. These identify which dialog box the bank's
 * `*99#` USSD menu is currently showing (DIALOG_*), and which high-level flow the
 * accessibility service is driving (SERVICE_*). Kept as an object rather than an enum
 * so the numeric values (which nothing else depends on) stay exactly as before.
 */
object Codes {
    const val DIALOG_ENTER_PIN = 1
    const val DIALOG_CONNECTION_ERROR = 2
    const val DIALOG_ENTER_UPI_ID = 3
    const val DIALOG_ENTER_AMOUNT = 4
    const val DIALOG_ENTER_REMARKS = 5
    const val DIALOG_SUCCESS_CHECK_BALANCE = 6
    const val DIALOG_SUCCESS_TRANSFER_UPI = 7
    const val DIALOG_TRANSACTIONS_HISTORY = 19
    const val DIALOG_INVALID_PIN_ERROR = 20

    const val SERVICE_TRANSFER_UPI = 13
    const val SERVICE_CHECK_BALANCE = 14
    const val SERVICE_TRANSACTIONS_HISTORY = 16
}

/** Outcome handed back from [UssdAccessibilityService] to whichever screen triggered it. */
sealed class UssdResult {
    data class CheckBalanceSuccess(val rawStatus: String) : UssdResult()
    data class TransferSuccess(val rawStatus: String) : UssdResult()
    data class TransactionHistory(val rawStatus: String) : UssdResult()
    object ConnectionError : UssdResult()
    object InvalidPin : UssdResult()
    data class UnknownError(val rawStatus: String) : UssdResult()
}

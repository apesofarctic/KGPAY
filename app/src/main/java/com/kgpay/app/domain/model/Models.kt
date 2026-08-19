package com.kgpay.app.domain.model

enum class TransactionStatus { SUCCESS, FAILED }

enum class TransactionCategory { FOOD, TRAVEL, SHOPPING, BILLS, RENT, ENTERTAINMENT, OTHER }

data class Transaction(
    val id: Long = 0,
    val payeeUpiId: String,
    val payeeName: String,
    val amount: Double,
    val remarks: String,
    val timestampMillis: Long,
    val status: TransactionStatus,
    val category: TransactionCategory,
    val referenceId: String? = null,
)

data class Payee(
    val id: Long = 0,
    val nickname: String,
    val upiId: String,
    val isFavorite: Boolean = false,
    val lastPaidMillis: Long = 0,
)

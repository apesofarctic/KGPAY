package com.kgpay.app.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kgpay.app.domain.model.Payee
import com.kgpay.app.domain.model.Transaction
import com.kgpay.app.domain.model.TransactionCategory
import com.kgpay.app.domain.model.TransactionStatus

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val payeeUpiId: String,
    val payeeName: String,
    val amount: Double,
    val remarks: String,
    val timestampMillis: Long,
    val status: String,
    val category: String,
    val referenceId: String?,
)

@Entity(tableName = "payees")
data class PayeeEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nickname: String,
    val upiId: String,
    val isFavorite: Boolean = false,
    val lastPaidMillis: Long = 0,
)

fun TransactionEntity.toDomain() = Transaction(
    id = id,
    payeeUpiId = payeeUpiId,
    payeeName = payeeName,
    amount = amount,
    remarks = remarks,
    timestampMillis = timestampMillis,
    status = runCatching { TransactionStatus.valueOf(status) }.getOrDefault(TransactionStatus.FAILED),
    category = runCatching { TransactionCategory.valueOf(category) }.getOrDefault(TransactionCategory.OTHER),
    referenceId = referenceId,
)

fun Transaction.toEntity() = TransactionEntity(
    id = id,
    payeeUpiId = payeeUpiId,
    payeeName = payeeName,
    amount = amount,
    remarks = remarks,
    timestampMillis = timestampMillis,
    status = status.name,
    category = category.name,
    referenceId = referenceId,
)

fun PayeeEntity.toDomain() = Payee(id, nickname, upiId, isFavorite, lastPaidMillis)

fun Payee.toEntity() = PayeeEntity(id, nickname, upiId, isFavorite, lastPaidMillis)

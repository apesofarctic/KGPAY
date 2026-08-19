package com.kgpay.app.data.repository

import com.kgpay.app.data.local.TransactionDao
import com.kgpay.app.data.local.toDomain
import com.kgpay.app.data.local.toEntity
import com.kgpay.app.domain.model.Transaction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TransactionRepository(private val dao: TransactionDao) {
    fun observeAll(): Flow<List<Transaction>> = dao.observeAll().map { list -> list.map { it.toDomain() } }

    suspend fun record(transaction: Transaction): Long = dao.insert(transaction.toEntity())
}

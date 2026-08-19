package com.kgpay.app.data.repository

import com.kgpay.app.data.local.PayeeDao
import com.kgpay.app.data.local.toDomain
import com.kgpay.app.data.local.toEntity
import com.kgpay.app.domain.model.Payee
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PayeeRepository(private val dao: PayeeDao) {
    fun observeAll(): Flow<List<Payee>> = dao.observeAll().map { list -> list.map { it.toDomain() } }

    suspend fun recordPayment(upiId: String, nickname: String) {
        val existing = dao.findByUpiId(upiId)
        if (existing != null) {
            dao.update(existing.copy(lastPaidMillis = System.currentTimeMillis()))
        } else {
            dao.upsert(Payee(nickname = nickname, upiId = upiId, lastPaidMillis = System.currentTimeMillis()).toEntity())
        }
    }

    suspend fun toggleFavorite(payee: Payee) {
        dao.upsert(payee.copy(isFavorite = !payee.isFavorite).toEntity())
    }

    suspend fun addPayee(nickname: String, upiId: String) {
        dao.upsert(Payee(nickname = nickname, upiId = upiId, isFavorite = true).toEntity())
    }
}

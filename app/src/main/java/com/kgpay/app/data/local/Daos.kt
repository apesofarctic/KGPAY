package com.kgpay.app.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Query("SELECT * FROM transactions ORDER BY timestampMillis DESC")
    fun observeAll(): Flow<List<TransactionEntity>>

    @Insert
    suspend fun insert(entity: TransactionEntity): Long
}

@Dao
interface PayeeDao {
    @Query("SELECT * FROM payees ORDER BY isFavorite DESC, lastPaidMillis DESC")
    fun observeAll(): Flow<List<PayeeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: PayeeEntity): Long

    @Update
    suspend fun update(entity: PayeeEntity)

    @Query("SELECT * FROM payees WHERE upiId = :upiId LIMIT 1")
    suspend fun findByUpiId(upiId: String): PayeeEntity?
}

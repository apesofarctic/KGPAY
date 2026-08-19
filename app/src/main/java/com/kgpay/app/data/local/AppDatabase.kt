package com.kgpay.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Note what is deliberately NOT stored here: the UPI PIN. Only transaction records (amount,
 * payee, status, category) and payee/contact nicknames persist locally — see
 * [com.kgpay.app.data.ussd.UssdLauncher] and [UssdAccessibilityService] for how the PIN stays
 * in-memory-only for the duration of a single USSD session.
 */
@Database(entities = [TransactionEntity::class, PayeeEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
    abstract fun payeeDao(): PayeeDao

    companion object {
        fun build(context: Context): AppDatabase =
            Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, "kgpay.db").build()
    }
}

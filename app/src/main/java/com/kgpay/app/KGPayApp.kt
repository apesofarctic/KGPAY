package com.kgpay.app

import android.app.Application
import com.kgpay.app.data.local.AppDatabase
import com.kgpay.app.data.repository.PayeeRepository
import com.kgpay.app.data.repository.SettingsRepository
import com.kgpay.app.data.repository.TransactionRepository

/**
 * Application-level container. No DI framework is used on purpose: the dependency
 * graph is tiny (three repositories over one Room database + one DataStore), so a
 * hand-rolled container keeps things easy to follow.
 */
class KGPayApp : Application() {

    lateinit var database: AppDatabase
        private set
    lateinit var transactionRepository: TransactionRepository
        private set
    lateinit var payeeRepository: PayeeRepository
        private set
    lateinit var settingsRepository: SettingsRepository
        private set

    override fun onCreate() {
        super.onCreate()
        database = AppDatabase.build(this)
        transactionRepository = TransactionRepository(database.transactionDao())
        payeeRepository = PayeeRepository(database.payeeDao())
        settingsRepository = SettingsRepository(this)
    }
}

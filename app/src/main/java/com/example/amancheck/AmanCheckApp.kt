package com.example.amancheck

import android.app.Application
import com.example.amancheck.data.local.AppDatabase
import com.example.amancheck.data.repository.AmanCheckRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

class AmanCheckApp : Application() {
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    val database by lazy { AppDatabase.getDatabase(this, applicationScope) }
    val repository by lazy { AmanCheckRepository(database.dao()) }

    override fun onCreate() {
        super.onCreate()
    }
}

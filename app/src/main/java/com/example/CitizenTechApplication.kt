package com.example

import android.app.Application
import com.example.data.local.CitizenTechDatabase
import com.example.data.local.UserPreferencesDataStore
import com.example.data.repository.CitizenTechRepository
import com.example.data.repository.DefaultCitizenTechRepository

class AppContainer(application: Application) {
    private val database: CitizenTechDatabase by lazy {
        CitizenTechDatabase.getInstance(application)
    }
    private val preferencesDataStore: UserPreferencesDataStore by lazy {
        UserPreferencesDataStore(application)
    }
    val repository: CitizenTechRepository by lazy {
        DefaultCitizenTechRepository(
            dao = database.dao(),
            prefsStore = preferencesDataStore,
        )
    }
}

class CitizenTechApplication : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}

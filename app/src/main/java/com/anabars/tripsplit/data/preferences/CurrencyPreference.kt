package com.anabars.tripsplit.data.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.anabars.tripsplit.utils.getDefaultCurrency
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CurrencyPreference @Inject constructor(private val dataStore: DataStore<Preferences>) {

    suspend fun saveCurrency(key: String, code: String) {
        dataStore.edit { settings ->
            settings[stringPreferencesKey(key)] = code
        }
    }

    fun getCurrencyFlow(key: String): Flow<String> {
        return dataStore.data.map { it[stringPreferencesKey(key)] ?: getDefaultCurrency() }
    }
}
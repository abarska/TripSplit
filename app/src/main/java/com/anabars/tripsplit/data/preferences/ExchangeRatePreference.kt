package com.anabars.tripsplit.data.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.anabars.tripsplit.common.TripSplitConstants.PREF_KEY_EXCHANGE_RATES
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ExchangeRatePreference @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {

    private val dataStoreKey = stringPreferencesKey(PREF_KEY_EXCHANGE_RATES)
    private val moshi = Moshi.Builder().build()
    private val type = Types.newParameterizedType(Map::class.java, String::class.java, String::class.java)
    private val adapter: JsonAdapter<Map<String, String>> = moshi.adapter(type)

    val exchangeRatesFlow: Flow<Map<String, String>> = dataStore.data.map { preferences ->
        val json = preferences[dataStoreKey]
        json?.let { adapter.fromJson(it) } ?: emptyMap()
    }

    suspend fun saveRates(rates: Map<String, String>) {
        val json = adapter.toJson(rates)
        dataStore.edit { prefs ->
            prefs[dataStoreKey] = json
        }
    }
}
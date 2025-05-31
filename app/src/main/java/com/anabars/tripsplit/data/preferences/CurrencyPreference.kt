package com.anabars.tripsplit.data.preferences

import android.content.Context
import android.telephony.TelephonyManager
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.anabars.tripsplit.TripSplitApplication
import com.anabars.tripsplit.common.TripSplitConstants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Currency
import java.util.Locale
import javax.inject.Inject

class CurrencyPreference @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {

    suspend fun saveCurrency(key: String, code: String) {
        dataStore.edit { settings ->
            val dataStoreKey = stringToKey(key)
            settings[dataStoreKey] = code
        }
    }

    fun getCurrencyFlow(key: String): Flow<String> {
        val default = defaultCurrencyByKey(key)
        val dataStoreKey = stringToKey(key)
        return dataStore.data.map { it[dataStoreKey] ?: default }
    }

    private fun stringToKey(key: String) = stringPreferencesKey(key)

    private fun defaultCurrencyByKey(key: String): String {
        if (key == TripSplitConstants.PREF_KEY_PREFERRED_CURRENCY) return "EUR"
        val context = TripSplitApplication.instance.applicationContext
        val simCurrency = getCurrencyFromSim(context)
        return simCurrency ?: "EUR"
    }

    private fun getCurrencyFromSim(context: Context): String? {
        val tm = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        val country = tm.simCountryIso.uppercase(Locale.US)
        return try {
            Currency.getInstance(Locale("", country)).currencyCode
        } catch (e: Exception) {
            null
        }
    }
}
package com.wanandroid.core.network.cookie

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import javax.inject.Inject
import javax.inject.Singleton

private val Context.cookieDataStore by preferencesDataStore(name = "wan_cookies")

@Singleton
class PersistentCookieJar @Inject constructor(
    @ApplicationContext private val context: Context,
) : CookieJar, CookieCleaner {

    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        if (cookies.isEmpty()) return
        val key = stringPreferencesKey(url.host)
        val cookieStr = cookies.joinToString(";") { "${it.name}=${it.value}" }
        runBlocking {
            context.cookieDataStore.edit { prefs -> prefs[key] = cookieStr }
        }
    }

    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        val key = stringPreferencesKey(url.host)
        val raw = runBlocking {
            context.cookieDataStore.data.first()[key] ?: return@runBlocking ""
        }
        if (raw.isBlank()) return emptyList()
        return raw.split(";").mapNotNull { pair ->
            val parts = pair.trim().split("=", limit = 2)
            if (parts.size == 2) {
                Cookie.Builder()
                    .domain(url.host)
                    .name(parts[0].trim())
                    .value(parts[1].trim())
                    .build()
            } else null
        }
    }

    override fun clearCookies() {
        runBlocking { context.cookieDataStore.edit { it.clear() } }
    }
}

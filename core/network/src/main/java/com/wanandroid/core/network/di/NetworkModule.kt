package com.wanandroid.core.network.di

import android.util.Log
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.wanandroid.core.network.BuildConfig
import com.wanandroid.core.network.WanApiService
import com.wanandroid.core.network.cookie.CookieCleaner
import com.wanandroid.core.network.cookie.PersistentCookieJar
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

private const val TAG = "WanNetwork"

@Module
@InstallIn(SingletonComponent::class)
abstract class NetworkModule {

    @Binds
    @Singleton
    abstract fun bindCookieCleaner(impl: PersistentCookieJar): CookieCleaner

    companion object {

        private val json = Json {
            ignoreUnknownKeys = true
            coerceInputValues = true
        }

        @Provides
        @Singleton
        fun provideOkHttpClient(cookieJar: PersistentCookieJar): OkHttpClient {
            val logging = HttpLoggingInterceptor { message ->
                Log.d(TAG, message)
            }.apply {
                level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY
                        else HttpLoggingInterceptor.Level.NONE
            }

            return OkHttpClient.Builder()
                .cookieJar(cookieJar)
                .addInterceptor(logging)
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .build()
        }

        @Provides
        @Singleton
        fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit =
            Retrofit.Builder()
                .baseUrl("https://wanandroid.com")
                .client(okHttpClient)
                .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
                .build()

        @Provides
        @Singleton
        fun provideWanApiService(retrofit: Retrofit): WanApiService =
            retrofit.create(WanApiService::class.java)
    }
}

package com.wanandroid.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.wanandroid.app.navigation.AppNavigation
import com.wanandroid.core.data.datastore.UserPreferencesDataStore
import com.wanandroid.core.ui.theme.WanAndroidTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var userPreferencesDataStore: UserPreferencesDataStore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val isLoggedIn by userPreferencesDataStore.isLoggedIn.collectAsStateWithLifecycle(false)
            WanAndroidTheme {
                AppNavigation(isLoggedIn = isLoggedIn)
            }
        }
    }
}

package com.github.quantakt.anilistclient.presentation.ui.activities.main

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.core.net.toUri
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import com.github.quantakt.anilistclient.ANILIST_API_CLIENT_ID
import com.github.quantakt.anilistclient.presentation.ui.screens.main.App
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    val viewModel: MainActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        holdSplashScreen()

        setContent {
            App(onLoginRequest = ::requestAuth)
        }
    }

    private fun holdSplashScreen() {
        // Suspend drawing while waiting for logged in user to be loaded by view model
        // activity continues to show splash screen while we are not ready yet.
        // Ref: https://developer.android.com/guide/topics/ui/splash-screen#suspend-drawing
        val content: View = findViewById(android.R.id.content)
        content.viewTreeObserver.addOnPreDrawListener(
            object : ViewTreeObserver.OnPreDrawListener {
                override fun onPreDraw(): Boolean {
                    return if (viewModel.isReady.value) {
                        content.viewTreeObserver.removeOnPreDrawListener(this)
                        true
                    } else {
                        false
                    }
                }
            }
        )
    }

    private fun requestAuth() {
        val authUrl = "https://anilist.co/api/v2/oauth/authorize".toUri().buildUpon()
            .appendQueryParameter("client_id", ANILIST_API_CLIENT_ID)
            .appendQueryParameter("response_type", "token")
            .build()

        startActivity(Intent(Intent.ACTION_VIEW, authUrl))
    }
}

package com.github.quantakt.anilistclient.presentation.ui.activities.auth

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.github.quantakt.anilistclient.R
import com.github.quantakt.anilistclient.presentation.ui.theme.AppTheme
import dagger.hilt.android.AndroidEntryPoint
import com.github.quantakt.anilistclient.presentation.ui.activities.main.MainActivity

@AndroidEntryPoint
class AnilistOAuthActivity : ComponentActivity() {
    companion object {
        private const val TAG = "AnilistOAuthActivity"
    }

    private val viewModel: AnilistOAuthActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                LoadingScreen()
            }
        }

        handleResult(intent.data)
    }

    private fun handleResult(data: Uri?) {
        val regex = "access_token=(.*?)&".toRegex()
        val matchResult = regex.find(data?.fragment.toString())

        val token = matchResult?.groups?.get(1)

        if (token != null) {
            lifecycleScope.launchWhenCreated {
                try {
                    viewModel.login(token.value)
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to login", e)
                    Toast.makeText(
                        this@AnilistOAuthActivity,
                        getString(R.string.error_anilist_login),
                        Toast.LENGTH_SHORT
                    ).show()
                } finally {
                    returnToMain()
                }
            }
        } else {
            returnToMain()
        }
    }

    private fun returnToMain() {
        finish()

        val intent = Intent(
            this,
            MainActivity::class.java
        ).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        }

        startActivity(intent)
    }
}

@Composable
private fun LoadingScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator()
    }
}
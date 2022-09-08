package com.github.quantakt.anilistclient.presentation.ui.screens.login

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.quantakt.anilistclient.R
import com.github.quantakt.anilistclient.presentation.ui.theme.AppTheme

@Composable
fun LoginScreen(onLoginRequest: () -> Unit) {

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {

        Text(
            modifier = Modifier
                .padding(horizontal = 32.dp)
                .padding(bottom = 32.dp),
            text = stringResource(R.string.description_sign_in),
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center
        )

        Button(
            onClick = onLoginRequest
        ) {

            Icon(
                modifier = Modifier
                    .padding(end = 8.dp)
                    .size(32.dp),
                painter = painterResource(id = R.drawable.anilist_logo),
                contentDescription = null,
                tint = Color.Unspecified
            )

            Text(stringResource(R.string.action_sign_in_with_anilist))
        }
    }
}

@Preview
@Composable
private fun Preview() {
    AppTheme {
        LoginScreen {

        }
    }
}
package com.github.quantakt.anilistclient.presentation.ui.screens.main.media

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallTopAppBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun MediaDetailsScreen(
    viewModel: MediaDetailsScreenViewModel,
    openMedia: (mediaId: Int) -> Unit,
) {
    MediaDetailsScreen(viewModel.mediaId)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MediaDetailsScreen(mediaId: Int) {
    // TODO: Implement media details screen
    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = {
                    Text(mediaId.toString())
                },
            )
        },
        content = {
            Text(
                modifier = Modifier.padding(it),
                text = mediaId.toString(),
            )
        }
    )
}

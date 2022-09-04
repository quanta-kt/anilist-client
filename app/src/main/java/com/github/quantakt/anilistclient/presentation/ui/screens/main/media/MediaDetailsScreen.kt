package com.github.quantakt.anilistclient.presentation.ui.screens.main.media

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun MediaDetailsScreen(
    viewModel: MediaDetailsScreenViewModel = hiltViewModel(),
    openMedia: (mediaId: Int) -> Unit,
    navigateUp: () -> Unit,
) {
    val viewState by viewModel.state.collectAsState()

    MediaDetailsScreen(
        viewState = viewState,
        openMedia = openMedia,
        navigateUp = navigateUp,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MediaDetailsScreen(
    viewState: MediaDetailsScreenState,
    openMedia: (mediaId: Int) -> Unit,
    navigateUp: () -> Unit,
) {
    // TODO: Implement media details screen
    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = {
                    val title = viewState.mediaDetails?.title
                    if (title != null) {
                        Text(
                            text = title,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = navigateUp) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = null
                        )
                    }
                }
            )
        },
        content = {
            val title = viewState.mediaDetails?.title
            if (title != null) {
                Text(
                    modifier = Modifier.padding(it),
                    text = title,
                )
            }
        }
    )
}

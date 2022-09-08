package com.github.quantakt.anilistclient.presentation.ui.screens.main.media

import com.github.quantakt.anilistclient.domain.entities.MediaDetails

data class MediaDetailsScreenState(
    val mediaDetails: MediaDetails? = null,
    val isRefreshing: Boolean = false,
)

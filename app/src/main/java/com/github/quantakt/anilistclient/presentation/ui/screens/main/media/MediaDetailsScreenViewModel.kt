package com.github.quantakt.anilistclient.presentation.ui.screens.main.media

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.github.quantakt.anilistclient.presentation.navigation.Screen
import javax.inject.Inject

class MediaDetailsScreenViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    val mediaId : Int = savedStateHandle[Screen.MediaDetails.ARG_MEDIA_ID]
        ?: error("${Screen.MediaDetails.ARG_MEDIA_ID} not specified")
}
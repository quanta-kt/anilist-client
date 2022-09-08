package com.github.quantakt.anilistclient.presentation.ui.screens.main.media

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.quantakt.anilistclient.domain.entities.MediaDetails
import com.github.quantakt.anilistclient.domain.observers.GetMediaDetails
import com.github.quantakt.anilistclient.presentation.navigation.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class MediaDetailsScreenViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    getMediaDetails: GetMediaDetails,
) : ViewModel() {

    private val mediaId : Int = savedStateHandle[Screen.MediaDetails.ARG_MEDIA_ID]
        ?: error("${Screen.MediaDetails.ARG_MEDIA_ID} not specified")

    private val mediaDetails: StateFlow<MediaDetails?> = getMediaDetails(GetMediaDetails.Params(mediaId))
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    private val isRefreshing = MutableStateFlow(false)

    val state = combine(mediaDetails, isRefreshing) { mediaDetails, isRefreshing ->
        MediaDetailsScreenState(
            mediaDetails = mediaDetails,
            isRefreshing = isRefreshing,
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        MediaDetailsScreenState()
    )
}
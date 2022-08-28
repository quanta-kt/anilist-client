package com.github.quantakt.anilistclient.presentation.ui.screens.main.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.github.quantakt.anilistclient.domain.entities.MediaListItem
import com.github.quantakt.anilistclient.domain.entities.MediaType
import com.github.quantakt.anilistclient.domain.interactors.UpdateMediaListItemProgress
import com.github.quantakt.anilistclient.domain.observers.ObserveViewerCustomLists
import com.github.quantakt.anilistclient.domain.observers.ObserveViewerMediaList
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ListScreenViewModel @Inject constructor(
    observeViewerMediaList: ObserveViewerMediaList,
    observeViewerCustomLists: ObserveViewerCustomLists,
    private val updateMediaListItemProgress: UpdateMediaListItemProgress,
) : ViewModel() {

    private val selectedTab = MutableStateFlow(MediaType.ANIME)
    private val animeQuery = MutableStateFlow(ViewerAnimeListQuery())
    private val mangaQuery = MutableStateFlow(ViewerMangaListQuery())

    @OptIn(ExperimentalCoroutinesApi::class)
    val animePagerFlow = animeQuery.flatMapLatest {
        observeViewerMediaList(ObserveViewerMediaList.Params(
            type = it.type,
            status = it.status,
            sort = it.sort,
            customList = it.customListFilter
        ))
    }.cachedIn(viewModelScope)

    @OptIn(ExperimentalCoroutinesApi::class)
    val mangaPagerFlow = mangaQuery.flatMapLatest {
        observeViewerMediaList(ObserveViewerMediaList.Params(
            type = it.type,
            status = it.status,
            sort = it.sort,
        ))
    }.cachedIn(viewModelScope)

    private val animeCustomLists = observeViewerCustomLists(ObserveViewerCustomLists.Params(MediaType.ANIME))
        .map { set -> set.map { it.name } }
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    private val mangaCustomLists = observeViewerCustomLists(ObserveViewerCustomLists.Params(MediaType.MANGA))
        .map { set -> set.map { it.name } }
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val state = combine(
        selectedTab,
        animeQuery,
        mangaQuery,
        animeCustomLists,
        mangaCustomLists,
    ) { selectedTab, animeQuery, mangaQuery, animeCustomLists, mangaCustomLists ->
        ListScreenState(
            selectedTab = selectedTab,
            animeListQuery = animeQuery,
            mangaListQuery = mangaQuery,
            animeCustomLists = animeCustomLists,
            mangaCustomLists = mangaCustomLists
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = ListScreenState()
    )

    fun setAnimeQuery(query: ViewerAnimeListQuery) {
        animeQuery.value = query
    }

    fun setMangaQuery(query: ViewerMangaListQuery) {
        mangaQuery.value = query
    }

    fun setSelectedTab(tab: MediaType) {
        selectedTab.value = tab
    }

    fun onUpdateProgress(mediaListItem: MediaListItem, progress: Int) {
        viewModelScope.launch {
            val result = updateMediaListItemProgress.executeSync(UpdateMediaListItemProgress.Params(
                mediaListItem = mediaListItem,
                progress = progress,
            ))

            if (result.isFailure) {
                // TODO: Show a snack bar
            }
        }
    }

    fun onUpdateProgressVolume(mediaListItem: MediaListItem, progress: Int) {
        viewModelScope.launch {
            val result = updateMediaListItemProgress.executeSync(UpdateMediaListItemProgress.Params(
                mediaListItem = mediaListItem,
                progressVolume = progress,
            ))

            if (result.isFailure) {
                // TODO: Show a snack bar
            }
        }
    }
}
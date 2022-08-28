package com.github.quantakt.anilistclient.presentation.ui.screens.main.list

import androidx.compose.runtime.Immutable
import com.github.quantakt.anilistclient.domain.entities.MediaListSort
import com.github.quantakt.anilistclient.domain.entities.MediaListStatus
import com.github.quantakt.anilistclient.domain.entities.MediaType

@Immutable
data class ListScreenState(
    val selectedTab: MediaType = MediaType.values().first(),
    val animeListQuery: ViewerAnimeListQuery = ViewerAnimeListQuery(),
    val mangaListQuery: ViewerMangaListQuery = ViewerMangaListQuery(),
    val animeCustomLists: List<String> = emptyList(),
    val mangaCustomLists: List<String> = emptyList(),
)

@Immutable
open class ViewerMediaListQuery(
    open val type: MediaType,
    open val status: Set<MediaListStatus>? = null,
    open val sort: MediaListSort? = null,
    open val customListFilter: String? = null,
)

@Immutable
data class ViewerAnimeListQuery(
    override val status: Set<MediaListStatus>? = null,
    override val sort: MediaListSort? = null,
    override val customListFilter: String? = null,
) : ViewerMediaListQuery(MediaType.ANIME)

@Immutable
data class ViewerMangaListQuery(
    override val status: Set<MediaListStatus>? = null,
    override val sort: MediaListSort? = null,
    override val customListFilter: String? = null,
) : ViewerMediaListQuery(MediaType.MANGA)

package com.github.quantakt.anilistclient.presentation.ui.screens.main.list

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import coil.compose.rememberImagePainter
import com.github.quantakt.anilistclient.R
import com.github.quantakt.anilistclient.domain.entities.MediaListItem
import com.github.quantakt.anilistclient.domain.entities.MediaListStatus
import com.github.quantakt.anilistclient.domain.entities.MediaType
import kotlinx.coroutines.launch

@Composable
fun ListScreen(
    listViewModel: ListScreenViewModel = hiltViewModel(),
) {
    val state by listViewModel.state.collectAsState()

    ListScreen(
        state = state,
        animePagingItems = listViewModel.animePagerFlow.collectAsLazyPagingItems(),
        mangaPagingItems = listViewModel.mangaPagerFlow.collectAsLazyPagingItems(),
        onChangeTab = listViewModel::setSelectedTab,
        onUpdateAnimeQuery = listViewModel::setAnimeQuery,
        onUpdateMangaQuery = listViewModel::setMangaQuery,
        onUpdateProgress = listViewModel::onUpdateProgress,
        onUpdateProgressVolume = listViewModel::onUpdateProgressVolume,
    )
}

@Composable
fun ListScreen(
    state: ListScreenState,
    animePagingItems: LazyPagingItems<MediaListItem>,
    mangaPagingItems: LazyPagingItems<MediaListItem>,
    onChangeTab: (mediaType: MediaType) -> Unit,
    onUpdateAnimeQuery: (query: ViewerAnimeListQuery) -> Unit,
    onUpdateMangaQuery: (query: ViewerMangaListQuery) -> Unit,
    onUpdateProgress: (mediaListItem: MediaListItem, progress: Int) -> Unit,
    onUpdateProgressVolume: (mediaListItem: MediaListItem, progress: Int) -> Unit,
) {

    Column(
        modifier = Modifier.fillMaxSize()
    ) {

        val selectedTab = state.selectedTab
        val selectedTabIndex = state.selectedTab.ordinal

        TabRow(
            modifier = Modifier.fillMaxWidth(),
            selectedTabIndex = selectedTabIndex,
            backgroundColor = MaterialTheme.colors.background,
            contentColor = MaterialTheme.colors.primary
        ) {
            MediaType.values().forEach {

                val color = if (selectedTab == it) {
                    MaterialTheme.colors.primary
                } else {
                    MaterialTheme.colors.onSurface
                }

                val label = if (it == MediaType.ANIME) {
                    R.string.title_anime_list
                } else {
                    R.string.title_manga_list
                }

                Tab(
                    selected = selectedTab == it,
                    onClick = { onChangeTab(it) }
                ) {
                    Text(
                        modifier = Modifier.padding(12.dp),
                        text = stringResource(label),
                        style = MaterialTheme.typography.body1,
                        color = color,
                    )
                }
            }
        }

        when (selectedTab) {
            MediaType.ANIME -> MediaList(
                pagingItems = animePagingItems,
                sheetContent = {
                    FilterSheet(
                        type = MediaType.ANIME,
                        statusFilters = state.animeListQuery.status ?: emptySet(),
                        onUpdateStatusFilter = {
                            onUpdateAnimeQuery(state.animeListQuery.copy(status = it.ifEmpty { null }))
                        },
                        customLists = state.animeCustomLists,
                        customListFilter = state.animeListQuery.customListFilter,
                        onUpdateCustomListFilter = {
                            onUpdateAnimeQuery(state.animeListQuery.copy(customListFilter = it))
                        }
                    )
                },
                onUpdateProgress = onUpdateProgress,
                onUpdateProgressVolume = onUpdateProgressVolume,
            )

            MediaType.MANGA -> MediaList(
                pagingItems = mangaPagingItems,
                sheetContent = {
                    FilterSheet(
                        type = MediaType.MANGA,
                        statusFilters = state.mangaListQuery.status ?: emptySet(),
                        onUpdateStatusFilter = {
                            onUpdateMangaQuery(state.mangaListQuery.copy(status = it.ifEmpty { null }))
                        },
                        customLists = state.mangaCustomLists,
                        customListFilter = state.mangaListQuery.customListFilter,
                        onUpdateCustomListFilter = {
                            onUpdateMangaQuery(state.mangaListQuery.copy(customListFilter = it))
                        }
                    )
                },
                onUpdateProgress = onUpdateProgress,
                onUpdateProgressVolume = onUpdateProgressVolume,
            )
        }
    }
}

@Composable
private fun FilterSheet(
    type: MediaType,
    statusFilters: Set<MediaListStatus>,
    onUpdateStatusFilter: (Set<MediaListStatus>) -> Unit,
    customLists: List<String>,
    customListFilter: String?,
    onUpdateCustomListFilter: (String?) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {

        // FIXME: Generalise this as a loop
        FilterItem(
            modifier = Modifier.fillMaxWidth(),
            checked = MediaListStatus.CURRENT in statusFilters,
            text = stringResource(
                if (type == MediaType.ANIME) R.string.anime_status_current
                else R.string.manga_status_current
            ),
            onCheckedChange = {
                val new = if (it) {
                    statusFilters + MediaListStatus.CURRENT
                } else {
                    statusFilters - MediaListStatus.CURRENT
                }

                onUpdateStatusFilter(new)
            }
        )

        FilterItem(
            modifier = Modifier.fillMaxWidth(),
            checked = MediaListStatus.REPEATING in statusFilters,
            text = stringResource(
                if (type == MediaType.ANIME) R.string.anime_status_repeating
                else R.string.manga_status_repeating
            ),
            onCheckedChange = {
                val new = if (it) {
                    statusFilters + MediaListStatus.REPEATING
                } else {
                    statusFilters - MediaListStatus.REPEATING
                }

                onUpdateStatusFilter(new)
            }
        )

        FilterItem(
            modifier = Modifier.fillMaxWidth(),
            checked = MediaListStatus.PAUSED in statusFilters,
            text = stringResource(R.string.anmanga_status_paused),
            onCheckedChange = {
                val new = if (it) {
                    statusFilters + MediaListStatus.PAUSED
                } else {
                    statusFilters - MediaListStatus.PAUSED
                }

                onUpdateStatusFilter(new)
            }
        )

        FilterItem(
            modifier = Modifier.fillMaxWidth(),
            checked = MediaListStatus.COMPLETED in statusFilters,
            text = stringResource(R.string.animanga_status_completed),
            onCheckedChange = {
                val new = if (it) {
                    statusFilters + MediaListStatus.COMPLETED
                } else {
                    statusFilters - MediaListStatus.COMPLETED
                }

                onUpdateStatusFilter(new)
            }
        )

        FilterItem(
            modifier = Modifier.fillMaxWidth(),
            checked = MediaListStatus.DROPPED in statusFilters,
            text = stringResource(R.string.animanga_status_dropped),
            onCheckedChange = {
                val new = if (it) {
                    statusFilters + MediaListStatus.DROPPED
                } else {
                    statusFilters - MediaListStatus.DROPPED
                }

                onUpdateStatusFilter(new)
            }
        )

        if (customLists.isNotEmpty()) {
            Divider(modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp))

            customLists.forEach { customListName ->
                FilterItem(
                    modifier = Modifier.fillMaxWidth(),
                    checked = customListName == customListFilter,
                    text = customListName,
                    onCheckedChange = {
                        if (it) {
                            onUpdateCustomListFilter(customListName)
                        } else {
                            onUpdateCustomListFilter(null)
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun FilterItem(
    modifier: Modifier = Modifier,
    checked: Boolean,
    text: String,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier = modifier
            .clip(MaterialTheme.shapes.small)
            .width(intrinsicSize = IntrinsicSize.Min)
            .clickable {
                onCheckedChange(!checked)
            }
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = null
        )

        Text(
            modifier = Modifier
                .weight(1f)
                .padding(start = 8.dp),
            text = text
        )
    }
}


@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun MediaList(
    pagingItems: LazyPagingItems<MediaListItem>,
    sheetContent: @Composable() (ColumnScope.() -> Unit),
    onUpdateProgress: (mediaListItem: MediaListItem, progress: Int) -> Unit,
    onUpdateProgressVolume: (mediaListItem: MediaListItem, progress: Int) -> Unit,
) {

    val scope = rememberCoroutineScope()

    val sheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden
    )

    ModalBottomSheetLayout(
        sheetState = sheetState,
        sheetContent = sheetContent,
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {

            MediaListPaging(
                modifier = Modifier.fillMaxSize(),
                pagingItems = pagingItems,
                onUpdateProgress = onUpdateProgress,
                onUpdateProgressVolume = onUpdateProgressVolume,
            )

            ExtendedFloatingActionButton(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
                text = {
                    Text(stringResource(R.string.action_filter_list))
                },
                icon = {
                    Icon(imageVector = Icons.Filled.Sort, contentDescription = null)
                },
                onClick = { scope.launch { sheetState.animateTo(ModalBottomSheetValue.Expanded) } }
            )
        }
    }
}

@Composable
private fun MediaListPaging(
    modifier: Modifier = Modifier,
    pagingItems: LazyPagingItems<MediaListItem>,
    onUpdateProgress: (mediaListItem: MediaListItem, progress: Int) -> Unit,
    onUpdateProgressVolume: (mediaListItem: MediaListItem, progress: Int) -> Unit,
) {
    val combinedLoadStates = pagingItems.loadState

    if (combinedLoadStates.refresh is LoadState.Loading) {
        Box(modifier = Modifier.fillMaxSize()) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center)
            )
        }

        return
    }

    LazyColumn(modifier = modifier) {
        items(pagingItems) { item ->
            MediaListItem(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
                    .padding(top = 8.dp),
                item = item,
                onUpdateProgress = {
                    if (item != null) {
                        onUpdateProgress(item, it)
                    }
                },
                onUpdateProgressVolume = {
                    if (item != null) {
                        onUpdateProgressVolume(item, it)
                    }
                }
            )
        }

        item {
            when (val appendState = combinedLoadStates.append) {
                LoadState.Loading -> {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .padding(vertical = 16.dp)
                                .align(Alignment.Center)
                        )
                    }
                }
                is LoadState.Error -> {
                    LoadStateError(
                        modifier = Modifier.fillMaxWidth(),
                        error = appendState.error.localizedMessage
                            ?: appendState.error.message
                            ?: "Something went wrong",
                    ) {
                        pagingItems.retry()
                    }
                }

                else -> {}
            }
        }

        // Space at bottom avoid FAB blocking the last list item
        item { Spacer(modifier = Modifier.height(80.dp)) }
    }
}

@Composable
private fun LoadStateError(
    modifier: Modifier = Modifier,
    error: String,
    onClickRetry: () -> Unit,
) {
    Column(modifier = modifier
        .fillMaxSize()
        .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            textAlign = TextAlign.Center,
            text = error,
            color = MaterialTheme.colors.error
        )

        Button(onClick = onClickRetry) {
            Text("Retry")
        }
    }
}

@Composable
private fun MediaListItem(
    modifier: Modifier = Modifier,
    item: MediaListItem?,
    onUpdateProgress: (progress: Int) -> Unit,
    onUpdateProgressVolume: (progress: Int) -> Unit,
) {
    Card(modifier = modifier) {

        Row(modifier = Modifier.height(IntrinsicSize.Min)) {

            Image(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(96.dp),
                painter = rememberImagePainter(item?.coverImageUrl),
                contentDescription = null,
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier
                    .padding(8.dp)
                    .weight(1f)
            ) {

                // Title
                item?.title?.let {
                    Text(
                        modifier = Modifier.padding(8.dp),
                        text = it,
                        style = MaterialTheme.typography.body1.copy(fontWeight = FontWeight.Bold)
                    )
                }

                // Score
                MediaScore(score = item?.score) {
                    // TODO
                }
            }

            // Progress
            Row(
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .height(intrinsicSize = IntrinsicSize.Min)
            ) {
                item?.progress?.let { progress ->
                    val total = (item.totalEpisodes ?: item.totalChapters)
                    MediaProgress(
                        modifier = Modifier.fillMaxHeight(),
                        progress = progress,
                        total = total,
                        onClickIncrement = {
                            onUpdateProgress(progress + 1)
                        },
                        onClickDecrement = {
                            onUpdateProgress(progress - 1)
                        },
                    )
                }

                item?.progressVolume?.let { progress ->
                    MediaProgress(
                        modifier = Modifier
                            .fillMaxHeight()
                            .padding(start = 4.dp),
                        progress = progress,
                        total = item.totalVolumes,
                        onClickIncrement = {
                            onUpdateProgressVolume(progress + 1)
                        },
                        onClickDecrement = {
                            onUpdateProgressVolume(progress - 1)
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun MediaScore(
    modifier: Modifier = Modifier,
    score: Double?,
    onClick: () -> Unit,
) {

    val formattedScore = score?.toString() ?: "?"

    Row(
        modifier = modifier
            .clip(MaterialTheme.shapes.small)
            .clickable { onClick() }
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Icon(
            modifier = Modifier.padding(end = 8.dp),
            imageVector = Icons.Filled.Star,
            contentDescription = null,
            tint = MaterialTheme.colors.primary
        )

        Text(
            text = formattedScore,
            style = MaterialTheme.typography.body2,
        )
    }
}

@Composable
private fun MediaProgress(
    modifier: Modifier = Modifier,
    progress: Int,
    total: Int?,
    onClickIncrement: () -> Unit,
    onClickDecrement: () -> Unit,
) {

    val incrementEnabled = total == null || progress < total
    val decrementEnabled = progress > 0
    val incrementIconAlpha = if (incrementEnabled) ContentAlpha.high else ContentAlpha.disabled
    val decrementIconAlpha = if (decrementEnabled) ContentAlpha.high else ContentAlpha.disabled

    CompositionLocalProvider(LocalTextStyle provides MaterialTheme.typography.body2) {
        Column(
            modifier = modifier
                .clip(MaterialTheme.shapes.small)
                .defaultMinSize(minWidth = 32.dp)
                .width(IntrinsicSize.Min),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
        ) {

            CompositionLocalProvider(LocalContentAlpha provides incrementIconAlpha) {
                Icon(
                    modifier = Modifier
                        .padding(vertical = 4.dp)
                        .clip(MaterialTheme.shapes.small)
                        .clickable(
                            onClick = onClickIncrement,
                            enabled = incrementEnabled
                        )
                        .padding(vertical = 8.dp)
                        .fillMaxWidth(),
                    imageVector = Icons.Filled.Add,
                    contentDescription = null,
                )
            }

            Text(
                modifier = Modifier
                    .padding(4.dp),
                text = progress.toString(),
            )

            Divider(modifier = Modifier
                .padding(vertical = 8.dp)
                .fillMaxWidth())

            Text(text = total?.toString() ?: "?")

            CompositionLocalProvider(LocalContentAlpha provides decrementIconAlpha) {
                Icon(
                    modifier = Modifier
                        .padding(vertical = 4.dp)
                        .clip(MaterialTheme.shapes.small)
                        .clickable(onClick = onClickDecrement, enabled = decrementEnabled)
                        .padding(vertical = 8.dp)
                        .fillMaxWidth(),
                    imageVector = Icons.Filled.Remove,
                    contentDescription = null,
                )
            }
        }
    }
}
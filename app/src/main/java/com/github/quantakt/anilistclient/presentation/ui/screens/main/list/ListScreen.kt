package com.github.quantakt.anilistclient.presentation.ui.screens.main.list

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
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
import com.github.quantakt.anilistclient.domain.entities.MediaType
import com.github.quantakt.anilistclient.presentation.ui.components.CustomTabRow
import java.text.NumberFormat

@Composable
fun ListScreen(
    listViewModel: ListScreenViewModel = hiltViewModel(),
    openFilter: (mediaType: MediaType) -> Unit,
    openMedia: (mediaId: Int) -> Unit,
) {
    val state by listViewModel.state.collectAsState()

    ListScreen(
        state = state,
        animePagingItems = listViewModel.animePagerFlow.collectAsLazyPagingItems(),
        mangaPagingItems = listViewModel.mangaPagerFlow.collectAsLazyPagingItems(),
        onChangeTab = listViewModel::setSelectedTab,
        onUpdateProgress = listViewModel::onUpdateProgress,
        onUpdateProgressVolume = listViewModel::onUpdateProgressVolume,
        openFilter = openFilter,
        openMedia = openMedia,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListScreen(
    state: ListScreenState,
    animePagingItems: LazyPagingItems<MediaListItem>,
    mangaPagingItems: LazyPagingItems<MediaListItem>,
    onChangeTab: (mediaType: MediaType) -> Unit,
    onUpdateProgress: (mediaListItem: MediaListItem, progress: Int) -> Unit,
    onUpdateProgressVolume: (mediaListItem: MediaListItem, progress: Int) -> Unit,
    openFilter: (mediaType: MediaType) -> Unit,
    openMedia: (mediaId: Int) -> Unit,
) {

    val selectedTab = state.selectedTab
    val selectedTabIndex = state.selectedTab.ordinal

    Scaffold(
        topBar = {
            Column {
                SmallTopAppBar(
                    modifier = Modifier.fillMaxWidth(),
                    title = {
                        Text(stringResource(R.string.title_list))
                    },
                    actions = {
                        IconButton(onClick = { /*TODO*/ }) {
                            Icon(imageVector = Icons.Default.Search, contentDescription = null)
                        }
                        IconButton(onClick = { /*TODO*/ }) {
                            Icon(imageVector = Icons.Default.Refresh, contentDescription = null)
                        }
                    },
                )

                CustomTabRow(
                    modifier = Modifier.fillMaxWidth(),
                    selectedTabIndex = selectedTabIndex,
                    contentColor = MaterialTheme.colorScheme.primary,
                ) {
                    MediaType.values().forEach {

                        val color = if (selectedTab == it) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        }

                        val label = if (it == MediaType.ANIME) {
                            R.string.title_anime_list
                        } else {
                            R.string.title_manga_list
                        }

                        Tab(
                            selected = selectedTab == it,
                            onClick = { onChangeTab(it) },
                            text = {
                                Text(
                                    text = stringResource(label),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = color,
                                )
                            }
                        )
                    }
                }
            }
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = {
                    Text(stringResource(R.string.action_filter_list))
                },
                icon = {
                    Icon(imageVector = Icons.Filled.Sort, contentDescription = null)
                },
                onClick = {
                    openFilter(state.selectedTab)
                }
            )
        },

        content = { paddingValues ->
            when (selectedTab) {
                MediaType.ANIME -> MediaListPaging(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = paddingValues,
                    pagingItems = animePagingItems,
                    onUpdateProgress = onUpdateProgress,
                    onUpdateProgressVolume = onUpdateProgressVolume,
                    openMedia = openMedia,
                )

                MediaType.MANGA -> MediaListPaging(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = paddingValues,
                    pagingItems = mangaPagingItems,
                    onUpdateProgress = onUpdateProgress,
                    onUpdateProgressVolume = onUpdateProgressVolume,
                    openMedia = openMedia,
                )
            }
        },
    )
}

@Composable
private fun MediaListPaging(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues,
    pagingItems: LazyPagingItems<MediaListItem>,
    onUpdateProgress: (mediaListItem: MediaListItem, progress: Int) -> Unit,
    onUpdateProgressVolume: (mediaListItem: MediaListItem, progress: Int) -> Unit,
    openMedia: (mediaId: Int) -> Unit,
) {
    val combinedLoadStates = pagingItems.loadState

    if (combinedLoadStates.refresh is LoadState.Loading) {
        Box(
            modifier = modifier.padding(contentPadding)
        ) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center)
            )
        }

        return
    }

    LazyColumn(
        modifier = modifier,
        contentPadding = contentPadding
    ) {
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
                },
                onClick = {
                    val mediaId = item?.mediaId
                    if (mediaId != null) {
                        openMedia(mediaId)
                    }
                },
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
                            ?: stringResource(R.string.error_unnkown),
                    ) {
                        pagingItems.retry()
                    }
                }

                else -> {}
            }
        }
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
            color = MaterialTheme.colorScheme.error
        )

        Button(onClick = onClickRetry) {
            Text(stringResource(R.string.action_retry))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MediaListItem(
    modifier: Modifier = Modifier,
    item: MediaListItem?,
    onUpdateProgress: (progress: Int) -> Unit,
    onUpdateProgressVolume: (progress: Int) -> Unit,
    onClick: () -> Unit,
) {

    // TODO: Display placeholders when mediaListItem is null
    val mediaListItem = item ?: return
    val title = mediaListItem.title
    val progress = mediaListItem.progress
    val progressVolume = mediaListItem.progressVolume

    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        onClick = onClick
    ) {

        Row(modifier = Modifier.height(IntrinsicSize.Min)) {

            Image(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(96.dp),
                painter = rememberImagePainter(mediaListItem.coverImageUrl),
                contentDescription = null,
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier
                    .padding(8.dp)
                    .weight(1f)
            ) {

                // Title
                if (title != null) {
                    Text(
                        modifier = Modifier.padding(8.dp),
                        text = title,
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                    )
                }

                // Score
                MediaScore(score = mediaListItem.score) {
                    // TODO
                }
            }

            // Progress
            Row(
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .height(intrinsicSize = IntrinsicSize.Min)
            ) {

                if (progress != null) {
                    val total = (mediaListItem.totalEpisodes ?: mediaListItem.totalChapters)
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

                if (progressVolume != null) {
                    MediaProgress(
                        modifier = Modifier
                            .fillMaxHeight()
                            .padding(start = 4.dp),
                        progress = progressVolume,
                        total = mediaListItem.totalVolumes,
                        onClickIncrement = {
                            onUpdateProgressVolume(progressVolume + 1)
                        },
                        onClickDecrement = {
                            onUpdateProgressVolume(progressVolume - 1)
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

    val numberFormat = remember { NumberFormat.getInstance() }

    val formattedScore =
        if (score != null) {
            numberFormat.format(score)
        } else {
            stringResource(R.string.unknown_value_placeholder)
        }

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
            tint = MaterialTheme.colorScheme.primary
        )

        Text(
            text = formattedScore,
            style = MaterialTheme.typography.bodySmall,
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

    val numberFormat = remember { NumberFormat.getInstance() }

    val incrementEnabled = total == null || progress < total
    val decrementEnabled = progress > 0

    CompositionLocalProvider(LocalTextStyle provides MaterialTheme.typography.bodySmall) {
        Column(
            modifier = modifier
                .clip(MaterialTheme.shapes.small)
                .defaultMinSize(minWidth = 32.dp)
                .width(IntrinsicSize.Min),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
        ) {

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

            Text(
                modifier = Modifier
                    .padding(4.dp),
                text = numberFormat.format(progress),
            )

            Divider(modifier = Modifier
                .padding(vertical = 8.dp)
                .fillMaxWidth())

            Text(
                text = if (total != null) {
                    numberFormat.format(total)
                } else {
                    stringResource(R.string.unknown_value_placeholder)
                }
            )

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
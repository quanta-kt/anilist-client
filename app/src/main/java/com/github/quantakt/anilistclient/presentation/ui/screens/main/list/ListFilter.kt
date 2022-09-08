package com.github.quantakt.anilistclient.presentation.ui.screens.main.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.github.quantakt.anilistclient.domain.entities.MediaListStatus
import com.github.quantakt.anilistclient.domain.entities.MediaType
import com.github.quantakt.anilistclient.presentation.localization.stringRes

@Composable
fun ListFilter(
    viewModel: ListScreenViewModel,
    mediaType: MediaType
) {

    val state by viewModel.state.collectAsState()

    val listQuery = if (mediaType == MediaType.MANGA) {
        state.mangaListQuery
    } else {
        state.animeListQuery
    }

    val customLists = if (mediaType == MediaType.MANGA) {
        state.mangaCustomLists
    } else {
        state.animeCustomLists
    }

    ListFilter(
        type = mediaType,
        statusFilters = listQuery.status ?: emptySet(),
        onUpdateStatusFilter = {
            if (mediaType == MediaType.MANGA) {
                viewModel.setMangaQuery(
                    state.mangaListQuery.copy(status = it.ifEmpty { null })
                )
            } else {
                viewModel.setAnimeQuery(
                    state.animeListQuery.copy(status = it.ifEmpty { null })
                )
            }
        },
        customLists = customLists,
        customListFilter = listQuery.customListFilter,
        onUpdateCustomListFilter = {
            if (mediaType == MediaType.MANGA) {
                viewModel.setMangaQuery(state.mangaListQuery.copy(customListFilter = it))
            } else {
                viewModel.setAnimeQuery(state.animeListQuery.copy(customListFilter = it))
            }
        }
    )
}

private val allMediaListStatuses = MediaListStatus.values()

@Composable
private fun ListFilter(
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

        allMediaListStatuses.forEach { status ->
            FilterItem(
                modifier = Modifier.fillMaxWidth(),
                checked = status in statusFilters,
                text = stringResource(status.stringRes(type)),
                onCheckedChange = {
                    val new = if (it) {
                        statusFilters + status
                    } else {
                        statusFilters - status
                    }

                    onUpdateStatusFilter(new)
                }
            )
        }

        if (customLists.isNotEmpty()) {
            Divider(modifier = Modifier.padding(16.dp))
        }

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
            text = text,
        )
    }
}
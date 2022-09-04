package com.github.quantakt.anilistclient.presentation.localization

import androidx.annotation.StringRes
import com.github.quantakt.anilistclient.R
import com.github.quantakt.anilistclient.domain.entities.MediaListStatus
import com.github.quantakt.anilistclient.domain.entities.MediaType

@StringRes
fun MediaListStatus.stringRes(mediaType: MediaType): Int {
    return when (this) {
        MediaListStatus.CURRENT ->
            if (mediaType == MediaType.ANIME) {
                R.string.anime_status_current
            } else {
                R.string.manga_status_current
            }
        MediaListStatus.REPEATING ->
            if (mediaType == MediaType.ANIME) {
                R.string.anime_status_repeating
            } else {
                R.string.manga_status_repeating
            }
        MediaListStatus.PLANNING -> R.string.animanga_status_planning
        MediaListStatus.COMPLETED -> R.string.animanga_status_completed
        MediaListStatus.DROPPED -> R.string.animanga_status_dropped
        MediaListStatus.PAUSED -> R.string.anmanga_status_paused
    }
}
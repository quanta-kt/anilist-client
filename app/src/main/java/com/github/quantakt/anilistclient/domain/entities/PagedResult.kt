package com.github.quantakt.anilistclient.domain.entities

data class PagedResult <T>(
    val items: List<T>,
    val currentPage: Int,
    val hasNextPage: Boolean,
    val lastPage: Int,
)
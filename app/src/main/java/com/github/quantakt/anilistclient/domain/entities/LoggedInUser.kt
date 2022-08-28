package com.github.quantakt.anilistclient.domain.entities

/**
 * An AniList logged in user with it's auth token
 */
interface LoggedInUser {
    val id: Int
    val token: String
}
package com.github.quantakt.anilistclient.data

import com.apollographql.apollo3.ApolloCall
import com.apollographql.apollo3.api.Operation
import com.apollographql.apollo3.api.Optional

/**
 * Adds auth header to this call
 */
fun <D : Operation.Data> ApolloCall<D>.anilistAuth(token: String) =
    addHttpHeader("Authorization", "Bearer $token")

/**
 * Adds auth header to this call if auth token is not null
 */
fun <D : Operation.Data> ApolloCall<D>.anilistAuthOptional(token: String?) =
    if (token != null)
        anilistAuth(token)
    else
        this

/**
 * Converts a Kotlin nullable value to Optional
 */
fun <T> T?.toOptional(): Optional<T> = Optional.presentIfNotNull(this)
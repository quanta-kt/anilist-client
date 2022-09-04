package com.github.quantakt.anilistclient.domain.entities

sealed class InvokeState
object InvokeStarted : InvokeState()
object InvokeSuccess : InvokeState()
data class InvokeError(val throwable: Throwable) : InvokeState()
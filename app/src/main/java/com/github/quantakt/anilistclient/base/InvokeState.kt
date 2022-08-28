package com.github.quantakt.anilistclient.base

sealed class InvokeState
object InvokeStarted : InvokeState()
object InvokeSuccess : InvokeState()
data class InvokeError(val throwable: Throwable) : InvokeState()
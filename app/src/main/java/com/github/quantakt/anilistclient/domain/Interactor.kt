package com.github.quantakt.anilistclient.domain

import com.github.quantakt.anilistclient.base.InvokeError
import com.github.quantakt.anilistclient.base.InvokeStarted
import com.github.quantakt.anilistclient.base.InvokeState
import com.github.quantakt.anilistclient.base.InvokeSuccess
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*

abstract class Interactor<in P> {

    operator fun invoke(
        params: P,
    ): Flow<InvokeState> = flow {
        emit(InvokeStarted)
        doWork(params)
        emit(InvokeSuccess)
    }.catch {
        emit(InvokeError(it))
    }

    suspend fun executeSync(params: P) = doWork(params)

    protected abstract suspend fun doWork(params: P)
}

abstract class ObservableInteractor<in P, R> {
    operator fun invoke(params: P): Flow<R> = createObservable(params)

    suspend fun executeSync(params: P): R = createObservable(params).first()

    protected abstract fun createObservable(params: P): Flow<R>
}

abstract class ResultInteractor<in P, R> {
    operator fun invoke(params: P): Flow<R> = flow {
        emit(doWork(params))
    }

    suspend fun executeSync(params: P): R = doWork(params)

    protected abstract suspend fun doWork(params: P): R
}

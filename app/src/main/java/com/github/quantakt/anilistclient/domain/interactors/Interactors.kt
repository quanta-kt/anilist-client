package com.github.quantakt.anilistclient.domain.interactors

import com.github.quantakt.anilistclient.domain.entities.InvokeError
import com.github.quantakt.anilistclient.domain.entities.InvokeStarted
import com.github.quantakt.anilistclient.domain.entities.InvokeState
import com.github.quantakt.anilistclient.domain.entities.InvokeSuccess
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

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

package com.github.quantakt.anilistclient.presentation.ui.activities.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.quantakt.anilistclient.base.AuthState
import com.github.quantakt.anilistclient.domain.observers.ObserveAuthState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    observeAuthState: ObserveAuthState
): ViewModel() {

    val authState: StateFlow<AuthState?> = observeAuthState(Unit)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = null
        )

    /**
     * If view model has retrieved default logged in user yet
     */
    val isReady: StateFlow<Boolean> = authState
        .map { it != null }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = false
        )
}
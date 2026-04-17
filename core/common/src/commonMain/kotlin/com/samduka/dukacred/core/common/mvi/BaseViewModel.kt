package com.samduka.dukacred.core.common.mvi

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

abstract class BaseViewModel<S : UiState, A : UiAction, E : UiEffect>(
    initialState: S,
) : AutoCloseable {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val _state = MutableStateFlow(initialState)
    private val _effects = MutableSharedFlow<E>(extraBufferCapacity = 1)

    val state = _state.asStateFlow()
    val effects = _effects.asSharedFlow()

    abstract fun onAction(action: A)

    protected fun updateState(reducer: (S) -> S) {
        _state.update(reducer)
    }

    protected fun launch(block: suspend CoroutineScope.() -> Unit) {
        scope.launch(block = block)
    }

    protected fun sendEffect(effect: E) {
        scope.launch {
            _effects.emit(effect)
        }
    }

    override fun close() {
        scope.cancel()
    }
}

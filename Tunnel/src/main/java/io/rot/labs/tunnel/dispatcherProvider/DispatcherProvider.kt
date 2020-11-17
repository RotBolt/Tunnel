package io.rot.labs.tunnel.dispatcherProvider

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.newSingleThreadContext

interface DispatcherProvider {
    fun provideMainDispatcher(): CoroutineDispatcher

    fun provideIoDispatcher(): CoroutineDispatcher

    fun provideComputationDispatcher(): CoroutineDispatcher

    fun provideSingleThreadDispatcher(name: String = "SingleThread"): CoroutineDispatcher
}
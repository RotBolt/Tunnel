package io.rot.labs.tunnel.dispatcherProvider

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.newSingleThreadContext

class DispatcherProvider {

    fun provideMainDispatcher(): CoroutineDispatcher = Dispatchers.Main

    fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.IO

    fun provideComputationDispatcher(): CoroutineDispatcher = Dispatchers.Default

    @OptIn(ObsoleteCoroutinesApi::class)
    fun provideSingleThreadDispatcher(name: String = "SingleThread"): CoroutineDispatcher =
        newSingleThreadContext(name)

}
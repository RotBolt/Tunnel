package io.rot.labs.tunnel.dispatcherProvider

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.newSingleThreadContext

class DispatcherProviderImpl : DispatcherProvider {

    override fun provideMainDispatcher(): CoroutineDispatcher = Dispatchers.Main

    override fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.IO

    override fun provideComputationDispatcher(): CoroutineDispatcher = Dispatchers.Default

    @OptIn(ObsoleteCoroutinesApi::class)
    override fun provideSingleThreadDispatcher(name: String): CoroutineDispatcher {
        val contextName = if (name.isEmpty()) "SingleThread" else name
        return newSingleThreadContext(contextName)
    }

}
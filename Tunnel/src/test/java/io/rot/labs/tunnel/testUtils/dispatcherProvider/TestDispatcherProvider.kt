package io.rot.labs.tunnel.testUtils.dispatcherProvider

import io.rot.labs.tunnel.dispatcherProvider.DispatcherProvider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineDispatcher

class TestDispatcherProvider : DispatcherProvider {
    override fun provideMainDispatcher(): CoroutineDispatcher = TestCoroutineDispatcher()

    override fun provideIoDispatcher(): CoroutineDispatcher = TestCoroutineDispatcher()

    override fun provideComputationDispatcher(): CoroutineDispatcher = TestCoroutineDispatcher()

    override fun provideSingleThreadDispatcher(name: String): CoroutineDispatcher =
        TestCoroutineDispatcher()
}
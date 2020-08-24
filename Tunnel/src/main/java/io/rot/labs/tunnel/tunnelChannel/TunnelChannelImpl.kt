package io.rot.labs.tunnel.tunnelChannel

import io.rot.labs.tunnel.collectorProvider.CollectorProvider
import io.rot.labs.tunnel.dispatcherProvider.DispatcherProvider
import io.rot.labs.tunnel.message.TunnelBundle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

class TunnelChannelImpl : TunnelChannel {

    private lateinit var mainChannel: BroadcastChannel<TunnelBundle>
    private lateinit var ioChannel: BroadcastChannel<TunnelBundle>
    private lateinit var computationChannel: BroadcastChannel<TunnelBundle>
    private lateinit var newSingleThreadChannel: BroadcastChannel<TunnelBundle>
    private lateinit var postChannel: BroadcastChannel<TunnelBundle>


    @OptIn(InternalCoroutinesApi::class)
    override fun initChannels(
        dispatcherProvider: DispatcherProvider,
        collectorProvider: CollectorProvider,
        coroutineScope: CoroutineScope
    ) {
        mainChannel = BroadcastChannel(Channel.BUFFERED)
        ioChannel = BroadcastChannel(Channel.BUFFERED)
        computationChannel = BroadcastChannel(Channel.BUFFERED)
        newSingleThreadChannel = BroadcastChannel(Channel.BUFFERED)
        postChannel = BroadcastChannel(Channel.BUFFERED)

        coroutineScope.launch(dispatcherProvider.provideMainDispatcher()) {
            mainChannel.asFlow().apply {
                flowOn(dispatcherProvider.provideMainDispatcher())
                collect(collectorProvider.mainCollector)
            }
        }

        coroutineScope.launch(dispatcherProvider.provideIoDispatcher()) {
            ioChannel.asFlow().apply {
                flowOn(dispatcherProvider.provideIoDispatcher())
                collect(collectorProvider.ioCollector)
            }
        }

        coroutineScope.launch(dispatcherProvider.provideComputationDispatcher()) {
            computationChannel.asFlow().apply {
                flowOn(dispatcherProvider.provideComputationDispatcher())
                collect(collectorProvider.computationCollector)
            }
        }

        coroutineScope.launch(dispatcherProvider.provideSingleThreadDispatcher()) {
            newSingleThreadChannel.asFlow().apply {
                flowOn(dispatcherProvider.provideSingleThreadDispatcher())
                collect(collectorProvider.singleThreadCollector)
            }
        }

        coroutineScope.launch {
            postChannel.asFlow().apply {
                collect(collectorProvider.postCollector)
            }
        }
    }

    override fun getMainChannel(): BroadcastChannel<TunnelBundle> = mainChannel

    override fun getIoChannel(): BroadcastChannel<TunnelBundle> = ioChannel

    override fun getComputationChannel(): BroadcastChannel<TunnelBundle> = computationChannel

    override fun getNewSingleThreadChannel(): BroadcastChannel<TunnelBundle> =
        newSingleThreadChannel

    override fun getPostChannel(): BroadcastChannel<TunnelBundle> = postChannel

}
package io.rot.labs.tunnel.tunnelChannel

import io.rot.labs.tunnel.collectorProvider.CollectorProvider
import io.rot.labs.tunnel.dispatcherProvider.DispatcherProvider
import io.rot.labs.tunnel.message.TunnelBundle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.BroadcastChannel

interface TunnelChannel {

    fun initChannels(
        dispatcherProvider: DispatcherProvider,
        collectorProvider: CollectorProvider,
        coroutineScope: CoroutineScope
    )

    fun getMainChannel(): BroadcastChannel<TunnelBundle>

    fun getIoChannel(): BroadcastChannel<TunnelBundle>

    fun getComputationChannel(): BroadcastChannel<TunnelBundle>

    fun getNewSingleThreadChannel(): BroadcastChannel<TunnelBundle>

    fun getPostChannel(): BroadcastChannel<TunnelBundle>
}
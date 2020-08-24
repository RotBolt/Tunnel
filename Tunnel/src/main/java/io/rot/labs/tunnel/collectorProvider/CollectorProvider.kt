package io.rot.labs.tunnel.collectorProvider

import io.rot.labs.tunnel.message.TunnelBundle
import kotlinx.coroutines.flow.FlowCollector

data class CollectorProvider(
    var postCollector: FlowCollector<TunnelBundle>,
    var ioCollector: FlowCollector<TunnelBundle>,
    var mainCollector: FlowCollector<TunnelBundle>,
    var computationCollector: FlowCollector<TunnelBundle>,
    var singleThreadCollector: FlowCollector<TunnelBundle>
)
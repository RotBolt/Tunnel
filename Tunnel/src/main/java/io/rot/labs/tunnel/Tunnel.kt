package io.rot.labs.tunnel

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentHashMap
import kotlin.coroutines.CoroutineContext


class Tunnel {
    var pipe = BroadcastChannel<Any>(Channel.BUFFERED)
    private var pipeFlow = pipe.asFlow().apply {
        flowOn(Dispatchers.Main)
    }


    companion object {
        private var tunnel: Tunnel? = null
        fun get(): Tunnel {
            if (tunnel == null) {
                synchronized(Tunnel::class) {
                    if (tunnel == null) {
                        tunnel = Tunnel()
                    }
                }
            }
            return tunnel!!
        }
    }

    fun send(scope: CoroutineScope, drop: Any) {
        scope.launch() {
            println("BROADCAST $drop")
            pipe.send(drop)
        }
    }

    fun setDispatcher(coroutineContext: CoroutineContext) {
        pipeFlow.flowOn(coroutineContext)
    }

    fun subscribe(scope: CoroutineScope, action: (Any) -> Unit) {
        scope.launch {
            pipeFlow.collect(object : FlowCollector<Any> {
                override suspend fun emit(value: Any) {
                    println("INCOMING $value")
                    action.invoke(value)
                }
            })

            pipeFlow.collect {

            }
        }
    }

    fun init() {
        val mapClass = Class.forName("io.rot.labs.tunnel.TunnelMap")
        val method = mapClass.getMethod("getMap")
        val map = method.invoke(null) as ConcurrentHashMap<*, *>
        println("PUI MAP ${map.size}")
    }
}
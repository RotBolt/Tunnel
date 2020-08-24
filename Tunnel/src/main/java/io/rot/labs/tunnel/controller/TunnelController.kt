package io.rot.labs.tunnel.controller

import io.rot.labs.tunnel.regisrtyClass.RegistryClass
import io.rot.labs.tunnel.collectorProvider.CollectorProvider
import io.rot.labs.tunnel.dispatcherProvider.DispatcherProvider
import io.rot.labs.tunnel.message.TunnelBundle
import io.rot.labs.tunnel.message.TunnelMessage
import io.rot.labs.tunnel.tunnelChannel.TunnelChannel
import io.rot.labs.tunnel_common.SubscriberDetail
import io.rot.labs.tunnel_common.utils.DispatcherType
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentHashMap
import kotlin.coroutines.CoroutineContext

class TunnelController(
    private val map: ConcurrentHashMap<String, ArrayList<SubscriberDetail>>,
    private val registryMap: HashMap<String, Any>,
    private val dispatcherProvider: DispatcherProvider,
    private val tunnelChannel: TunnelChannel
) : Controller, CoroutineScope {

    init {
        val collectorProvider = CollectorProvider(
            getCollector(),
            getCollector(),
            getCollector(),
            getCollector(),
            getCollector()
        )
        tunnelChannel.initChannels(dispatcherProvider, collectorProvider, this)
    }

    private fun getCollector(): FlowCollector<TunnelBundle> {
        return object : FlowCollector<TunnelBundle> {
            override suspend fun emit(value: TunnelBundle) {
                deliverMessage(value)
            }
        }
    }

    private fun deliverMessage(bundle: TunnelBundle) {
        val invokerObj = registryMap[bundle.subscriberDetail.invokerClassName]
        invokerObj?.let {
            val invokerClass = Class.forName(bundle.subscriberDetail.invokerClassName)
            val method = invokerClass.getMethod(
                bundle.subscriberDetail.methodName,
                bundle.subscriberDetail.messageObjClass
            )
            method.invoke(it, bundle.msgObject)
        }
    }

    override fun <T : Any> register(registryClass: RegistryClass<T>) {
        registryMap[registryClass.registryObj::class.qualifiedName.toString()] =
            registryClass.registryObj
    }

    override fun <T : Any> post(tunnelMessage: TunnelMessage<T>, vararg channelIds: String) {
        for (channelId in channelIds) {
            val key = tunnelMessage.messageObject::class.qualifiedName.toString() + "_" + channelId
            val subscriberInfoList = map[key]
            if (subscriberInfoList != null) {
                for (subscriberDetail in subscriberInfoList) {
                    launch(getDispatcher(subscriberDetail.dispatcherType)) {
                        val channel = getChannel(subscriberDetail.dispatcherType)
                        channel.send(TunnelBundle(tunnelMessage.messageObject, subscriberDetail))
                    }
                }
            }
        }
    }

    override fun <T : Any> unregister(registryClass: RegistryClass<T>) {
        registryMap.remove(registryClass.registryObj::class.qualifiedName.toString())
    }

    private fun getChannel(dispatcherType: DispatcherType): BroadcastChannel<TunnelBundle> {
        return when (dispatcherType) {
            DispatcherType.IO -> tunnelChannel.getIoChannel()
            DispatcherType.MAIN -> tunnelChannel.getMainChannel()
            DispatcherType.COMPUTATION -> tunnelChannel.getComputationChannel()
            DispatcherType.NEW_SINGLE_THREAD -> tunnelChannel.getNewSingleThreadChannel()
            DispatcherType.POST -> tunnelChannel.getPostChannel()
        }
    }

    private fun getDispatcher(dispatcherType: DispatcherType): CoroutineDispatcher {
        return when (dispatcherType) {
            DispatcherType.IO -> dispatcherProvider.provideIoDispatcher()
            DispatcherType.MAIN -> dispatcherProvider.provideMainDispatcher()
            DispatcherType.COMPUTATION -> dispatcherProvider.provideComputationDispatcher()
            DispatcherType.NEW_SINGLE_THREAD -> dispatcherProvider.provideSingleThreadDispatcher()
            DispatcherType.POST -> dispatcherProvider.provideComputationDispatcher()
        }
    }

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Default
}
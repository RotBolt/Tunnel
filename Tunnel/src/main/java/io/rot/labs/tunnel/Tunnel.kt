package io.rot.labs.tunnel

import io.rot.labs.tunnel.controller.TunnelController
import io.rot.labs.tunnel.dispatcherProvider.DispatcherProvider
import io.rot.labs.tunnel.dispatcherProvider.DispatcherProviderImpl
import io.rot.labs.tunnel.message.TunnelMessage
import io.rot.labs.tunnel.regisrtyClass.RegistryClass
import io.rot.labs.tunnel.tunnelChannel.TunnelChannelImpl
import io.rot.labs.tunnel_common.SubscriberDetail
import io.rot.labs.tunnel_common.utils.NameStore
import kotlinx.coroutines.InternalCoroutinesApi
import java.util.concurrent.ConcurrentHashMap

@InternalCoroutinesApi
class Tunnel private constructor() {

    private lateinit var tunnelController: TunnelController

    private var dispatcherProvider: DispatcherProvider? = null

    private fun init(dispatcherProvider: DispatcherProvider) {
        val mapClass =
            Class.forName("${NameStore.GENERATED_ROOT_PACKAGE}.${NameStore.MAP_CLASS_NAME}")
        val method = mapClass.getMethod(NameStore.MAP_FUN_NAME)
        val map = method.invoke(null) as ConcurrentHashMap<String, ArrayList<SubscriberDetail>>
        tunnelController =
            TunnelController(
                map,
                hashMapOf(),
                dispatcherProvider,
                TunnelChannelImpl()
            )
    }

    companion object {
        private var tunnel: Tunnel? = null
        fun get(customDispatcherProvider: DispatcherProvider = DispatcherProviderImpl()): Tunnel {
            if (tunnel == null) {
                synchronized(Tunnel::class) {
                    if (tunnel == null) {
                        tunnel = Tunnel()
                        tunnel!!.init(customDispatcherProvider)
                    }
                }
            }
            return tunnel!!
        }
    }

    fun <T : Any> register(registryClass: RegistryClass<T>) {
        tunnelController.register(registryClass)
    }

    fun <T : Any> unregister(registryClass: RegistryClass<T>) {
        tunnelController.unregister(registryClass)
    }

    fun <T : Any> post(tunnelMessage: TunnelMessage<T>, vararg channelIds: String = arrayOf("default")) {
        tunnelController.post(tunnelMessage, *channelIds)
    }
}
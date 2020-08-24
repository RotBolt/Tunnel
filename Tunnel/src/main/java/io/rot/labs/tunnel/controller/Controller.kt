package io.rot.labs.tunnel.controller

import io.rot.labs.tunnel.regisrtyClass.RegistryClass
import io.rot.labs.tunnel.message.TunnelMessage


interface Controller {

    /**
     *  register this object to receive the events in @Subscribed methods
     */
    fun <T : Any> register(registryClass: RegistryClass<T>)


    /**
     *  send the message object to desired channel
     */
    fun <T : Any> post(tunnelMessage: TunnelMessage<T>, vararg channelIds: String)

    /**
     *  unregister this object to receive the events in @Subscribed methods
     */
    fun <T : Any> unregister(registryClass: RegistryClass<T>)


}
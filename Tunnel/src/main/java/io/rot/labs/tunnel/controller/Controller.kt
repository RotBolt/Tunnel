package io.rot.labs.tunnel.controller

interface Controller {

    /**
     *  register this object to receive the events in @Subscribed methods
     */
    fun register(parentObject: Any)


    /**
     *  send the message object to desired channel
     */
    fun post(msgObject: Any, vararg channelIds: String)

    /**
     *  unregister this object to receive the events in @Subscribed methods
     */
    fun unregister(parentObject: Any)


}
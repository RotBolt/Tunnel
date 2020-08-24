package io.rot.labs.tunnel.message


/**
 *  Tunnel Message Object
 *  Object which is actually posted and delivered to Target annotated with @Subsribe
 */
data class TunnelMessage<T:Any>(
    val messageObject: T
)
package io.rot.labs.tunnel.message

import io.rot.labs.tunnel_common.SubscriberDetail


/**
 *  Helper class to deliver the message object to @Subscribed Target
 */
data class TunnelBundle(
    /**
     *  Actual Message Object
     */
    val msgObject: Any,

    /**
     *  Details of Target annotated with Subscribed
     */
    val subscriberDetail: SubscriberDetail
)
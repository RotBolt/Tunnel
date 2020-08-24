package io.rot.labs.tunnel.message

data class TunnelMessage(
    val key: String,
    val msgObject: Any,
    val channelId: String
)
package io.rot.labs.tunnel_compiler

import io.rot.labs.tunnel_common.utils.DispatcherType

data class SubscriberDetailHolder(
    val invokerClassName: String,
    val methodName: String,
    val messageObjClass: String,
    val dispatcherType: DispatcherType
)
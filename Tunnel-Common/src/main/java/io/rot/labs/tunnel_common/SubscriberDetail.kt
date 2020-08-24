package io.rot.labs.tunnel_common

import io.rot.labs.tunnel_common.utils.DispatcherType


data class SubscriberDetail(
    val invokerClassName :String,
    val methodName : String,
    val dispatcherType : DispatcherType
)
package io.rot.labs.tunnel_common.annotation

import io.rot.labs.tunnel_common.utils.DispatcherType
import io.rot.labs.tunnel_common.utils.Constants.DEFAULT_CHANNEL

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class Subscribe(
    val dispatcherType: DispatcherType = DispatcherType.POST,
    val channelIds: Array<String> = [DEFAULT_CHANNEL]
)
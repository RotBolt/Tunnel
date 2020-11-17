package io.rot.labs.tunnel.testUtils.targets

import io.rot.labs.tunnel.testUtils.messageObjects.MessageZero
import io.rot.labs.tunnel_common.annotation.Subscribe

open class SimpleTarget {

    @Subscribe
    fun onEvent(message: MessageZero) {

    }
}
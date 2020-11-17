package io.rot.labs.tunnel.testUtils.targets

import io.rot.labs.tunnel.testUtils.Channels
import io.rot.labs.tunnel.testUtils.messageObjects.HugeMessage
import io.rot.labs.tunnel_common.annotation.Subscribe
import io.rot.labs.tunnel_common.utils.DispatcherType

open class HugeTarget {

    var myMessageList = mutableListOf<HugeMessage>()

    @Subscribe(DispatcherType.IO, channelIds = [Channels.CHANNEL_1])
    fun onReceived(hugeMessage: HugeMessage) {
        myMessageList.add(hugeMessage)
    }
}
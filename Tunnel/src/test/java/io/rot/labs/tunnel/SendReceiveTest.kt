package io.rot.labs.tunnel

import io.rot.labs.tunnel.message.TunnelMessage
import io.rot.labs.tunnel.regisrtyClass.RegistryClass
import io.rot.labs.tunnel.testUtils.Channels
import io.rot.labs.tunnel.testUtils.dispatcherProvider.TestDispatcherProvider
import io.rot.labs.tunnel.testUtils.messageObjects.HugeMessage
import io.rot.labs.tunnel.testUtils.messageObjects.MessageZero
import io.rot.labs.tunnel.testUtils.targets.HugeTarget
import io.rot.labs.tunnel.testUtils.targets.SimpleTarget
import kotlinx.coroutines.InternalCoroutinesApi
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import java.util.concurrent.CountDownLatch
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

@InternalCoroutinesApi
class SendReceiveTest {

    private lateinit var tunnel: Tunnel

    @Before
    fun setup() {
        tunnel = Tunnel.get(TestDispatcherProvider())
    }

    @Test
    fun simple_post_event_test() {
        val simpleTarget = Mockito.spy(SimpleTarget())
        tunnel.register(RegistryClass(simpleTarget))
        val message = MessageZero()
        tunnel.post(TunnelMessage(message))
        verify(simpleTarget).onEvent(message)
    }

    @Test
    fun send_200_events() {
        val latch = CountDownLatch(200_000)
        val hugeTarget = HugeTarget()
        tunnel.register(RegistryClass(hugeTarget))
        val message = HugeMessage()

        val executorOne = ThreadPoolExecutor(
            10, 20, 60, TimeUnit.SECONDS,
            LinkedBlockingQueue<Runnable>()
        )

        for (i in 0 until 100_000) {
            executorOne.execute(Runnable {
                tunnel.post(TunnelMessage(message))
                latch.countDown()
            })
        }

        val executorTwo = ThreadPoolExecutor(
            10, 20, 60, TimeUnit.SECONDS,
            LinkedBlockingQueue<Runnable>()
        )

        for (i in 0 until 100_000) {
            executorTwo.execute(Runnable {
                tunnel.post(TunnelMessage(message), Channels.CHANNEL_1)
                latch.countDown()
            })
        }
        assertTrue(latch.await(2, TimeUnit.SECONDS))
        assertTrue(hugeTarget.myMessageList.isNotEmpty())
        assertTrue(hugeTarget.myMessageList.size == 100_000)
    }

}
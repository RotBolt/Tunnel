package io.rot.labs.tunnel

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import io.rot.labs.tunnel_common.utils.DispatcherType
import io.rot.labs.tunnel_common.annotation.Subscribe
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext


@InternalCoroutinesApi
class MainActivity : AppCompatActivity(), CoroutineScope {


    override val coroutineContext: CoroutineContext = SupervisorJob() + Dispatchers.Main

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Tunnel().init()
    }

    override fun onResume() {
        super.onResume()

        val tunnel = Tunnel.get().apply {
            setDispatcher(Dispatchers.Main)
        }

        tunnel.send(this, "opp")

        tunnel.subscribe(this) {
            tvPUI.text = it.toString()
        }

        launch {
            delay(3000)
            tunnel.send(this, TunnelMap.getMap().size)
            delay(2000)
            tunnel.send(this, "tuta")
        }
    }

    @Subscribe(dispatcherType = DispatcherType.IO, channelIds = ["CHANNEL_1","CHANNEL_2"])
    fun puiOnEvent(name: Bundle) {

    }
}
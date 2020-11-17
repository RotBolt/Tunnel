package io.rot.labs.tunnelapp

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import io.rot.labs.tunnel.Tunnel
import io.rot.labs.tunnel.message.TunnelMessage
import io.rot.labs.tunnel.regisrtyClass.RegistryClass
import io.rot.labs.tunnel_common.annotation.Subscribe
import io.rot.labs.tunnel_common.utils.DispatcherType
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

@InternalCoroutinesApi
open class MainActivity : AppCompatActivity(), CoroutineScope {


    override val coroutineContext: CoroutineContext = SupervisorJob() + Dispatchers.Main


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        launch {
            delay(5000)
            Tunnel.get().post(TunnelMessage(Bundle().apply {
                putString("PUI", "Oppaaaaaai")
            }), "CHANNEL_1")
        }

        btnSendEvents.setOnClickListener {
            send200kEvents()
        }
    }

    fun send200kEvents() {
        val startTime = System.currentTimeMillis()
        for (i in 0..200_000) {
            Tunnel.get().post(TunnelMessage(Bundle().apply {
                putString("PUI", i.toString())
            }),"CHANNEL_1")
        }
        val timeTook = System.currentTimeMillis() - startTime
        Tunnel.get().post(TunnelMessage(Bundle().apply {
            putString("PUI", "Time took $timeTook")
        }), "CHANNEL_2")
    }

    override fun onStart() {
        super.onStart()
        Tunnel.get().register(RegistryClass(this))
    }


    override fun onStop() {
        super.onStop()
        Tunnel.get().unregister(RegistryClass(this))
    }

    @Subscribe(dispatcherType = DispatcherType.IO, channelIds = ["CHANNEL_1", "CHANNEL_2"])
    open fun puiOnEvent(name: Bundle) {
        val string = name["PUI"].toString()
        Log.d("PUI", "RecievedValue $string")
        tvPUI.text = "Received Value $string"
    }
}
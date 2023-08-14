package com.huynhngoctai.vpn_ict

import android.app.Application
import com.google.android.gms.ads.MobileAds
import com.wireguard.android.backend.Backend
import com.wireguard.android.backend.GoBackend
import com.wireguard.android.backend.Tunnel
import com.wireguard.config.Config
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference

class App : Application() {

    private var backend: Backend? = null
    private val futureBackend = CompletableDeferred<Backend>()
    private val coroutineScope = CoroutineScope(Job() + Dispatchers.Main.immediate)

    private var myTunnel: Tunnel? = null
    private val myConfig: Config? = null

    fun getMyTunnel(): Tunnel? {
        return myTunnel
    }

    fun getMyConfig(): Config? {
        return myConfig
    }

    init {
        weakSelf = WeakReference(this)
    }

    override fun onCreate() {
        super.onCreate()
        coroutineScope.launch(Dispatchers.IO) {
            try {
                backend = GoBackend(applicationContext)
                futureBackend.complete(backend!!)
            } catch (e: Throwable) {
                e.printStackTrace()
            }
        }

        //initialize Ads SDK
        MobileAds.initialize(this) {}
    }

    companion object {
        private lateinit var weakSelf: WeakReference<App>

        fun get(): App {
            return weakSelf.get()!!
        }

        suspend fun getBackend() = get().futureBackend.await()
    }
}
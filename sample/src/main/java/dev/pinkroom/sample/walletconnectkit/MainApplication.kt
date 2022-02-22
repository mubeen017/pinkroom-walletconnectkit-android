package dev.pinkroom.sample.walletconnectkit

import android.app.Application
import android.content.Intent
import android.widget.Toast
import dev.pinkroom.walletconnectkit.WalletConnectKit
import dev.pinkroom.walletconnectkit.WalletConnectKitConfig

class MainApplication : Application() {

    private val config = WalletConnectKitConfig(
        bridgeUrl = "wss://bridge.walletconnect.org",
        appUrl = "walletconnectkit.com",
        appName = "WalletConnect Kit",
        appDescription = "This is the Swiss Army toolkit for WalletConnect!"
    )

    val walletConnectKit by lazy {
        WalletConnectKit.builder(this).config(config).build().apply {
            onDisconnected = ::onDisconnected
        }
    }

    private fun onDisconnected() {
        Toast.makeText(applicationContext, "Disconnecting...", Toast.LENGTH_SHORT).show()
        val intent = Intent(applicationContext, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }
}
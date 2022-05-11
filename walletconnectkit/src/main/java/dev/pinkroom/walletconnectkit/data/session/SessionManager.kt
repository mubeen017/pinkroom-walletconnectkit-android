package dev.pinkroom.walletconnectkit.data.session

import org.walletconnect.Session
import org.walletconnect.impls.WCSessionStore

interface SessionManager {
    var session: Session?
    val address: String?
    fun createSession(callback: Session.Callback)
    fun removeSession()
    fun loadSession(callback: Session.Callback)
    val isSessionStored: Boolean
    val storages: WCSessionStore
}
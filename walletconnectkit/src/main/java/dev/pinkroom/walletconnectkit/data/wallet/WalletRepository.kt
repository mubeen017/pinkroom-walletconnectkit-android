package dev.pinkroom.walletconnectkit.data.wallet

import android.content.Intent
import android.net.Uri
import dev.pinkroom.walletconnectkit.WalletConnectKitConfig
import dev.pinkroom.walletconnectkit.common.Dispatchers
import dev.pinkroom.walletconnectkit.common.toHex
import dev.pinkroom.walletconnectkit.common.toWei
import dev.pinkroom.walletconnectkit.data.session.SessionRepository
import kotlinx.coroutines.withContext
import org.walletconnect.Session
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

internal class WalletRepository(
    private val walletConnectKitConfig: WalletConnectKitConfig,
    private val sessionRepository: SessionRepository,
    private val dispatchers: Dispatchers,
) : WalletManager {

    override fun openWallet() {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse("wc:")
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        walletConnectKitConfig.context.startActivity(intent)
    }

    override fun requestHandshake() {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(sessionRepository.wcUri)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        walletConnectKitConfig.context.startActivity(intent)
    }

    override suspend fun performTransaction(
        address: String,
        value: String,
        data: String?,
        nonce: String?,
        gasPrice: String?,
        gasLimit: String?,
    ): Result<Session.MethodCall.Response> {
        return withContext(dispatchers.io) {
            suspendCoroutine { continuation ->
                sessionRepository.address?.let { fromAddress ->
                    sessionRepository.session?.let { session ->
                        val id = System.currentTimeMillis()
                        session.performMethodCall(
                            Session.MethodCall.SendTransaction(
                                id,
                                fromAddress,
                                address,
                                nonce,
                                gasPrice,
                                gasLimit,
                                value.toWei().toHex(),
                                data ?: ""
                            )
                        ) { response -> onPerformTransactionResponse(id, response, continuation) }
                        openWallet()
                    } ?: continuation.resume(Result.failure(Throwable("Session not found!")))
                } ?: continuation.resume(Result.failure(Throwable("Address not found!")))
            }
        }
    }

    override suspend fun performTransaction(
        address: String,
        value: String,
        nonce: String?,
        gasPrice: String?,
        gasLimit: String?,
    ) = performTransaction(address, value, null, nonce, gasLimit, gasLimit)

    private fun onPerformTransactionResponse(
        id: Long,
        response: Session.MethodCall.Response,
        continuation: Continuation<Result<Session.MethodCall.Response>>
    ) {
        if (id != response.id) {
            val throwable = Throwable("The response id is different from the transaction id!")
            continuation.resume(Result.failure(throwable))
            return
        }
        response.error?.let {
            continuation.resume(Result.failure(Throwable(it.message)))
        } ?: continuation.resume(Result.success(response))
    }
}
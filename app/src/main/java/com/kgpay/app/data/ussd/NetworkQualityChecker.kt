package com.kgpay.app.data.ussd

import kotlinx.coroutines.suspendCancellableCoroutine
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import kotlin.coroutines.resume

enum class NetworkQuality { STABLE, POOR, UNKNOWN }

/**
 * Downloads a small fixed asset and times it, same technique as the original
 * `InternetSpeed.java`, but as a suspend function instead of a blocking `CountDownLatch` so it
 * plays nicely with a Compose ViewModel/coroutine scope.
 */
class NetworkQualityChecker(private val client: OkHttpClient = OkHttpClient()) {

    suspend fun check(): NetworkQuality = suspendCancellableCoroutine { cont ->
        val request = Request.Builder()
            .url("https://upload.wikimedia.org/wikipedia/commons/thumb/5/5b/HTTP_logo.svg/768px-HTTP_logo.svg.png")
            .build()
        val start = System.currentTimeMillis()
        val call = client.newCall(request)
        cont.invokeOnCancellation { call.cancel() }
        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                if (cont.isActive) cont.resume(NetworkQuality.UNKNOWN)
            }

            override fun onResponse(call: Call, response: Response) {
                val bytes = response.body?.bytes()?.size ?: 0
                val elapsedMs = (System.currentTimeMillis() - start).coerceAtLeast(1)
                val kbPerSec = bytes / 1024.0 / (elapsedMs / 1000.0)
                if (cont.isActive) cont.resume(if (kbPerSec < 100) NetworkQuality.POOR else NetworkQuality.STABLE)
            }
        })
    }
}

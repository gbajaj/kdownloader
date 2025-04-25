package com.gbajaj.downloader

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class DownloadDispatcher(private val httpClient: HttpClient) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    fun enqueue(req: DownloaderRequest): Int {
        val job = scope.launch {
            execute(req)
        }
        req.job = job
        return req.downloadId
    }

    private suspend fun execute(request: DownloaderRequest) {
        DownloadTask(request, httpClient).run(
            onStart = {
                //IO
                executeOnMainThread { request.onStart() }
            },
            onProgress = {
                //IO
                executeOnMainThread { request.onProgress(it) }
            },
            onPause = {
                executeOnMainThread { request.onPause() }
            },
            onCompleted = {
                executeOnMainThread { request.onCompleted() }
            },
            onError = {
                executeOnMainThread { request.onError(it) }
            }
        )

    }

    private fun executeOnMainThread(block: () -> Unit) {
        scope.launch {
            //Main Thread
            block()
        }
    }

    fun cancel(request: DownloaderRequest) {
        request.job.cancel()
        executeOnMainThread { request.onCancel() }
    }

    fun cancelAll() {
        scope.cancel()
    }
}
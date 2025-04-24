package com.gbajaj.downloader

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

class DownloadTask(private val req: DownloaderRequest, private val httpClient: HttpClient) {

    suspend fun run(
        onStart: () -> Unit = {},
        onProgress: (value: Int) -> Unit = { _ -> },
        onPause: () -> Unit = {},
        onCompleted: () -> Unit = {},
        onError: (error: String) -> Unit = { _ -> }
    ) {
        withContext(Dispatchers.IO) {
            onStart()

            //Use of HttpClient
            httpClient.connect(request = req)
            // Simulate read data from interpret
            for (i in 1..100) {
                delay(100)
                onProgress(i)
            }
            onCompleted()
        }
    }
}
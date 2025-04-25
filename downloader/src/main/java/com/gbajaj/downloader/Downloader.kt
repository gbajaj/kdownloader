package com.gbajaj.downloader

class Downloader private constructor(private val config: DownloaderConfig) {
    val reqQueue = DownloaderRequestQueue(DownloadDispatcher(config.httpClient))

    companion object {
        fun create(config: DownloaderConfig = DownloaderConfig()): Downloader {
            return Downloader(config)
        }
    }

    fun newReqBuilder(url: String, dirPah: String, fileName: String): DownloaderRequest.Builder {
        return DownloaderRequest.Builder(url, dirPah, fileName).readTimeOut(config.readTimeOut)
            .connectTimeOut(config.connectTimeOut)
    }

    fun enqueue(
        request: DownloaderRequest,
        onStart: () -> Unit = {},
        onProgress: (value: Int) -> Unit = { _ -> },
        onPause: () -> Unit = {},
        onCancel: () -> Unit = {},
        onCompleted: () -> Unit = {},
        onError: (error: String) -> Unit = { _ -> }
    ): Int {
        request.onStart = onStart
        request.onProgress = onProgress
        request.onPause = onPause
        request.onCancel = onCancel
        request.onCompleted = onCompleted
        request.onError = onError
        return reqQueue.enqueue(request)
    }

    fun pause(id: Int) = reqQueue.pause(id)

    fun resume(id: Int) = reqQueue.resume(id)

    fun cancel(id: Int) = reqQueue.cancel(id)

    fun cancel(tag: String) = reqQueue.cancel(tag)

    fun cancelAll() = reqQueue.cancelAll()
}
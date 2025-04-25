package com.gbajaj.downloader

import kotlinx.coroutines.Job

class DownloaderRequest(
    internal val url: String,
    internal val tag: String?,
    internal val dirPath: String,
    internal val downloadId: Int,
    internal val fileName: String,
    internal val readTimeOut: Int,
    internal val connectTimeOut: Int,
) {
    internal var totalBytes: Long = 0
    internal var downloadedBytes: Long = 0
    internal lateinit var job: Job
    internal lateinit var onStart: () -> Unit
    internal lateinit var onProgress: (value: Int) -> Unit
    internal lateinit var onPause: () -> Unit
    internal lateinit var onCompleted: () -> Unit
    internal lateinit var onError: (error: String) -> Unit

    data class Builder(
        private val url: String,
        private val dirPath: String,
        private val fileName: String
    ) {
        private var tag: String? = null
        private var readTimeOut: Int = 0
        private var connectTimeOut: Int = 0

        fun tag(tag: String): Builder {
            this.tag = tag
            return this
        }

        fun readTimeOut(timeOut: Int): Builder {
            this.readTimeOut = timeOut
            return this
        }

        fun connectTimeOut(connectTimeOut: Int): Builder {
            this.connectTimeOut = connectTimeOut
            return this
        }

        fun build(): DownloaderRequest {
            return DownloaderRequest(
                url = url,
                tag = tag,
                dirPath = dirPath,
                downloadId = getUniqueId(url, dirPath, fileName),
                fileName = fileName,
                readTimeOut = readTimeOut,
                connectTimeOut = connectTimeOut

            )
        }
    }
}
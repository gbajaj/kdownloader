package com.gbajaj.downloader

interface HttpClient {
    fun connect(request: DownloaderRequest)
}
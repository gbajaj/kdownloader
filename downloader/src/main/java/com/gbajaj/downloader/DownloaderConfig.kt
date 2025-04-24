package com.gbajaj.downloader


data class DownloaderConfig(
    val httpClient: HttpClient = DefaultHttpClient(),
    val connectTimeOut: Int = Constants.DEFAULT_CONNECT_TIMEOUT_MILLS,
    val readTimeOut: Int = Constants.DEFAULT_READ_TIMEOUT_MILLS
)
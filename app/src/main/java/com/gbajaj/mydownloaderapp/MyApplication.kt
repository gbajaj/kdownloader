package com.gbajaj.mydownloaderapp

import android.app.Application
import com.gbajaj.downloader.Downloader

class MyApplication: Application() {

    lateinit var downloader: Downloader

    override fun onCreate() {
        super.onCreate()
        downloader = Downloader.create()
    }
}
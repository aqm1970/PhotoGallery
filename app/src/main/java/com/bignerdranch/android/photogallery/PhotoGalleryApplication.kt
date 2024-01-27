package com.bignerdranch.android.photogallery

import android.app.Application

class PhotoGalleryApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        PreferenceRepository.initialize(this)
    }
}
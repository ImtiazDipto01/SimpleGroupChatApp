package com.example.myapplication.utils

import android.app.Application
import com.sendbird.android.SendBird

class App : Application() {

    val APP_ID = "91AABEDF-03D9-44ED-88AB-0724BC215192"

    companion object{
        val SENDER_MSG = 1
        val RECEIVER_MSG = 2
        val SENDER_STICKER = 3
        val RECEIVER_STICKER = 4
    }

    override fun onCreate() {
        super.onCreate()
        SendBird.init(APP_ID, applicationContext)
    }
}
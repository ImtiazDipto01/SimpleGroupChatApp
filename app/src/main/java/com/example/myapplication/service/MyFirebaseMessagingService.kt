package com.example.myapplication.service

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.sendbird.android.shadow.com.google.gson.JsonElement
import com.sendbird.android.shadow.com.google.gson.JsonParser


class MyFirebaseMessagingService : FirebaseMessagingService() {

    val TAG = "FirebaseMessaging"

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.e(TAG, token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.e(TAG, "onMessageReceived : Yes")
        /*val message: String? = remoteMessage.getData().get("message")
        val payload: JsonElement = JsonParser().parse(remoteMessage.getData().get("sendbird"))
        sendNotification(message, payload)*/
    }

    private fun sendNotification(message: String?, payload: JsonElement) {
        val payLoadAsJsonObject = payload.asJsonObject
        Log.e(TAG, "Check Message : ${payLoadAsJsonObject.get("message")}")
    }
}
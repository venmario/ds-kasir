package com.example.restoapp.firebase

import android.content.Intent
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.restoapp.model.Notification
import com.example.restoapp.receiver.NotificationReceiver
import com.example.restoapp.view.HomeFragment
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import java.util.Date

class MyFirebaseMessagingService:FirebaseMessagingService() {

    companion object{
        val NOTIFICATION_RECEIVED = "NOTIFICATION_RECEIVED"
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        Log.d("message",message.toString())
        Log.d("message", "message type : ${message.messageType}")
        Log.d("message", "transaction id :" + message.data["transaction_id"].toString())
        val transactionId = message.data["transaction_id"].toString()
        val title = message.notification?.title
        val body = message.notification?.body
        val notification = Notification(transactionId,title!!,body!!,Date().time.toString(), false)
        val intent = Intent(NOTIFICATION_RECEIVED)
        intent.putExtra("notification", Gson().toJson(notification))
        Log.d("MyFirebaseMessagingService", "Sending broadcast with notification: $notification")
        LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent)
//        sendBroadcast(intent)
    }
}

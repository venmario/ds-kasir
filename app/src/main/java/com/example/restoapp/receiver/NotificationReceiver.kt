package com.example.restoapp.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import com.example.restoapp.viewmodel.NotificationViewModel
import com.example.restoapp.application.MyApplication
import com.example.restoapp.model.Notification
import com.google.gson.Gson

class NotificationReceiver:BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("NotificationReceiver", "onReceive triggered")
        val notificationJson = intent?.getStringExtra("notification")
        if (notificationJson != null) {
            Log.d("NotificationReceiver", "Received notificationJson: $notificationJson")
            val notification = Gson().fromJson(notificationJson, Notification::class.java)
            val application = context?.applicationContext as MyApplication
            val notificationViewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(application).create(
                NotificationViewModel::class.java)
            notificationViewModel.addNotification(notification)
            Log.d("NotificationReceiver", "Notification added to ViewModel: $notification")
        }
    }
}
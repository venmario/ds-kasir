package com.example.restoapp.viewmodel

import android.app.Application
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.restoapp.application.MyApplication
import com.example.restoapp.model.Notification
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


class NotificationViewModel(application: Application): AndroidViewModel(application) {
    val notificationsLD = MutableLiveData<List<Notification>>()

    fun getAll(){
        val sharedPreferences: SharedPreferences =
            MyApplication.getAppContext().getSharedPreferences(MyApplication.getAppContext().packageName, MODE_PRIVATE)
        val notificationJson = sharedPreferences.getString("notifications","")
        Log.d("notification json",notificationJson.toString())
        val sType = object : TypeToken<List<Notification>>(){}.type
        val result = Gson().fromJson<List<Notification>>(notificationJson,sType)
        if(result != null){
            notificationsLD.value = result as ArrayList<Notification>
        }
    }

    fun addNotification(notification: Notification){
        val sharedPreferences: SharedPreferences =
            MyApplication.getAppContext().getSharedPreferences(MyApplication.getAppContext().packageName, MODE_PRIVATE)
        val notificationJson = sharedPreferences.getString("notifications","")
        val sType = object : TypeToken<List<Notification>>(){}.type
        val result = Gson().fromJson<List<Notification>>(notificationJson,sType)
        val notifications:ArrayList<Notification>
        if(result != null){
            notifications = result as ArrayList<Notification>
        }else{
            notifications = arrayListOf()
        }
        notifications.add(notification)
        Log.d("add notification", "notif added")
        notificationsLD.postValue(notifications)
        val editor:SharedPreferences.Editor = sharedPreferences.edit()
        editor.putString("notifications", Gson().toJson(notifications))
        editor.apply()
    }
}
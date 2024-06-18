package com.example.restoapp.util

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import com.example.restoapp.view.LoginFragment
import com.example.restoapp.view.MainActivity
import com.example.restoapp.R
import com.example.restoapp.application.MyApplication
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import java.text.NumberFormat
import java.util.Locale

fun ImageView.loadImage(url: String?, progressBar: ProgressBar){
    Picasso.get()
        .load(url)
        .centerCrop()
        .resize(120,120)
        .error(R.drawable.ic_baseline_error_24)
        .into(this, object: Callback {
            override fun onSuccess() {
                progressBar.visibility = View.GONE
            }
            override fun onError(e: Exception?) {
                progressBar.visibility = View.GONE
            }
        })
}

fun clearNotifications(){
    val shared = MyApplication.getAppContext().getSharedPreferences(MyApplication.getAppContext().packageName, Context.MODE_PRIVATE)
    val editor: SharedPreferences.Editor = shared.edit()
    editor.putString("notifications","")
    editor.apply()
}
fun setFcmTokens(activity: Activity, newFcmToken:String){
    Log.d("new fcm token", newFcmToken)
    val shared = activity.getSharedPreferences(activity.packageName, Context.MODE_PRIVATE)
    val editor: SharedPreferences.Editor = shared.edit()
    val (oldFcmToken,currentFcmToken) = getFcmTokens(activity)
    oldFcmToken?.let{
        if (oldFcmToken.isEmpty()){
            editor.putString(MainActivity.OLD_FCM_TOKEN,newFcmToken)
//            editor.putString(MainActivity.CURRENT_FCM_TOKEN,newFcmToken)
        }else{
            editor.putString(MainActivity.OLD_FCM_TOKEN,currentFcmToken)
        }
    }
    if (newFcmToken != currentFcmToken){
        Log.d("new fcm token", newFcmToken)
        Log.d("current fcm token", currentFcmToken!!)
        editor.putString(MainActivity.CURRENT_FCM_TOKEN,newFcmToken)
    }
    editor.apply()
}
fun getFcmTokens(activity: Activity):List<String?>{
    val shared = activity.getSharedPreferences(activity.packageName, Context.MODE_PRIVATE)
    val oldFcmToken = shared.getString(MainActivity.OLD_FCM_TOKEN, "")
    val currentFcmToken = shared.getString(MainActivity.CURRENT_FCM_TOKEN,"")
    Log.d("util", "oldFcmToken : $oldFcmToken")
    Log.d("util", "currentFcmToken : $currentFcmToken")
    return listOf( oldFcmToken,currentFcmToken )
}

fun getAccToken(activity: Activity):List<String?>{
    val shared = activity.getSharedPreferences(activity.packageName, Context.MODE_PRIVATE)
    val accToken = shared.getString(LoginFragment.ACCESS_TOKEN, "")
    val username = shared.getString(LoginFragment.USERNAME,"")
    Log.d("util", "acc token : $accToken")
    Log.d("util", "username : $username")
    return listOf( accToken,username )
}

fun setNewAccToken(activity: Activity,newToken:String, username:String){
    val shared = activity.getSharedPreferences(activity.packageName, Context.MODE_PRIVATE)
    val editor: SharedPreferences.Editor = shared.edit()
    editor.putString(LoginFragment.ACCESS_TOKEN,newToken)
    editor.putString(LoginFragment.USERNAME,username)
    editor.apply()
}
fun getAuthorizationHeaders(activity: Activity):HashMap<String,String> {
    val (token) = getAccToken(activity)
    val headers = HashMap<String,String>()
    headers["Authorization"] = "Bearer $token"
    return  headers
}

fun convertToRupiah(amount: Int): String {
    val localeID = Locale("in", "ID")
    val numberFormat = NumberFormat.getCurrencyInstance(localeID)
    numberFormat.maximumFractionDigits = 0 // No decimal places
    return numberFormat.format(amount).replace("Rp", "Rp ")
}

fun showToast(message: String,applicationContext:Context) {
    Toast.makeText(applicationContext, message, Toast.LENGTH_LONG).show()
}
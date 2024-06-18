package com.example.restoapp.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.restoapp.global.GlobalData
import com.example.restoapp.model.LoginResponse
import com.example.restoapp.model.ServiceResult
import com.example.restoapp.model.User
import com.google.gson.Gson
import com.midtrans.sdk.corekit.internal.util.SingleLiveEvent
import org.json.JSONObject

class LoginViewModel(application: Application): AndroidViewModel(application) {
    val serviceResult = SingleLiveEvent<ServiceResult<LoginResponse>>()

    val cashierUrl = "${GlobalData.apiUrl}/cashier"
    val TAG = "volleyTag"
    private var queue: RequestQueue? = null

    fun login(username:String,password:String,oldFcmToken:String,currentFcmToken:String){
        val url = "$cashierUrl/login"
        queue = Volley.newRequestQueue(getApplication())
        val stringRequest = object: StringRequest(
            Method.POST, url,{
                val result = JSONObject(it)
                Log.d("SIGN IN", "sign in response : $it")
                val status = result.getBoolean("isSuccess")
                if (status){
                    val userJson = result.getJSONObject("data").toString()
                    val user = Gson().fromJson(userJson, User::class.java)
                    val accToken = result.getString("token")
                    serviceResult.value = ServiceResult(status,null,LoginResponse(user,accToken))
                    Log.d("SIGN IN", "acc token : $accToken")
                }else{
                    val errMsg = result.getString("errorMessage")
                    serviceResult.value = ServiceResult(status,errMsg,null)
                    Log.d("SIGN IN", "err msg : $errMsg")
                }
            },{
                if (it.message!=null){
                    Log.d("sign in error", it.message!!)
                }
            }
        ){
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String,String>()
                params["username"] = username
                params["password"] = password
                params["oldFcmToken"] = oldFcmToken
                params["currentFcmToken"] = currentFcmToken
                return params
            }
        }
        stringRequest.tag = TAG
        queue?.add(stringRequest)
    }
}
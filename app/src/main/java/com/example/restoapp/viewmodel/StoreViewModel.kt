package com.example.restoapp.viewmodel

import android.app.Activity
import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.restoapp.global.GlobalData
import com.example.restoapp.model.Income
import com.example.restoapp.model.ServiceResult
import com.example.restoapp.model.Store
import com.example.restoapp.model.User
import com.example.restoapp.util.getAuthorizationHeaders
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONObject

class StoreViewModel(application: Application): AndroidViewModel(application) {
    private val mutableStoreTime = MutableLiveData<ServiceResult<Store>>()
    val store: LiveData<ServiceResult<Store>> get() = mutableStoreTime

    private val mutableSetStoreTime = MutableLiveData<ServiceResult<Void>>()
    val setStore: LiveData<ServiceResult<Void>> get() = mutableSetStoreTime

    val storeUrl = "${GlobalData.apiUrl}/store"
    val TAG = "volleyTag"
    private var queue: RequestQueue? = null
    fun setOpenClose(activity: Activity, store:Store){
        val url = "$storeUrl/setOpenCloseHour/${store.id}"
        queue = Volley.newRequestQueue(getApplication())
        val stringRequest = object: StringRequest(
            Method.POST, url, {
                val result = JSONObject(it)
                Log.d("set store", "set store response : $it")
                val isSuccess = result.getBoolean("isSuccess")
                if(isSuccess){
                    mutableSetStoreTime.value = ServiceResult(isSuccess,null, null )
                }else{
                    val errorMessage = result.getString("errorMessage")
                    mutableSetStoreTime.value = ServiceResult(isSuccess,errorMessage, null)
                }
            },{
                mutableSetStoreTime.value = ServiceResult(false,it.message, null)
            }
        ){
            override fun getParams(): MutableMap<String, String>? {
                val body = HashMap<String, String>()
                body["open"] = store.open
                body["close"] = store.close
                return body
            }
            override fun getHeaders(): MutableMap<String, String> {
                return getAuthorizationHeaders(activity)
            }
        }
        stringRequest.tag = TAG
        queue?.add(stringRequest)
    }

    fun getOpenClose(activity: Activity){
        val url = "$storeUrl/getOpenCloseHour"
        queue = Volley.newRequestQueue(getApplication())
        val stringRequest = object: StringRequest(
            Method.GET, url, {
                val result = JSONObject(it)
                Log.d("get store", "get store response : $it")
                val isSuccess = result.getBoolean("isSuccess")
                if(isSuccess){
                    val data = result.getString("data")
                    val store = Gson().fromJson(data, Store::class.java)
                    mutableStoreTime.value = ServiceResult(isSuccess,null, store )
                }else{
                    val errorMessage = result.getString("errorMessage")
                    mutableStoreTime.value = ServiceResult(isSuccess,errorMessage, null)
                }
            },{
                mutableStoreTime.value = ServiceResult(false,it.message, null)
            }
        ){
            override fun getHeaders(): MutableMap<String, String> {
                return getAuthorizationHeaders(activity)
            }
        }
        stringRequest.tag = TAG
        queue?.add(stringRequest)
    }

    fun setFalseSetStore() {
        mutableSetStoreTime.value = ServiceResult(false,null,null)
    }
}
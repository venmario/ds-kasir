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
import com.example.restoapp.model.Category
import com.example.restoapp.model.Income
import com.example.restoapp.model.ServiceResult
import com.example.restoapp.util.getAuthorizationHeaders
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONObject

class IncomeViewModel(application: Application): AndroidViewModel(application) {
    private val mutableIncome = MutableLiveData<ServiceResult<ArrayList<Income>>>()
    val incomes: LiveData<ServiceResult<ArrayList<Income>>> get() = mutableIncome

    private val incomeUrl = "${GlobalData.apiUrl}/cashier/income"
    private val TAG = "volleyTag"
    private var queue: RequestQueue? = null
    fun getIncome(activity: Activity, filter:String){
        Log.d("filter",filter)
        val url = "$incomeUrl/getIncome?filter=${filter}"
        queue = Volley.newRequestQueue(getApplication())
        val stringRequest = object: StringRequest(
            Method.GET, url, {
                val result = JSONObject(it)
                Log.d("get all income", "get all income response : $it")
                val isSuccess = result.getBoolean("isSuccess")
                if(isSuccess){
                    val data = result.getString("data")
                    val sType = object : TypeToken<List<Income>>(){}.type
                    val incomes = Gson().fromJson<List<Income>>(data,sType)
                    mutableIncome.value = ServiceResult(isSuccess,null, incomes as ArrayList)
                }else{
                    val errorMessage = result.getString("errorMessage")
                    mutableIncome.value = ServiceResult(isSuccess,errorMessage, null)
                }
            },{
                mutableIncome.value = ServiceResult(false,it.message, null)
            }
        ){
            override fun getHeaders(): MutableMap<String, String> {
                return getAuthorizationHeaders(activity)
            }
        }
        stringRequest.tag = TAG
        queue?.add(stringRequest)
    }
}
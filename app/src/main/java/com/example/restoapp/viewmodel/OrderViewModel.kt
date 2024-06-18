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
import com.example.restoapp.model.Order
import com.example.restoapp.model.OrderDetail
import com.example.restoapp.model.ServiceResult
import com.example.restoapp.model.Status
import com.example.restoapp.util.getAuthorizationHeaders
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.midtrans.sdk.corekit.internal.util.SingleLiveEvent
import org.json.JSONObject

class OrderViewModel(application: Application): AndroidViewModel(application) {
    val serviceResultOrderList= SingleLiveEvent<ServiceResult<ArrayList<Status>>>()

    private val mutableOrderConfirm = MutableLiveData<Boolean>()
    val confirmedOrder: LiveData<Boolean> get() = mutableOrderConfirm

    private val mutableOrderPickup = MutableLiveData<Boolean>()
    val pickedUpOrder: LiveData<Boolean> get() = mutableOrderPickup

    private val mutableOrderCollect = MutableLiveData<Boolean>()
    val collectedOrder: LiveData<Boolean> get() = mutableOrderCollect

    private val mutableSelectedOrder = MutableLiveData<Order>()
    val selectedOrder: LiveData<Order> get() = mutableSelectedOrder

    val cashierUrl = "${GlobalData.apiUrl}/cashier"
    val TAG = "volleyTag"
    private var queue: RequestQueue? = null

    fun setConfirOrderFalse(){
        mutableOrderConfirm.value = false
    }

    fun getAllStatus(activity: Activity){
        val url = "$cashierUrl/getTodaysOrder"
        queue = Volley.newRequestQueue(getApplication())
        val stringRequest = object: StringRequest(
            Method.GET, url,{
                val result = JSONObject(it)
                Log.d("get all status", "get all status response : $it")
                val status = result.getBoolean("isSuccess")
                if (status){
                    val data = result.getString("data")
                    val sType = object : TypeToken<List<Status>>(){}.type
                    val statuses = Gson().fromJson<List<Status>>(data,sType)
                    Log.d("orders", it.toString())
                    serviceResultOrderList.value = ServiceResult(status,null,statuses as ArrayList)
                }else{
                    val errMsg = result.getString("errorMessage")
                    serviceResultOrderList.value = ServiceResult(status,errMsg,null)
                    Log.d("orders", "err msg : $errMsg")
                }
            },{
                if (it.message!=null){
                    Log.d("orders", it.message!!)
                }
            }
        ){
            override fun getHeaders(): MutableMap<String, String> {
                return getAuthorizationHeaders(activity)
            }
        }
        stringRequest.tag = TAG
        queue?.add(stringRequest)
    }

    fun selectOrder(order:Order,activity: Activity){
        Log.d("select order","select order")
        val url = "$cashierUrl/getOrderById/${order.orderId}"
        queue = Volley.newRequestQueue(getApplication())
        val stringRequest = object: StringRequest(
            Method.GET, url,{
                val result = JSONObject(it)
                Log.d("select order vm", "select order vm : $it")
                val status = result.getBoolean("isSuccess")
                if (status){
                    val data = result.getString("data")
                    val sType = object : TypeToken<List<OrderDetail>>(){}.type
                    val orderDetails = Gson().fromJson<List<OrderDetail>>(data,sType)
                    Log.d("orderdetail", it.toString())
                    order.orderDetails = orderDetails as ArrayList<OrderDetail>
                    mutableSelectedOrder.value = order
                }
            },{
                if (it.message!=null){
                    Log.d("orderdetail error", it.message!!)
                }
            }
        ){
            override fun getHeaders(): MutableMap<String, String> {
                return getAuthorizationHeaders(activity)
            }
        }
        stringRequest.tag = TAG
        queue?.add(stringRequest)
    }

    fun confirmOrder(orderId:String,estimationTime:String, activity: Activity){
        Log.d("estimation Time",estimationTime)
        val url = "$cashierUrl/confirmOrder/${orderId}"
        queue = Volley.newRequestQueue(getApplication())
        val stringRequest = object: StringRequest(
            Method.POST, url,{
                val result = JSONObject(it)
                Log.d("Confirm Order", "sign in response : $it")
                val status = result.getBoolean("isSuccess")
                if (status){
                    mutableOrderConfirm.value = true
                }else{
                    mutableOrderConfirm.value = false
                }
            },{
                if (it.message!=null){
                    Log.d("orderdetail error", it.message!!)
                }
            }
        ){
            override fun getHeaders(): MutableMap<String, String> {
                return getAuthorizationHeaders(activity)
            }

            override fun getParams(): MutableMap<String, String> {
                val hashMap = HashMap<String,String>()
                hashMap["estimation"] = estimationTime
                return hashMap
            }
        }
        stringRequest.tag = TAG
        queue?.add(stringRequest)
    }

    fun pickUpOrder(orderId:String,activity: Activity){
        Log.d("select order","select order")
        val url = "$cashierUrl/orderReady/${orderId}"
        queue = Volley.newRequestQueue(getApplication())
        val stringRequest = object: StringRequest(
            Method.GET, url,{
                val result = JSONObject(it)
                Log.d("SIGN IN", "sign in response : $it")
                val status = result.getBoolean("isSuccess")
                if (status){
                    mutableOrderPickup.value = true
                }else{
                    mutableOrderPickup.value = false
                }
            },{
                if (it.message!=null){
                    Log.d("orderdetail error", it.message!!)
                }
            }
        ){
            override fun getHeaders(): MutableMap<String, String> {
                return getAuthorizationHeaders(activity)
            }
        }
        stringRequest.tag = TAG
        queue?.add(stringRequest)
    }

    fun collectOrder(orderId:String,activity: Activity){
        Log.d("select order","select order")
        val url = "$cashierUrl/collectOrder/${orderId}"
        queue = Volley.newRequestQueue(getApplication())
        val stringRequest = object: StringRequest(
            Method.GET, url,{
                val result = JSONObject(it)
                Log.d("SIGN IN", "sign in response : $it")
                val status = result.getBoolean("isSuccess")
                if (status){
                    mutableOrderCollect.value = true
                }else{
                    mutableOrderCollect.value = false
                }
            },{
                if (it.message!=null){
                    Log.d("orderdetail error", it.message!!)
                }
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
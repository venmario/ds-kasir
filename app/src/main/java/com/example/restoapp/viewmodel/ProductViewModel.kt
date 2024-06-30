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
import com.example.restoapp.model.Order
import com.example.restoapp.model.Product
import com.example.restoapp.model.ServiceResult
import com.example.restoapp.model.Status
import com.example.restoapp.util.getAuthorizationHeaders
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONObject

class ProductViewModel(application: Application): AndroidViewModel(application) {
    private val mutableCategoriesWithProduct = MutableLiveData<ServiceResult<ArrayList<Category>>>()
    val categoriesWithProduct: LiveData<ServiceResult<ArrayList<Category>>> get() = mutableCategoriesWithProduct

    private val mutableCategories = MutableLiveData<ArrayList<Category>>()
    val categories: LiveData<ArrayList<Category>> get() = mutableCategories

    private val mutableSelectedProduct = MutableLiveData<Product?>()
    val selectedProduct: LiveData<Product?> get() = mutableSelectedProduct

    private val mutableCreatedProduct = MutableLiveData<ServiceResult<Void>>()
    val createdProduct: LiveData<ServiceResult<Void>> get() = mutableCreatedProduct

    private val mutableUpdatedProduct = MutableLiveData<ServiceResult<Void>>()
    val updatedProduct: LiveData<ServiceResult<Void>> get() = mutableUpdatedProduct

    private val mutableDeletedProduct = MutableLiveData<ServiceResult<Void>>()
    val deletedProduct: LiveData<ServiceResult<Void>> get() = mutableDeletedProduct

    val productUrl = "${GlobalData.apiUrl}/cashier/product"
    val TAG = "volleyTag"
    private var queue: RequestQueue? = null

    fun selectProduct(product: Product?){
        mutableSelectedProduct.value = product
    }

    fun populateCategories(categories:ArrayList<Category>){
        mutableCategories.value = categories
    }
    fun getAllProducts(activity: Activity){
        val url = "$productUrl/getAllProduct"
        queue = Volley.newRequestQueue(getApplication())
        val stringRequest = object: StringRequest(
            Method.GET, url, {
                val result = JSONObject(it)
                Log.d("get all product", "get all product response : $it")
                val isSuccess = result.getBoolean("isSuccess")
                if(isSuccess){
                    val data = result.getString("data")
                    val sType = object : TypeToken<List<Category>>(){}.type
                    val products = Gson().fromJson<List<Category>>(data,sType)
                    mutableCategoriesWithProduct.value = ServiceResult(isSuccess,null, products as ArrayList)
                }else{
                    val errorMessage = result.getString("errorMessage")
                    mutableCategoriesWithProduct.value = ServiceResult(isSuccess,errorMessage, null)
                }
            },{
                mutableCategoriesWithProduct.value = ServiceResult(false,it.message, null)
            }
        ){
            override fun getHeaders(): MutableMap<String, String> {
                return getAuthorizationHeaders(activity)
            }
        }
        stringRequest.tag = TAG
        queue?.add(stringRequest)
    }

    fun setFalseCreateProduct(){
        mutableCreatedProduct.value = ServiceResult(false,null,null)
    }
    fun setFalseUpdateProduct(){
        mutableUpdatedProduct.value = ServiceResult(false,null,null)
    }
    fun setFalseDeleteProduct(){
        mutableDeletedProduct.value = ServiceResult(false,null,null)
    }
    fun createProduct(activity: Activity, product: Product){
        val url = "$productUrl/createProduct"
        queue = Volley.newRequestQueue(getApplication())
        val stringRequest = object: StringRequest(
            Method.POST, url, {
                val result = JSONObject(it)
                Log.d("created product", "created product response : $it")
                val isSuccess = result.getBoolean("isSuccess")
                if(isSuccess){
                    mutableCreatedProduct.value = ServiceResult(isSuccess,null, null)
                }else{
                    val errorMessage = result.getString("errorMessage")
                    mutableCreatedProduct.value = ServiceResult(isSuccess,errorMessage, null)
                }
            },{
                    mutableCreatedProduct.value = ServiceResult(false,it.message, null)
            }
        ){
            override fun getParams(): MutableMap<String, String> {
                val body = HashMap<String,String>()
                if (product.image != null){
                    body["image"] = product.image!!
                }
                if (product.name != null){
                    body["name"] = product.name!!
                }
                if (product.description != null){
                    body["description"] = product.description!!
                }
                if (product.available != null){
                    body["available"] = product.available.toString()
                }
                if (product.price != null){
                    body["price"] = product.price.toString()
                    body["poin"] = product.poin.toString()
                }
                if (product.categoryId != null){
                    body["category_id"] = product.categoryId.toString()
                }
                return body
            }
            override fun getHeaders(): MutableMap<String, String> {
                return getAuthorizationHeaders(activity)
            }
        }
        stringRequest.tag = TAG
        queue?.add(stringRequest)
    }

    fun updateProduct(activity: Activity, product: Product){
        val url = "$productUrl/updateProduct/${product.id}"
        queue = Volley.newRequestQueue(getApplication())
        val stringRequest = object: StringRequest(
            Method.PUT, url, {
                val result = JSONObject(it)
                Log.d("updated product", "updated product response : $it")
                val isSuccess = result.getBoolean("isSuccess")
                if(isSuccess){
                    mutableUpdatedProduct.value = ServiceResult(isSuccess,null, null)
                }else{
                    val errorMessage = result.getString("errorMessage")
                    mutableUpdatedProduct.value = ServiceResult(isSuccess,errorMessage, null)
                }
            },{
                mutableUpdatedProduct.value = ServiceResult(false,it.message, null)
            }
        ){
            override fun getParams(): MutableMap<String, String> {
                val body = HashMap<String,String>()
                if (product.image != null){
                    body["image"] = product.image!!
                }
                if (product.name != null){
                    body["name"] = product.name!!
                }
                if (product.description != null){
                    body["description"] = product.description!!
                }
                if (product.available != null){
                    body["available"] = product.available.toString()
                }
                if (product.price != null){
                    body["price"] = product.price.toString()
                    body["poin"] = product.poin.toString()
                }
                if (product.categoryId != null){
                    body["category_id"] = product.categoryId.toString()
                }
                return body
            }
            override fun getHeaders(): MutableMap<String, String> {
                return getAuthorizationHeaders(activity)
            }
        }
        stringRequest.tag = TAG
        queue?.add(stringRequest)
    }

    fun deleteProdcut(activity: Activity, productId:Int){
        val url = "$productUrl/deleteProduct/${productId}"
        queue = Volley.newRequestQueue(getApplication())
        val stringRequest = object: StringRequest(
            Method.DELETE, url, {
                val result = JSONObject(it)
                Log.d("deleted product", "deleted product response : $it")
                val isSuccess = result.getBoolean("isSuccess")
                if(isSuccess){
                    mutableDeletedProduct.value = ServiceResult(isSuccess,null, null)
                }else{
                    val errorMessage = result.getString("errorMessage")
                    mutableDeletedProduct.value = ServiceResult(isSuccess,errorMessage, null)
                }
            },{
                mutableDeletedProduct.value = ServiceResult(false,it.message, null)
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
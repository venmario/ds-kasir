package com.example.restoapp.model

import com.google.gson.annotations.SerializedName

data class Notification(
    val transactionId: String,
    val title:String,
    val body:String,
    val date:String,
    var isRead:Boolean
)

data class User(
    var id:Int?,
    var username:String?,
    var firstname:String?,
    var lastname:String?,
    var phonenumber:String?,
    var email:String?,
    var role:String
)

data class Status(
    var status:String,
    var orders:ArrayList<Order>
)

data class Order(
    @SerializedName("order_id")
    var orderId:String,
    var fullname:String,
    @SerializedName("total_item")
    var totalItem:Int,
    @SerializedName("grand_total")
    var grandTotal:Int,
    var status:String,
    var bank:String?,
    @SerializedName("payment_type")
    val paymentType: String,
    var orderDetails: ArrayList<OrderDetail>?
)

data class LoginResponse(
    val user:User,
    val token:String?
)

data class LogoutResponse(
    var isSuccess:Boolean,
    var errorMessage: String?
)

data class OrderDetail(
    val product:String,
    val quantity:Int,
    val price:Int,
    val note:String?,
)

data class ServiceResult<T>(
    var isSuccess: Boolean,
    var errorMessage: String?,
    var data:T?
)

data class Product(
    var id:Int?,
    var name:String?,
    var description:String?,
    var image:String?,
    var available:Boolean?,
    @SerializedName("category_id")
    var categoryId:Int?,
    var price:Int?,
    var poin:Int?
){
    constructor(): this(
        null,null,null,null,null,null,null,null
    )
}
data class Category(
    var id:Int,
    var name:String,
    var product:ArrayList<Product>?
){
    override fun toString(): String {
        return this.name
    }
}

data class Income(
    val income:Int,
    val totalTransaction:Int,
    val date:String
)

data class Store(
    var id:Int,
    var open:String,
    var close:String
)
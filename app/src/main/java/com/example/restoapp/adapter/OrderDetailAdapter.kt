package com.example.restoapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.restoapp.databinding.OrderDetailItemBinding
import com.example.restoapp.model.OrderDetail
import com.example.restoapp.model.Status
import com.example.restoapp.util.convertToRupiah

class OrderDetailAdapter(private val orderDetailList:ArrayList<OrderDetail>):RecyclerView.Adapter<OrderDetailAdapter.OrderDetailViewHolder>() {
    class OrderDetailViewHolder(var binding: OrderDetailItemBinding):RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderDetailViewHolder {
        val binding = OrderDetailItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OrderDetailViewHolder(binding)
    }

    override fun getItemCount(): Int = orderDetailList.size

    override fun onBindViewHolder(holder: OrderDetailViewHolder, position: Int) {
        val orderDetail = orderDetailList[position]
        with(holder.binding){
            textProductName?.text = orderDetail.product
            textQuantity?.text = "X${orderDetail.quantity}"
            textPrice?.text = convertToRupiah(orderDetail.price)
            if(orderDetail.note != null){
                textNote?.text = "Note : ${orderDetail.note}"
            }else{
                textNote?.visibility = View.GONE
            }
        }
    }

    fun updateOrderDetailList(newestOrderDetailList: ArrayList<OrderDetail>){
        orderDetailList.clear()
        orderDetailList.addAll(newestOrderDetailList)
        notifyDataSetChanged()
    }
}
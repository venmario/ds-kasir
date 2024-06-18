package com.duniasteak.restoapp.adapter.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.RecyclerView
import com.example.restoapp.databinding.OrderCardBinding
import com.example.restoapp.model.Order
import com.example.restoapp.util.convertToRupiah
import com.example.restoapp.viewmodel.OrderViewModel

class OrderListAdapter(private val orderList:ArrayList<Order>,private val listener:IOrderListListener):RecyclerView.Adapter<OrderListAdapter.OrderListViewHolder>() {
    class OrderListViewHolder(var binding: OrderCardBinding): RecyclerView.ViewHolder(binding.root)

    interface IOrderListListener{
        fun selectOrder(order:Order)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderListViewHolder {
        val binding = OrderCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OrderListViewHolder(binding)
    }

    override fun getItemCount(): Int = orderList.size

    override fun onBindViewHolder(holder: OrderListViewHolder, position: Int) {
        val order = orderList[position]
        with(holder.binding){
            textOrderId?.text = "ORDER #${order.orderId}"
            textOrderer?.text = "Customer Name : ${order.fullname}"
            textTotalItem?.text = "Total Item : ${order.totalItem}"
            textGrandTotal?.text = "Total : ${convertToRupiah( order.grandTotal)}"
            orderCard?.setOnClickListener() {
                listener.selectOrder(order)
            }
        }
    }


}
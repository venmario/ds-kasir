package com.duniasteak.restoapp.adapter.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.restoapp.databinding.RecviewStatusBinding
import com.example.restoapp.model.Status
import com.example.restoapp.view.SpacesItemDecoration
import com.example.restoapp.viewmodel.OrderViewModel

class StatusListAdapter(private val statusList:ArrayList<Status>, private val listener: OrderListAdapter.IOrderListListener):RecyclerView.Adapter<StatusListAdapter.StatusListViewHolder>() {
    class StatusListViewHolder(var binding: RecviewStatusBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StatusListViewHolder {
        val binding = RecviewStatusBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StatusListViewHolder(binding)
    }

    override fun getItemCount() = statusList.size

    override fun onBindViewHolder(holder: StatusListViewHolder, position: Int) {
        val objStatus = statusList[position]
        with(holder.binding){
            textStatusOrder?.text = objStatus.status
            val orderListAdapter = OrderListAdapter(objStatus.orders, listener )
            recViewOrder?.layoutManager = GridLayoutManager(this.root.context, 3)
            recViewOrder?.adapter = orderListAdapter
//            recViewOrder?.addItemDecoration(SpacesItemDecoration(10))
            recViewOrder?.isNestedScrollingEnabled = false
        }
    }

    fun updateStatusList(newestStatusList: ArrayList<Status>){
        statusList.clear()
        statusList.addAll(newestStatusList)
        notifyDataSetChanged()
    }
}
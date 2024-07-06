package com.example.restoapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.restoapp.databinding.IncomeCardBinding
import com.example.restoapp.databinding.ProductCardBinding
import com.example.restoapp.model.Category
import com.example.restoapp.model.Income
import com.example.restoapp.model.Product
import com.example.restoapp.util.convertToRupiah

class IncomeAdapter(private val incomeList:ArrayList<Income>):RecyclerView.Adapter<IncomeAdapter.IncomeViewHolder>() {

    class IncomeViewHolder(var binding: IncomeCardBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IncomeViewHolder {
        val binding = IncomeCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return IncomeViewHolder(binding)
    }

    override fun getItemCount(): Int = incomeList.size

    override fun onBindViewHolder(holder: IncomeViewHolder, position: Int) {
        val income = incomeList[position]
        with(holder.binding){
            textDate?.text = income.date
            textIncome?.text = convertToRupiah(income.income)
            textTotalTransaction?.text = income.totalTransaction.toString()
        }
    }

    fun updateIncomeList(newestIncomeList: ArrayList<Income>){
        incomeList.clear()
        incomeList.addAll(newestIncomeList)
        notifyDataSetChanged()
    }
}
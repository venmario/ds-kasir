package com.example.restoapp.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.restoapp.adapter.CategoryListAdapter
import com.example.restoapp.adapter.IncomeAdapter
import com.example.restoapp.databinding.FragmentIncomeBinding
import com.example.restoapp.util.convertToRupiah
import com.example.restoapp.viewmodel.IncomeViewModel

class IncomeFragment : Fragment() {

    private lateinit var binding: FragmentIncomeBinding
    private lateinit var viewModel: IncomeViewModel
    private val incomeListAdapter = IncomeAdapter(arrayListOf())
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentIncomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(IncomeViewModel::class.java)
        with(binding){
            recView?.layoutManager = LinearLayoutManager(context)
            recView?.adapter = incomeListAdapter

            textTotalIncome?.visibility = View.GONE
            chipToday?.isChecked = true
            viewModel.getIncome(requireActivity(), "today")

            chipToday?.setOnClickListener {
                viewModel.getIncome(requireActivity(), "today")
                progressBar?.visibility = View.VISIBLE
            }
            chipWeekly?.setOnClickListener {
                viewModel.getIncome(requireActivity(), "weekly")
                progressBar?.visibility = View.VISIBLE
            }
            chipMonthly?.setOnClickListener {
                viewModel.getIncome(requireActivity(), "monthly")
                progressBar?.visibility = View.VISIBLE
            }
        }
        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.incomes.observe(viewLifecycleOwner){
            if (it.isSuccess){
                with(binding){
                    progressBar?.visibility = View.GONE
                    incomeListAdapter.updateIncomeList(it.data!!)
                    Log.d("incomes",it.data!!.toString())
                    var totalIncome = 0
                    for(item in it.data!!){
                        Log.d("income", item.income.toString())
                        totalIncome += item.income
                    }
                    textTotalIncome?.visibility = View.VISIBLE
                    textTotalIncome?.text = "Total Income : ${convertToRupiah(totalIncome)}"
                }
            }
        }
    }
}
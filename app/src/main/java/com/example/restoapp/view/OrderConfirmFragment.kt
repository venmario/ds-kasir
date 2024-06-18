package com.example.restoapp.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.restoapp.R
import com.example.restoapp.adapter.OrderDetailAdapter
import com.example.restoapp.databinding.FragmentOrderConfirmBinding
import com.example.restoapp.util.convertToRupiah
import com.example.restoapp.viewmodel.OrderViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.textfield.MaterialAutoCompleteTextView

class OrderConfirmFragment : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentOrderConfirmBinding
//    private lateinit var viewModel: OrderViewModel
    private lateinit var navController: NavController
    private var estimationTime = "10"
    private val viewModel: OrderViewModel by activityViewModels()
    private val items = arrayOf("5 minutes","10 minutes", "15 minutes","20 minutes","25 minutes", "30 minutes", "45 minutes","60 minutes","90 minutes","120 minutes")
    private lateinit var adapterEstimation :ArrayAdapter<String>
    private val orderDetailAdapter = OrderDetailAdapter(arrayListOf())
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOrderConfirmBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        viewModel = ViewModelProvider(this).get(OrderViewModel::class.java)
        navController = Navigation.findNavController(requireParentFragment().requireView())
        adapterEstimation = ArrayAdapter<String>(requireContext(), R.layout.item_estimation, items)
        with(binding){
            recViewOrderDetail?.layoutManager = LinearLayoutManager(context)
            recViewOrderDetail?.adapter = orderDetailAdapter
            (itemEstimation as? MaterialAutoCompleteTextView)?.apply {
                setAdapter(adapterEstimation)
                setText(items[1],false)
//                setSelection(1)

            }
            itemEstimation?.setOnItemClickListener { _, _, position, _ ->
                val item = items[position]
                Log.d("item", item)
                val times = item.split(" ")
                estimationTime = times[0]
            }
        }

        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.selectedOrder.observe(viewLifecycleOwner){
            if(it != null){
                orderDetailAdapter.updateOrderDetailList(it.orderDetails!!)
                with(binding){
                    textCustomerName?.text = it.fullname
                    textTotal?.text = convertToRupiah(it.grandTotal)
                    textPaymentMethod?.text = when(it.paymentType){
                        "bank_transfer"-> {
                            when(it.bank){
                                "bca" -> "BCA Virtual Account"
                                else -> "BRI Virtual Account"
                            }
                        }
                        "shopeepay" -> "ShopeePay"
                        else -> "GoPay"
                    }

                    buttonConfirm?.setOnClickListener {_->
                        viewModel.confirmOrder(it.orderId,estimationTime,requireActivity())
                        buttonConfirm.text = "Loading..."
                        disableButton()
                    }
                }
            }
        }
        viewModel.confirmedOrder.observe(viewLifecycleOwner){
            if (it){
                Log.d("orderstatuschanged","get all status trigger on confirmedOrder observer function")
                viewModel.getAllStatus(requireActivity())
                navController.popBackStack()
                Log.d("get here","here")
                viewModel.setConfirOrderFalse()
            }
        }
    }

    private fun disableButton(){
        with(binding){
            buttonConfirm?.backgroundTintList = resources.getColorStateList(R.color.md_theme_primary_disable)
            buttonConfirm?.setTextColor(resources.getColorStateList(R.color.md_theme_secondary_disable))
            buttonConfirm?.isEnabled = false
        }
    }
}
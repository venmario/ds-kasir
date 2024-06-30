package com.example.restoapp.view

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.auth0.android.jwt.JWT
import com.duniasteak.restoapp.adapter.home.OrderListAdapter
import com.duniasteak.restoapp.adapter.home.StatusListAdapter
import com.example.restoapp.adapter.OrderDetailAdapter
import com.example.restoapp.databinding.FragmentHomeBinding
import com.example.restoapp.firebase.MyFirebaseMessagingService
import com.example.restoapp.model.Order
import com.example.restoapp.util.convertToRupiah
import com.example.restoapp.util.getAccToken
import com.example.restoapp.util.setNewAccToken
import com.example.restoapp.viewmodel.LoginViewModel
import com.example.restoapp.viewmodel.OrderViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.util.Date


class HomeFragment : Fragment(), OrderListAdapter.IOrderListListener {

    private lateinit var binding: FragmentHomeBinding

//    private lateinit var viewModel: OrderViewModel
    private val viewModel: OrderViewModel by activityViewModels()
    private val viewModelLogin: LoginViewModel by activityViewModels()
    private val statusListAdapter = StatusListAdapter(arrayListOf(),this)
    private val orderDetailAdapter = OrderDetailAdapter(arrayListOf())
    private lateinit var mediaPlayerNewOrder: MediaPlayer
    private lateinit var navController: NavController
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        mediaPlayerNewOrder = MediaPlayer.create(context, com.example.restoapp.R.raw.neworder)
//        viewModel = ViewModelProvider(this).get(OrderViewModel::class.java)
        val (accToken, username) = getAccToken(requireActivity())
        accToken?.let {
            if (it.isNotEmpty()) {
                val expToken = JWT(it).expiresAt
                if (expToken != null) {
                    Log.d("exp token", expToken.time.toString())
                    Log.d("now", Date().time.toString())
                    //acctoken expired
                    if (Date().time > expToken.time) {
                        Log.d("exp token", "token expired")
                        setNewAccToken(requireActivity(), "", "")
                        Navigation.findNavController(view)
                            .navigate(HomeFragmentDirections.actionToLogin())
                    } else {
                        with(binding) {
                            recViewStatus?.layoutManager = LinearLayoutManager(context)
                            recViewStatus?.adapter = statusListAdapter

                            recViewOrderDetail?.layoutManager = LinearLayoutManager(context)
                            recViewOrderDetail?.adapter = orderDetailAdapter
                            textCashierName?.text = "Cashier : $username"
                        }
                        Log.d(
                            "orderstatuschanged",
                            "get all status trigger on onviewcreated function"
                        )
                        viewModel.getAllStatus(requireActivity())
                        observeViewModel()
                    }
                }
            } else {
                Navigation.findNavController(view).navigate(HomeFragmentDirections.actionToLogin())
            }
        }
        disableButton()
        noOrderSelected()
        binding.refreshLayout?.setOnRefreshListener {
            Log.d("orderstatuschanged", "get all status trigger on refresh layout function")
            viewModel.getAllStatus(requireActivity())
            statusListAdapter.updateStatusList(arrayListOf())
            noOrderSelected()
        }
    }

    private fun observeViewModel() {
        viewModel.serviceResultOrderList.observe(viewLifecycleOwner){
            if(it.isSuccess){
                Log.d("orders", it.data!!.toString())
                statusListAdapter.updateStatusList(it.data!!)
                binding.loadingOrders?.visibility = View.GONE
                binding.refreshLayout?.isRefreshing = false
            }
        }

        viewModel.selectedOrder.observe(viewLifecycleOwner) {
            if (it.orderDetails != null) {
                orderSelected()
                orderDetailAdapter.updateOrderDetailList(it.orderDetails!!)
                binding.loadingOrderDetail?.visibility = View.GONE
                Log.d("order", it.toString())
                when(it.status){
                    "In Waiting List"-> {
                        enableButton()
                        with(binding){
                            buttonChangeStatusOrder?.text = "Confirm Order"
                            buttonChangeStatusOrder?.setOnClickListener {_->
                                Log.d("confirm order","clicked")
                                val action = HomeFragmentDirections.actionConfirmOrder()
                                navController.navigate(action)
                            }
                        }
                    }
                    "Processing" -> {
                        enableButton()
                        with(binding){
                            buttonChangeStatusOrder?.text = "Pick Up Order"
                            buttonChangeStatusOrder?.setOnClickListener {_->
                                Log.d("order ready",it.orderId)
                                MaterialAlertDialogBuilder(requireContext())
                                    .setTitle("Pick Up Order")
                                    .setMessage("Are you sure this order ready to pick up?")
                                    .setNegativeButton("No"){ dialog,_->
                                        dialog.dismiss()
                                    }
                                    .setPositiveButton("Yes"){dialog,_->
                                        viewModel.pickUpOrder(it.orderId, requireActivity())
                                        dialog.dismiss()
                                    }
                                    .show()
                            }
                        }
                    }
                    "Ready to Pick Up" -> {
                        with(binding){
                            buttonChangeStatusOrder?.text = "Collect Order"
                            buttonChangeStatusOrder?.setOnClickListener {_->
                                Log.d("order finish",it.orderId)
                                MaterialAlertDialogBuilder(requireContext())
                                    .setTitle("Collect Order")
                                    .setMessage("Are you sure this order collected?")
                                    .setNegativeButton("No"){ dialog,_->
                                        dialog.dismiss()
                                    }
                                    .setPositiveButton("Yes"){dialog,_->
                                        viewModel.collectOrder(it.orderId, requireActivity())
                                        dialog.dismiss()
                                    }
                                    .show()
                            }
                        }

                        enableButton()
                    }
                    "Booking" -> {
                        binding.buttonChangeStatusOrder?.text = "Order Booked"
                        disableButton()
                    }
                    else -> {
                        binding.buttonChangeStatusOrder?.text = "Order Finished"
                        disableButton()
                    }
                }
                with(binding){
                    textTotal?.text = convertToRupiah(it.grandTotal)
                    textPaymentMethod?.text = when(it.paymentType){
                        "bank_transfer"-> {
                            when(it.bank){
                                "bca" -> "BCA Virtual Account"
                                else -> "BRI Virtual Account"
                            }
                        }
                        "shopeepay" -> "ShopeePay"
                        "Redeem Point" -> "Redeem Point"
                        else -> "GoPay"
                    }
                    textCustomerName?.text = it.fullname
                }
            }
            Log.d("selectedOrder", it.toString())
        }

        viewModel.pickedUpOrder.observe(viewLifecycleOwner){
            if(it){
                orderStatusChanged()
            }
        }
        viewModel.collectedOrder.observe(viewLifecycleOwner){
            if (it){
                orderStatusChanged()
            }
        }

        viewModel.confirmedOrder.observe(viewLifecycleOwner){
            if (it){
                noOrderSelected()
            }
        }

        viewModelLogin.loggedOut.observe(viewLifecycleOwner){
            if (it.isSuccess){
                setNewAccToken(requireActivity(),"","")
                val action = HomeFragmentDirections.actionToLogin()
                navController.navigate(action)
            }
        }
    }

    override fun selectOrder(order: Order) {
        Log.d("select order", order.toString())
        viewModel.selectOrder(order, requireActivity())
        disableButton()
        with(binding){
            loadingOrderDetail?.visibility = View.VISIBLE
            buttonChangeStatusOrder?.text = "Loading..."
        }
    }

    private fun noOrderSelected(){
        with(binding){
            recViewOrderDetail?.visibility = View.GONE
            orderInfo?.visibility = View.GONE
            buttonChangeStatusOrder?.text = "Please choose an order"
        }
        disableButton()
    }
    private fun orderSelected(){
        Log.d("order selected","")
        with(binding){
            recViewOrderDetail?.visibility = View.VISIBLE
            orderInfo?.visibility = View.VISIBLE
            loadingOrderDetail?.visibility = View.GONE
        }
    }
    private fun enableButton(){
        with(binding){
            buttonChangeStatusOrder?.backgroundTintList = resources.getColorStateList(com.example.restoapp.R.color.md_theme_primary)
            buttonChangeStatusOrder?.setTextColor(resources.getColorStateList(com.example.restoapp.R.color.md_theme_secondary))
            buttonChangeStatusOrder?.isEnabled = true
        }
    }

    private fun disableButton(){
        with(binding){
            buttonChangeStatusOrder?.backgroundTintList = resources.getColorStateList(com.example.restoapp.R.color.md_theme_primary_disable)
            buttonChangeStatusOrder?.setTextColor(resources.getColorStateList(com.example.restoapp.R.color.md_theme_secondary_disable))
            buttonChangeStatusOrder?.isEnabled = false
        }
    }

    private fun orderStatusChanged(){
        Log.d("orderstatuschanged","get all status trigger on orderstatuschanged function")
        viewModel.getAllStatus(requireActivity())
        noOrderSelected()
        binding.loadingOrders?.visibility = View.VISIBLE
    }

    private val orderReceiver:BroadcastReceiver = object : BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            if(intent != null && intent.action != null){
                if (intent.action == MyFirebaseMessagingService.NOTIFICATION_RECEIVED){
                    val notificationJson = intent.getStringExtra("notification")
                    if (notificationJson != null) {
                        Log.d("orderstatuschanged","get all status trigger on orderReceiver function")
                        viewModel.getAllStatus(requireActivity())
                        mediaPlayerNewOrder.start()
                    }

                }
            }
            Log.d("NotificationReceiver", "onReceive triggered")
        }

    }

    override fun onResume() {
        super.onResume()
        val intentFilter = IntentFilter()
        intentFilter.addAction(MyFirebaseMessagingService.NOTIFICATION_RECEIVED)
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(orderReceiver,intentFilter)
    }

    override fun onPause() {
        super.onPause()
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(orderReceiver)
    }
}
package com.example.restoapp.view

import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.restoapp.R
import com.example.restoapp.databinding.FragmentCancelOrderOptionBinding
import com.example.restoapp.viewmodel.OrderViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class CancelOrderOptionFragment : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentCancelOrderOptionBinding
    private lateinit var navController: NavController
    private val viewModel: OrderViewModel by activityViewModels()

    private var isReasonError = true
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCancelOrderOptionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(requireParentFragment().requireView())
        disableButton()
        with(binding){
            textReasonLayout?.editText?.doOnTextChanged { text, _, _, _ ->
                if (TextUtils.isEmpty(text)) {
                    isReasonError = true
                    textReasonLayout.error = "Please input the reason!"
                    disableButton()
                } else {
                    isReasonError = false
                    textReasonLayout.error = null
                    textReasonLayout.isErrorEnabled = false
                    enableButton()
                }
            }
        }
        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.selectedOrder.observe(viewLifecycleOwner){
            if(it!=null){
                with(binding){
                    buttonCancel?.setOnClickListener {
                        navController.popBackStack()
                    }

                    buttonConfirm?.setOnClickListener {_->
                        val reason = textReason?.text.toString()
                        viewModel.refundOrder(requireActivity(), it.orderId, reason)
                        disableButton()
                        buttonConfirm.text = "Loading..."
                    }
                }
            }
        }
        viewModel.canceledOrder.observe(viewLifecycleOwner){
            if (it.isSuccess){
                viewModel.getAllStatus(requireActivity())
                navController.popBackStack()
            }else{
                with(binding){
                    buttonConfirm?.text = it.errorMessage
                }
                enableButton()
            }
        }
    }

    private fun disableButton() {
        with(binding) {
            buttonConfirm?.backgroundTintList =
                resources.getColorStateList(R.color.md_theme_primary_disable)
            buttonConfirm?.setTextColor(resources.getColorStateList(R.color.md_theme_secondary_disable))
            buttonConfirm?.isEnabled = false
        }
    }

    private fun enableButton() {
        with(binding) {
            if (!isReasonError){
                buttonConfirm?.backgroundTintList =
                    resources.getColorStateList(R.color.md_theme_primary)
                buttonConfirm?.setTextColor(resources.getColorStateList(R.color.md_theme_secondary))
                buttonConfirm?.isEnabled = true
            }
        }
    }
}
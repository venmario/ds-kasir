package com.example.restoapp.view

import android.os.Bundle
import android.text.InputType
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.example.restoapp.R
import com.example.restoapp.databinding.FragmentStoreBinding
import com.example.restoapp.model.Store
import com.example.restoapp.viewmodel.StoreViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat

class StoreFragment : Fragment() {
    private lateinit var binding: FragmentStoreBinding
    private lateinit var viewModel: StoreViewModel
    private lateinit var store:Store
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentStoreBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(StoreViewModel::class.java)

        viewModel.getOpenClose(requireActivity())

        val timePickerOpen = createTimePicker("Open Time")
        timePickerOpen.addOnPositiveButtonClickListener {
            val hourOpen = timePickerOpen.hour
            val minuteOpen = timePickerOpen.minute
            binding.textOpen?.setText(String.format("%02d:%02d:%02d", hourOpen, minuteOpen,0))
        }

        val timePickerClose = createTimePicker("Close Time")
        timePickerClose.addOnPositiveButtonClickListener {
            val hourClose = timePickerClose.hour
            val minuteClose = timePickerClose.minute
            binding.textClose?.setText(String.format("%02d:%02d:%02d", hourClose, minuteClose,0))
        }

        with(binding) {
            textOpen?.inputType = InputType.TYPE_NULL
            textOpen?.keyListener = null
            textOpen?.setOnClickListener {
                showPickerIfNotAdded(timePickerOpen, "timePickerOpenTag")
            }

            textClose?.inputType = InputType.TYPE_NULL
            textClose?.keyListener = null
            textClose?.setOnClickListener {
                showPickerIfNotAdded(timePickerClose, "timePickerCloseTag")
            }
        }
        observeViewModel(view)
    }

    private fun createTimePicker(title: String): MaterialTimePicker {
        return MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_24H)
            .setInputMode(MaterialTimePicker.INPUT_MODE_KEYBOARD)
            .setTheme(R.style.BaseTheme_TimePicker)
            .setTitleText(title)
            .build()
    }

    private fun showPickerIfNotAdded(picker: MaterialTimePicker, tag: String) {
        val fragmentManager = parentFragmentManager
        val existingPicker = fragmentManager.findFragmentByTag(tag)
        if (existingPicker == null) {
            picker.show(fragmentManager, tag)
        }
    }

    private fun observeViewModel(view: View) {
        viewModel.store.observe(viewLifecycleOwner){
            if (it.isSuccess){
                store = Store(0,"","")
                if (it.data != null){
                    store = it.data!!
                }
                with(binding){
                    textOpen?.setText(store.open)
                    textClose?.setText(store.close)

                    buttonSave?.setOnClickListener {
                        store.open = textOpen?.text.toString()
                        store.close = textClose?.text.toString()
                        viewModel.setOpenClose(requireActivity(),store)
                    }
                }
            }
        }
        viewModel.setStore.observe(viewLifecycleOwner){
            if (it.isSuccess){
                Snackbar.make(view, "Store time successfully saved", Snackbar.LENGTH_SHORT)
                    .show()
                viewModel.setFalseSetStore()
            }
        }
    }
}
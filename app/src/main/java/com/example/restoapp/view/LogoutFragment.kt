package com.example.restoapp.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.example.restoapp.R
import com.example.restoapp.databinding.FragmentLogoutBinding
import com.example.restoapp.util.getFcmTokens
import com.example.restoapp.util.setNewAccToken
import com.example.restoapp.viewmodel.LoginViewModel
import com.example.restoapp.viewmodel.OrderViewModel

class LogoutFragment : Fragment() {
    private lateinit var binding: FragmentLogoutBinding
    private val viewModel: LoginViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLogoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val (_,currentFcmToken) = getFcmTokens(requireActivity())
        viewModel.logout(requireActivity(),currentFcmToken)
        val action = LogoutFragmentDirections.actionLogout()
        Navigation.findNavController(view).navigate(action)
    }
}
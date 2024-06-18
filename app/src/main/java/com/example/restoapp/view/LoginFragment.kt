package com.example.restoapp.view

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.auth0.android.jwt.JWT
import com.example.restoapp.viewmodel.LoginViewModel
import com.example.restoapp.application.MyApplication
import com.example.restoapp.databinding.FragmentLoginBinding
import com.example.restoapp.util.getAccToken
import com.example.restoapp.util.getFcmTokens
import com.example.restoapp.util.setNewAccToken
import com.example.restoapp.util.showToast
import java.util.Date

class LoginFragment : Fragment() {
    companion object{
        val ACCESS_TOKEN = "ACCESS_TOKEN"
        val USERNAME = "USERNAME"
    }

    private lateinit var binding: FragmentLoginBinding
    private lateinit var viewModel: LoginViewModel
    lateinit var shared: SharedPreferences
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
        val (accToken) = getAccToken(requireActivity())
        accToken?.let {
            if (it.isNotEmpty()) {
                val expToken = JWT(it).expiresAt
                if (expToken != null) {
                    Log.d("exp token", expToken.time.toString())
                    Log.d("now", Date().time.toString())
                    //acctoken expired
                    if (Date().time <= expToken.time) {
                        Log.d("exp token", "token not expired")
                        val action = LoginFragmentDirections.actionToHome()
                        Navigation.findNavController(view).navigate(action)
                    }
                }
            }
        }

        var sharedFile = requireContext().packageName
        shared = requireContext().getSharedPreferences(sharedFile, Context.MODE_PRIVATE)

        binding.buttonSignIn?.setOnClickListener {
            val username = binding.textUsername?.text.toString()
            val password = binding.textPassword?.text.toString()

            val (oldFcmToken,currentFcmToken) = getFcmTokens(requireActivity())
            viewModel.login(username,password,oldFcmToken!!,currentFcmToken!!)
            viewModel.serviceResult.observe(viewLifecycleOwner) { sr ->
                if (sr.isSuccess) {
                    val editor: SharedPreferences.Editor = shared.edit()
                    editor.putString(ACCESS_TOKEN, sr.data?.token)
                    editor.putString(USERNAME, sr.data?.user?.username)
                    editor.apply()
                    val action = LoginFragmentDirections.actionToHome()
                    Navigation.findNavController(view).navigate(action)
                } else {
                    showToast(sr.errorMessage!!, requireContext())
                }
            }
        }

    }
}
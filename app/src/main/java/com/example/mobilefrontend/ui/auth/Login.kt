package com.example.mobilefrontend

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.mobilefrontend.databinding.FragmentLoginBinding
import com.example.mobilefrontend.model.LoginRequest
import com.example.mobilefrontend.repository.ApiResult
import com.example.mobilefrontend.viewmodels.AuthViewModel
import kotlinx.coroutines.launch

class Login : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.userState.collect { result ->
                    when (result) {
                        is ApiResult.Loading -> {
                            binding.btnLogin.isEnabled = false
                            binding.btnLogin.text = "Logging in..."
                        }

                        is ApiResult.Success -> {
                            binding.btnLogin.isEnabled = true
                            findNavController().navigate(R.id.action_login_to_home)
                        }

                        is ApiResult.Error -> {
                            binding.btnLogin.isEnabled = true
                            binding.btnLogin.text = "Log In"
                            binding.etLoginEmail.error = result.message
                        }

                        null -> Unit
                    }
                }
            }
        }

        binding.btnLogin.setOnClickListener {
            val email = binding.etLoginEmail.text.toString()
            val password = binding.etLoginPassword.text.toString()

            if (email.isBlank() || password.isBlank()) {
                if (email.isBlank()) binding.etLoginEmail.error = "Email required"
                if (password.isBlank()) binding.etLoginPassword.error = "Password required"
                return@setOnClickListener
            }

            val payload = LoginRequest(email, password)
            viewModel.login(payload)
        }

        binding.tvSignup.setOnClickListener {
            findNavController().navigate(R.id.action_login_to_signup)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

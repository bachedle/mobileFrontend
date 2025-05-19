package com.example.mobilefrontend

import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.request.SingleRequest
import com.example.mobilefrontend.databinding.FragmentSignupBinding
import com.example.mobilefrontend.model.LoginRequest
import com.example.mobilefrontend.model.SignUpRequest
import com.example.mobilefrontend.repository.ApiResult
import com.example.mobilefrontend.viewmodels.AuthViewModel
import com.example.mobilefrontend.viewmodels.BaseViewModelFactory
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue

class Signup : Fragment() {

    private var _binding: FragmentSignupBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AuthViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        //background animation
//        val animationDrawable = binding.root.background as AnimationDrawable
//        animationDrawable.setEnterFadeDuration(500)
//        animationDrawable.setExitFadeDuration(500)
//        animationDrawable.start()

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.userState.collect { result ->
                    when (result) {
                        is ApiResult.Loading -> {
                            binding.btnSignup.isEnabled = false
                            binding.btnSignup.text = "Processing..."
                        }

                        is ApiResult.Success -> {
                            binding.btnSignup.isEnabled = true
                            findNavController().navigate(R.id.action_signup_to_home)
                        }

                        is ApiResult.Error -> {
                            binding.btnSignup.isEnabled = true
                            binding.btnSignup.text = "Sign Up"
//                            binding.etLoginEmail.error = result.message
                        }

                        null -> Unit
                    }
                }
            }
        }

        binding.btnSignup.setOnClickListener {
            val name = binding.etSignupName.text.toString()
            val email = binding.etSignupEmail.text.toString()
            val password = binding.etSignupPassword.text.toString()

            if (email.isBlank() || password.isBlank() || name.isBlank()) {
                if (email.isBlank()) binding.etSignupEmail.error = "Email required"
                if (password.isBlank()) binding.etSignupPassword.error = "Password required"
                if (name.isBlank()) binding.etSignupName.error = "Name required"
                return@setOnClickListener
            }
            val payload = SignUpRequest(name, email, password)
            viewModel.signup(payload)
        }

        binding.tvSignin.setOnClickListener {
            findNavController().navigate(R.id.action_signup_to_login)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

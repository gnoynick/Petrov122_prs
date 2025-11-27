package com.example.petrov122_prs.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.petrov122_prs.presentation.viewmodels.AuthState
import com.example.petrov122_prs.databinding.FragmentMainBinding
import com.example.petrov122_prs.presentation.viewmodels.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainFragment : Fragment() {
    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservers()

        Toast.makeText(requireContext(), "Добро пожаловать в приложение!", Toast.LENGTH_SHORT).show()

        binding.buttonLogout.setOnClickListener {
            viewModel.logout()
            Toast.makeText(requireContext(), "Выход выполняется...", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.authState.collect { state ->
                when (state) {
                    is AuthState.LoggedOut -> {
                        Toast.makeText(requireContext(), "Выход выполнен успешно", Toast.LENGTH_SHORT).show()
                        findNavController().navigate(com.example.petrov122_prs.R.id.action_mainFragment_to_loginFragment)
                    }
                    is AuthState.Error -> {
                        Toast.makeText(requireContext(), "Ошибка: ${state.message}", Toast.LENGTH_SHORT).show()
                    }
                    else -> {}
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
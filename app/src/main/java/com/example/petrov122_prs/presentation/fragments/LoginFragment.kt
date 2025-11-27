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
import com.example.petrov122_prs.databinding.FragmentLoginBinding
import com.example.petrov122_prs.presentation.viewmodels.AuthState
import com.example.petrov122_prs.domain.utils.TooltipKeys
import com.example.petrov122_prs.domain.utils.TooltipContent
import com.example.petrov122_prs.domain.utils.TooltipManager
import com.example.petrov122_prs.domain.utils.showTooltip
import com.example.petrov122_prs.presentation.viewmodels.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AuthViewModel by viewModels()

    @Inject
    lateinit var tooltipManager: TooltipManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupClickListeners()
        setupObservers()
        showTooltipsIfNeeded()

        Toast.makeText(requireContext(), "Добро пожаловать! Используйте демо данные для теста", Toast.LENGTH_LONG).show()
    }

    private fun setupClickListeners() {
        binding.buttonLogin.setOnClickListener {
            attemptLogin()
        }

        binding.buttonDemoLogin.setOnClickListener {
            fillDemoCredentials()
            Toast.makeText(requireContext(), "Демо данные заполнены! Нажмите 'Войти'", Toast.LENGTH_SHORT).show()
        }

        binding.buttonGoToRegister.setOnClickListener {
            findNavController().navigate(com.example.petrov122_prs.R.id.action_loginFragment_to_registerFragment)
            Toast.makeText(requireContext(), "Переход к регистрации", Toast.LENGTH_SHORT).show()
        }

        binding.buttonGuestLogin.setOnClickListener {
            findNavController().navigate(com.example.petrov122_prs.R.id.action_loginFragment_to_mainFragment)
            Toast.makeText(requireContext(), "Вход как гость", Toast.LENGTH_SHORT).show()
        }

        // Show tooltips on focus
        binding.editTextIdentifier.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                viewLifecycleOwner.lifecycleScope.launch {
                    if (tooltipManager.shouldShowTooltip(TooltipKeys.LOGIN_IDENTIFIER)) {
                        binding.textInputLayoutIdentifier.showTooltip(TooltipContent.LOGIN_IDENTIFIER) {
                            lifecycleScope.launch {
                                tooltipManager.markTooltipAsShown(TooltipKeys.LOGIN_IDENTIFIER)
                            }
                        }
                    }
                }
            }
        }

        binding.editTextPassword.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                viewLifecycleOwner.lifecycleScope.launch {
                    if (tooltipManager.shouldShowTooltip(TooltipKeys.LOGIN_PASSWORD)) {
                        binding.textInputLayoutPassword.showTooltip(TooltipContent.LOGIN_PASSWORD) {
                            lifecycleScope.launch {
                                tooltipManager.markTooltipAsShown(TooltipKeys.LOGIN_PASSWORD)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun showTooltipsIfNeeded() {
        viewLifecycleOwner.lifecycleScope.launch {
            // Show tooltips sequentially on first visit
            if (tooltipManager.shouldShowTooltip(TooltipKeys.DEMO_LOGIN)) {
                delay(1000) // Wait for layout to be ready
                binding.buttonDemoLogin.showTooltip(TooltipContent.DEMO_LOGIN, 4000L) {
                    lifecycleScope.launch {
                        tooltipManager.markTooltipAsShown(TooltipKeys.DEMO_LOGIN)

                        // Show next tooltip
                        if (tooltipManager.shouldShowTooltip(TooltipKeys.LOGIN_REMEMBER_ME)) {
                            delay(500)
                            binding.checkboxRememberMe.showTooltip(TooltipContent.LOGIN_REMEMBER_ME, 3000L) {
                                lifecycleScope.launch {
                                    tooltipManager.markTooltipAsShown(TooltipKeys.LOGIN_REMEMBER_ME)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun attemptLogin() {
        val identifier = binding.editTextIdentifier.text.toString()
        val password = binding.editTextPassword.text.toString()
        val rememberMe = binding.checkboxRememberMe.isChecked

        if (identifier.isEmpty() || password.isEmpty()) {
            Toast.makeText(requireContext(), "Заполните все поля", Toast.LENGTH_SHORT).show()
            return
        }

        viewModel.login(identifier, password, rememberMe)
        Toast.makeText(requireContext(), "Выполняется вход...", Toast.LENGTH_SHORT).show()
    }

    private fun fillDemoCredentials() {
        binding.editTextIdentifier.setText("demo_user")
        binding.editTextPassword.setText("Demo123!")
        binding.checkboxRememberMe.isChecked = true
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.authState.collect { state ->
                when (state) {
                    is AuthState.Loading -> showLoading(true)
                    is AuthState.LoginSuccess -> {
                        showLoading(false)
                        Toast.makeText(requireContext(), "Вход выполнен успешно!", Toast.LENGTH_SHORT).show()
                        findNavController().navigate(com.example.petrov122_prs.R.id.action_loginFragment_to_mainFragment)
                    }
                    is AuthState.Error -> {
                        showLoading(false)
                        showError(state.message)
                    }
                    else -> showLoading(false)
                }
            }
        }
    }

    private fun showLoading(show: Boolean) {
        binding.progressBar.visibility = if (show) View.VISIBLE else View.GONE
        binding.buttonLogin.isEnabled = !show
        binding.buttonDemoLogin.isEnabled = !show
        binding.buttonGuestLogin.isEnabled = !show
    }

    private fun showError(message: String) {
        Toast.makeText(requireContext(), "Ошибка: $message", Toast.LENGTH_LONG).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
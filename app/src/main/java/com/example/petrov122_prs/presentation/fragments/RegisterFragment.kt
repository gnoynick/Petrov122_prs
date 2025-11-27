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
import com.example.petrov122_prs.databinding.FragmentRegisterBinding
import com.example.petrov122_prs.domain.utils.TooltipKeys
import com.example.petrov122_prs.domain.utils.TooltipContent
import com.example.petrov122_prs.domain.utils.TooltipManager
import com.example.petrov122_prs.domain.utils.showTooltip
import com.example.petrov122_prs.presentation.viewmodels.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

@AndroidEntryPoint
class RegisterFragment : Fragment() {
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AuthViewModel by viewModels()

    @Inject
    lateinit var tooltipManager: TooltipManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupClickListeners()
        setupObservers()
        setupTooltips()

        Toast.makeText(requireContext(), "Заполните форму регистрации", Toast.LENGTH_SHORT).show()
    }

    private fun setupClickListeners() {
        binding.buttonRegister.setOnClickListener {
            attemptRegistration()
        }

        binding.buttonClearForm.setOnClickListener {
            clearForm()
            Toast.makeText(requireContext(), "Форма очищена", Toast.LENGTH_SHORT).show()
        }

        binding.buttonDemoRegister.setOnClickListener {
            fillDemoData()
            Toast.makeText(requireContext(), "Демо данные заполнены!", Toast.LENGTH_SHORT).show()
        }

        binding.buttonGoToLogin.setOnClickListener {
            findNavController().navigate(com.example.petrov122_prs.R.id.action_registerFragment_to_loginFragment)
            Toast.makeText(requireContext(), "Возврат к входу", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupTooltips() {
        // Username tooltip
        binding.editTextUsername.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                viewLifecycleOwner.lifecycleScope.launch {
                    if (tooltipManager.shouldShowTooltip(TooltipKeys.REGISTER_USERNAME)) {
                        binding.textInputLayoutUsername.showTooltip(TooltipContent.REGISTER_USERNAME) {
                            lifecycleScope.launch {
                                tooltipManager.markTooltipAsShown(TooltipKeys.REGISTER_USERNAME)
                            }
                        }
                    }
                }
            }
        }

        // Email tooltip
        binding.editTextEmail.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                viewLifecycleOwner.lifecycleScope.launch {
                    if (tooltipManager.shouldShowTooltip(TooltipKeys.REGISTER_EMAIL)) {
                        binding.textInputLayoutEmail.showTooltip(TooltipContent.REGISTER_EMAIL) {
                            lifecycleScope.launch {
                                tooltipManager.markTooltipAsShown(TooltipKeys.REGISTER_EMAIL)
                            }
                        }
                    }
                }
            }
        }

        // Password tooltip
        binding.editTextPassword.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                viewLifecycleOwner.lifecycleScope.launch {
                    if (tooltipManager.shouldShowTooltip(TooltipKeys.REGISTER_PASSWORD)) {
                        binding.textInputLayoutPassword.showTooltip(TooltipContent.REGISTER_PASSWORD) {
                            lifecycleScope.launch {
                                tooltipManager.markTooltipAsShown(TooltipKeys.REGISTER_PASSWORD)
                            }
                        }
                    }
                }
            }
        }

        // Confirm password tooltip
        binding.editTextConfirmPassword.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                viewLifecycleOwner.lifecycleScope.launch {
                    if (tooltipManager.shouldShowTooltip(TooltipKeys.REGISTER_CONFIRM_PASSWORD)) {
                        binding.textInputLayoutConfirmPassword.showTooltip(TooltipContent.REGISTER_CONFIRM_PASSWORD) {
                            lifecycleScope.launch {
                                tooltipManager.markTooltipAsShown(TooltipKeys.REGISTER_CONFIRM_PASSWORD)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun attemptRegistration() {
        val username = binding.editTextUsername.text.toString()
        val email = binding.editTextEmail.text.toString()
        val password = binding.editTextPassword.text.toString()
        val confirmPassword = binding.editTextConfirmPassword.text.toString()
        val firstName = binding.editTextFirstName.text.toString()
        val lastName = binding.editTextLastName.text.toString()
        val phoneNumber = binding.editTextPhone.text.toString()

        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || firstName.isEmpty() || lastName.isEmpty()) {
            Toast.makeText(requireContext(), "Заполните все обязательные поля", Toast.LENGTH_SHORT).show()
            return
        }

        if (password != confirmPassword) {
            Toast.makeText(requireContext(), "Пароли не совпадают", Toast.LENGTH_SHORT).show()
            return
        }

        viewModel.register(username, email, password, confirmPassword, firstName, lastName, phoneNumber)
        Toast.makeText(requireContext(), "Регистрация выполняется...", Toast.LENGTH_SHORT).show()
    }

    private fun clearForm() {
        binding.editTextUsername.text?.clear()
        binding.editTextEmail.text?.clear()
        binding.editTextPassword.text?.clear()
        binding.editTextConfirmPassword.text?.clear()
        binding.editTextFirstName.text?.clear()
        binding.editTextLastName.text?.clear()
        binding.editTextPhone.text?.clear()

        binding.textInputLayoutUsername.error = null
        binding.textInputLayoutEmail.error = null
        binding.textInputLayoutPassword.error = null
        binding.textInputLayoutConfirmPassword.error = null
    }

    private fun fillDemoData() {
        binding.editTextUsername.setText("demo_user_${Random.nextInt(1000)}")
        binding.editTextEmail.setText("demo${Random.nextInt(1000)}@example.com")
        binding.editTextPassword.setText("Demo123!")
        binding.editTextConfirmPassword.setText("Demo123!")
        binding.editTextFirstName.setText("Демо")
        binding.editTextLastName.setText("Пользователь")
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.authState.collect { state ->
                when (state) {
                    is AuthState.Loading -> showLoading(true)
                    is AuthState.RegistrationSuccess -> {
                        showLoading(false)
                        Toast.makeText(requireContext(), "Регистрация успешна!", Toast.LENGTH_SHORT).show()
                        findNavController().navigate(com.example.petrov122_prs.R.id.action_registerFragment_to_mainFragment)
                    }
                    is AuthState.Error -> {
                        showLoading(false)
                        showError(state.message)
                    }
                    else -> showLoading(false)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.validationErrors.collect { errors ->
                updateValidationErrors(errors)
                if (errors.isNotEmpty()) {
                    Toast.makeText(requireContext(), "Исправьте ошибки в форме", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun updateValidationErrors(errors: Map<String, String>) {
        binding.textInputLayoutUsername.error = errors["username"]
        binding.textInputLayoutEmail.error = errors["email"]
        binding.textInputLayoutPassword.error = errors["password"]
        binding.textInputLayoutConfirmPassword.error = errors["confirmPassword"]
        binding.textInputLayoutFirstName.error = errors["firstName"]
        binding.textInputLayoutLastName.error = errors["lastName"]
    }

    private fun showLoading(show: Boolean) {
        binding.progressBar.visibility = if (show) View.VISIBLE else View.GONE
        binding.buttonRegister.isEnabled = !show
        binding.buttonDemoRegister.isEnabled = !show
        binding.buttonClearForm.isEnabled = !show
    }

    private fun showError(message: String) {
        Toast.makeText(requireContext(), "Ошибка регистрации: $message", Toast.LENGTH_LONG).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
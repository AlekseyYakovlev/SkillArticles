package ru.skillbranch.skillarticles.ui.registration

import android.text.method.PasswordTransformationMethod
import android.view.View
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_registration.*
import ru.skillbranch.skillarticles.R
import ru.skillbranch.skillarticles.ui.auth.AuthFragmentArgs
import ru.skillbranch.skillarticles.ui.base.BaseFragment
import ru.skillbranch.skillarticles.viewmodels.registration.RegistrationViewModel

@AndroidEntryPoint
class RegistrationFragment : BaseFragment<RegistrationViewModel>() {
    override val viewModel: RegistrationViewModel by viewModels()
    override val layout: Int = R.layout.fragment_registration
    private val args: AuthFragmentArgs by navArgs()

    override fun setupViews() {

        btn_register.setOnClickListener {
            viewModel.handleRegisterClick(
                requireContext(),
                if (args.privateDestination == -1) null else args.privateDestination,
            )
        }

        et_name.doAfterTextChanged { newText ->
            newText?.let { viewModel.handleNameChange(it.toString()) }
        }

        et_login.doAfterTextChanged { newText ->
            newText?.let { viewModel.handleLoginChange(it.toString()) }
        }

        et_password.doAfterTextChanged { newText ->
            newText?.let { viewModel.handlePasswordChange(it.toString()) }
        }

        et_confirm.doAfterTextChanged { newText ->
            newText?.let { viewModel.handleSecondPasswordChange(it.toString()) }
        }

        wrap_password.setEndIconOnClickListener {
            viewModel.unmaskFirstPassword()
        }

        viewModel.state.observe(viewLifecycleOwner) { state ->
            with(state) {
                if (isNameValid) wrap_name.error = null
                else wrap_name.error = getString(R.string.reg_err__invalid_name)

                if (isEmailValid) wrap_login.error = null
                else wrap_login.error = getString(R.string.reg_err__invalid_email)

                if (isPasswordValid) wrap_password.error = null
                else wrap_password.error = getString(R.string.reg_err__invalid_password)

                if (isSecondPasswordVisible) {
                    et_password.transformationMethod = PasswordTransformationMethod.getInstance()
                    wrap_confirm.visibility = View.VISIBLE
                } else {
                    et_password.transformationMethod =null
                    wrap_confirm.visibility = View.GONE
                }

                if (arePasswordsTheSame) wrap_confirm.error = null
                else wrap_confirm.error = getString(R.string.reg_err__mismatching_passwords)
            }
        }
    }
}

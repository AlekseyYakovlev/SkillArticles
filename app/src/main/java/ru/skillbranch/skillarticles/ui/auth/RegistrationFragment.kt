package ru.skillbranch.skillarticles.ui.auth

import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import kotlinx.android.synthetic.main.fragment_registration.*
import ru.skillbranch.skillarticles.R
import ru.skillbranch.skillarticles.ui.base.BaseFragment
import ru.skillbranch.skillarticles.viewmodels.auth.AuthViewModel

class RegistrationFragment() : BaseFragment<AuthViewModel>() {
    override val viewModel: AuthViewModel by viewModels()
    override val layout: Int = R.layout.fragment_registration
    private val args: AuthFragmentArgs by navArgs()


    override fun setupViews() {

        btn_register.setOnClickListener {
            viewModel.handleRegister(
                et_name.text.toString(),
                et_login.text.toString(),
                et_password.text.toString(),
                if (args.privateDestination == -1) null else args.privateDestination,
                requireContext()
            )
        }

        et_name.doAfterTextChanged {
            if (!viewModel.isNameValid(it.toString())) {
                wrap_name.error = getString(R.string.reg_err__invalid_name)
            } else wrap_name.error = null

        }

        et_login.doAfterTextChanged {
            if (!viewModel.isEmailValid(it.toString())) {
                wrap_login.error = getString(R.string.reg_err__invalid_email)
            } else wrap_login.error = null
        }

        et_password.doAfterTextChanged {
            if (!viewModel.isPasswordValid(it.toString())) {
                wrap_password.error = getString(R.string.reg_err__invalid_password)
            } else wrap_password.error = null
        }

        et_confirm.doAfterTextChanged {
            if (et_confirm.text.toString() != et_password.text.toString()) {
                wrap_confirm.error = getString(R.string.reg_err__mismatching_passwords)
            } else wrap_confirm.error = null
        }
    }
}
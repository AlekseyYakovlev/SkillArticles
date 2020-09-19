package ru.skillbranch.skillarticles.ui.auth

import androidx.annotation.VisibleForTesting
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import androidx.savedstate.SavedStateRegistryOwner
import kotlinx.android.synthetic.main.fragment_registration.*
import ru.skillbranch.skillarticles.R
import ru.skillbranch.skillarticles.ui.RootActivity
import ru.skillbranch.skillarticles.ui.base.BaseFragment
import ru.skillbranch.skillarticles.viewmodels.auth.AuthViewModel

class RegistrationFragment() : BaseFragment<AuthViewModel>() {
    var _mockFactory:((SavedStateRegistryOwner)-> ViewModelProvider.Factory)? = null
    override val viewModel: AuthViewModel by viewModels {
        _mockFactory?.invoke(this)?: defaultViewModelProviderFactory
    }
    override val layout: Int = R.layout.fragment_registration
    private val args: AuthFragmentArgs by navArgs()

    //testing constructor
    @VisibleForTesting(otherwise = VisibleForTesting.NONE)
    constructor(
        mockRoot: RootActivity,
        mockFactory: ((SavedStateRegistryOwner)-> ViewModelProvider.Factory)? = null
    ) : this() {
        _mockRoot = mockRoot
        _mockFactory = mockFactory
    }

    override fun setupViews() {

        btn_register.setOnClickListener {
            viewModel.handleRegister(
                et_name.text.toString(),
                et_login.text.toString(),
                et_password.text.toString(),
                if (args.privateDestination == -1) null else args.privateDestination
            )
        }

        et_name.doAfterTextChanged {
            if (viewModel.isNameValid(it.toString())) {
                wrap_name.error =
                    "Login must be at least 3 symbols long and contain only letters and digits"
            } else wrap_name.error = null

        }

        et_login.doAfterTextChanged {
            if (viewModel.isEmailValid(it.toString())) {
                wrap_login.error = "Incorrect email"
            } else wrap_login.error = null
        }

        et_password.doAfterTextChanged {
            if (viewModel.isPasswordValid(it.toString())) {
                wrap_password.error =
                    "Password must be at least 8 symbols long and contain only letters and digits"
            } else wrap_password.error = null
        }

        et_confirm.doAfterTextChanged {
            if (et_confirm.text.toString() != et_password.text.toString()) {
                wrap_confirm.error = "Passwords doesn't match"
            } else wrap_confirm.error = null
        }
    }
}
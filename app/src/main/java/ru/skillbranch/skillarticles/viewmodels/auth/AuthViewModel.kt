package ru.skillbranch.skillarticles.viewmodels.auth

import android.content.Context
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import ru.skillbranch.skillarticles.R
import ru.skillbranch.skillarticles.data.repositories.RootRepository
import ru.skillbranch.skillarticles.viewmodels.base.BaseViewModel
import ru.skillbranch.skillarticles.viewmodels.base.IViewModelState
import ru.skillbranch.skillarticles.viewmodels.base.NavigationCommand
import ru.skillbranch.skillarticles.viewmodels.base.Notify

class AuthViewModel @ViewModelInject constructor(
    @Assisted handle: SavedStateHandle,
    private val repository: RootRepository,
) : BaseViewModel<AuthState>(handle, AuthState()),
    IAuthViewModel {

    companion object {

        //The name must be at least 3 characters long and contain
        // only letters and numbers and can also contain the characters "-" and "_"
        val validNameRegex = """[0-9a-z_\-]{3,}""".toRegex()

        //Password must be at least 8 characters long and contain only letters and numbers
        val validPasswordRegex = """[0-9a-z]{8,}""".toRegex()
    }

    init {
        subscribeOnDataSource(repository.isAuth()) { isAuth, state ->
            state.copy(isAuth = isAuth)
        }
    }

    override fun handleLogin(login: String, pass: String, dest: Int?) {
        launchSafety(null, { navigate(NavigationCommand.FinishLogin(dest)) }) {
            repository.login(login, pass)
        }
    }

    fun handleRegister(
        name: String,
        login: String,
        password: String,
        dest: Int?,
        context: Context
    ) {
        if (name.isEmpty() || login.isEmpty() || password.isEmpty()) {
            notify(
                Notify.ErrorMessage(context.getString(R.string.reg_err__empty_field))
            )
            return
        }

        if (!isNameValid(name)) {
            notify(
                Notify.ErrorMessage(context.getString(R.string.reg_err__invalid_name))
            )
            return
        }
        if (!isEmailValid(login)) {
            notify(
                Notify.ErrorMessage(context.getString(R.string.reg_err__invalid_email))
            )
            return
        }

        if (!isPasswordValid(password)) {
            notify(
                Notify.ErrorMessage(context.getString(R.string.reg_err__invalid_password))
            )
            return
        }

        launchSafety(null, { navigate(NavigationCommand.FinishLogin(dest)) }) {
            repository.register(name, login, password)
        }
    }

    fun isNameValid(name: String): Boolean =
        name.isNotEmpty() && name.matches(validNameRegex)

    fun isEmailValid(email: String): Boolean =
        email.isNotEmpty() && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()


    fun isPasswordValid(password: String): Boolean =
        password.isNotEmpty() && password.matches(validPasswordRegex)
}

data class AuthState(val isAuth: Boolean = false) : IViewModelState
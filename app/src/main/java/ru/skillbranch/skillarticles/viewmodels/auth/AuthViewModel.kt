package ru.skillbranch.skillarticles.viewmodels.auth

import androidx.lifecycle.SavedStateHandle
import ru.skillbranch.skillarticles.data.repositories.RootRepository
import ru.skillbranch.skillarticles.viewmodels.base.BaseViewModel
import ru.skillbranch.skillarticles.viewmodels.base.IViewModelState
import ru.skillbranch.skillarticles.viewmodels.base.NavigationCommand
import ru.skillbranch.skillarticles.viewmodels.base.Notify

class AuthViewModel(handle: SavedStateHandle) : BaseViewModel<AuthState>(handle, AuthState()),
    IAuthViewModel {
    companion object {
        //The name must be at least 3 characters long and contain
        // only letters and numbers and can also contain the characters "-" and "_"
        val validNameRegex = """[0-9a-z_\-]{3,}""".toRegex()

        //Password must be at least 8 characters long and contain only letters and numbers
        val validPasswordRegex = """[0-9a-z]{8,}""".toRegex()
    }


    private val repository = RootRepository

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

    fun handleRegister(name: String, login: String, password: String, dest: Int?) {
        if (!name.isNameValid()) {
            notify(
                Notify.ErrorMessage(
                    """The name must be at least 3 characters long and contain only letters and numbers and can also contain the characters "-" and "_""""
                )
            )
            return
        }
        if (!login.isEmailValid()) {
            notify(
                Notify.ErrorMessage(
                    "Incorrect Email entered"
                )
            )
            return
        }

        if (!password.isPasswordValid()) {
            notify(
                Notify.ErrorMessage(
                    "Password must be at least 8 characters long and contain only letters and numbers"
                )
            )
            return
        }

        launchSafety(null, { navigate(NavigationCommand.FinishLogin(dest)) }) {
            repository.register(name, login, password)
        }
    }

    private fun String.isNameValid(): Boolean =
        this.isNotEmpty() && this.matches(validNameRegex)

    private fun String.isEmailValid(): Boolean =
        this.isNotEmpty() && android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()


    private fun String.isPasswordValid(): Boolean =
        this.isNotEmpty() && this.matches(validPasswordRegex)
}

data class AuthState(val isAuth: Boolean = false) : IViewModelState
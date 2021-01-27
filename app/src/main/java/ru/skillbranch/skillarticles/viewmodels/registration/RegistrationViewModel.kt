package ru.skillbranch.skillarticles.viewmodels.registration

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.skillbranch.skillarticles.R
import ru.skillbranch.skillarticles.data.repositories.RootRepository
import ru.skillbranch.skillarticles.viewmodels.base.BaseViewModel
import ru.skillbranch.skillarticles.viewmodels.base.IViewModelState
import ru.skillbranch.skillarticles.viewmodels.base.NavigationCommand
import ru.skillbranch.skillarticles.viewmodels.base.Notify
import javax.inject.Inject

@HiltViewModel
class RegistrationViewModel @Inject constructor(
    handle: SavedStateHandle,
    private val repository: RootRepository,
) : BaseViewModel<RegistrationState>(handle, RegistrationState()) {

    fun handleRegisterClick(
        context: Context,
        dest: Int?,
    ) {
        with(currentState) {
            when {
                name.isBlank() || email.isBlank() || password.isBlank() ->
                    notify(Notify.ErrorMessage(context.getString(R.string.reg_err__empty_field)))
                !isNameValid ->
                    notify(Notify.ErrorMessage(context.getString(R.string.reg_err__invalid_name)))
                !isEmailValid ->
                    notify(Notify.ErrorMessage(context.getString(R.string.reg_err__invalid_email)))
                !isPasswordValid ->
                    notify(Notify.ErrorMessage(context.getString(R.string.reg_err__invalid_password)))
                !arePasswordsTheSame ->
                    notify(Notify.ErrorMessage(context.getString(R.string.reg_err__mismatching_passwords)))
                else -> launchSafely(null, { navigate(NavigationCommand.FinishLogin(dest)) }) {
                    repository.register(name, email, password)
                }
            }
        }
    }

    fun unmaskFirstPassword() {
        updateState {
            it.copy(
                isSecondPasswordVisible = !it.isSecondPasswordVisible,
                arePasswordsTheSame = arePasswordsTheSame(
                    it.password,
                    it.secondPassword,
                    !it.isSecondPasswordVisible
                ),
            )
        }
    }

    fun handleNameChange(newText: String) {
        updateState {
            it.copy(
                name = newText,
                isNameValid = isNameValid(newText),
            )
        }
    }

    fun handleLoginChange(newText: String) {
        updateState {
            it.copy(
                email = newText,
                isEmailValid = isEmailValid(newText),
            )
        }
    }

    fun handlePasswordChange(newText: String) {
        updateState {
            it.copy(
                password = newText,
                isPasswordValid = isPasswordValid(newText),
                arePasswordsTheSame = arePasswordsTheSame(
                    newText,
                    it.secondPassword,
                    it.isSecondPasswordVisible
                ),
            )
        }
    }

    fun handleSecondPasswordChange(newText: String) {
        updateState {
            it.copy(
                secondPassword = newText,
                arePasswordsTheSame = arePasswordsTheSame(
                    it.password,
                    newText,
                    it.isSecondPasswordVisible
                ),
            )
        }
    }

    private fun isNameValid(name: String): Boolean =
        name.isNotEmpty() && name.matches(validNameRegex)

    private fun isEmailValid(email: String): Boolean =
        email.isNotEmpty() && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()

    private fun isPasswordValid(password: String): Boolean =
        password.isNotEmpty() && password.matches(validPasswordRegex)

    private fun arePasswordsTheSame(
        firstPassword: String,
        secondPassword: String,
        isSecondPasswordVisible: Boolean
    ): Boolean = when {
        !isSecondPasswordVisible -> true
        else -> firstPassword == secondPassword
    }


    companion object {
        //The name must be at least 3 characters long and contain
        // only letters and numbers and can also contain the characters "-" and "_"
        val validNameRegex = """[0-9a-z_\-]{3,}""".toRegex()

        //Password must be at least 8 characters long and contain only letters and numbers
        val validPasswordRegex = """[0-9a-z]{8,}""".toRegex()
    }
}

data class RegistrationState(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val secondPassword: String = "",
    val isSecondPasswordVisible: Boolean = true,
    val isNameValid: Boolean = true,
    val isEmailValid: Boolean = true,
    val isPasswordValid: Boolean = true,
    val arePasswordsTheSame: Boolean = true,
) : IViewModelState
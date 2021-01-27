package ru.skillbranch.skillarticles.viewmodels.auth

import androidx.lifecycle.SavedStateHandle
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.skillbranch.skillarticles.data.repositories.RootRepository
import ru.skillbranch.skillarticles.viewmodels.base.BaseViewModel
import ru.skillbranch.skillarticles.viewmodels.base.IViewModelState
import ru.skillbranch.skillarticles.viewmodels.base.NavigationCommand
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    handle: SavedStateHandle,
    private val repository: RootRepository,
) : BaseViewModel<AuthState>(handle, AuthState()),
    IAuthViewModel {

    init {
        subscribeOnDataSource(repository.isAuth()) { isAuth, state ->
            state.copy(isAuth = isAuth)
        }
    }

    override fun handleLogin(login: String, pass: String, dest: Int?) {
        launchSafely {
            val isLoggingSuccessful = repository.login(login, pass)
            withContext(Dispatchers.Main) {
                if (isLoggingSuccessful) navigate(NavigationCommand.FinishLogin(dest))
            }
        }
    }
}

data class AuthState(val isAuth: Boolean = false) : IViewModelState
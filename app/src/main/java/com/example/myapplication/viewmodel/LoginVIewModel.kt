package com.example.myapplication.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.util.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.models.User
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginVIewModel @Inject constructor(
    private val client : ChatClient
) : ViewModel() {

    private val _loginEvent = MutableSharedFlow<LoginEvents>()
    val loginEvent = _loginEvent.asSharedFlow()

    private val _loadingState = MutableLiveData<UiLoadingState>()
    val loadingState:LiveData<UiLoadingState>
          get() = _loadingState

    private fun isValideUserName(userName: String): Boolean {
        return userName.length >= Constants.MIN_USERNAME_LENGTH
    }

    fun loginUser(userName: String, token: String? = null) {

        val trimmedUserName = userName.trim()

        viewModelScope.launch {
            if (isValideUserName(trimmedUserName) && token != null) {
                loginRegisteredUser(trimmedUserName, token)
            } else {
                if ((isValideUserName(trimmedUserName) && token == null)) {
                    loginGuestUser(trimmedUserName)
                } else {
                    _loginEvent.emit(LoginEvents.ErrorInputTooShort)

                }
            }
        }
    }

//cases  in the view model for states or smth like that
    sealed class LoginEvents {
        object ErrorInputTooShort : LoginEvents()
        data class ErrorLogIn(val error: String) : LoginEvents()
        object Success : LoginEvents()
    }
    sealed class UiLoadingState{
        object Loading :UiLoadingState()
        object NotLoading:UiLoadingState()
    }

    private fun loginRegisteredUser(userName: String, token: String) {
        val user = User(id = userName, name = userName)

        _loadingState.value = UiLoadingState.Loading
        client.connectUser(
            user = user,
            token = token
        ).enqueue { result ->
            _loadingState.value = UiLoadingState.NotLoading

            if (result.isSuccess) {
                viewModelScope.launch {
                    _loginEvent.emit(LoginEvents.Success)
                }
            } else {
                viewModelScope.launch {
                    _loginEvent.emit(
                        LoginEvents.ErrorLogIn(
                            result.error().message ?: "Unknown Error"
                        )
                    )
                }
            }

        }
    }

    private fun loginGuestUser(userName: String) {

        _loadingState.value = UiLoadingState.Loading

        client.connectGuestUser(
            userId = userName,
            username = userName
        ).enqueue { result ->
            _loadingState.value = UiLoadingState.NotLoading

            if (result.isSuccess) {
                viewModelScope.launch {
                    _loginEvent.emit(LoginEvents.Success)
                }
            } else {
                viewModelScope.launch {
                    _loginEvent.emit(
                        LoginEvents.ErrorLogIn(
                            result.error().message ?: "Unknown Error"
                        )
                    )
                }
            }
        }

    }
}
package com.sunueric.tableauapp.ui.viewmodels

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.sunueric.tableauapp.network.AuthService
import kotlinx.coroutines.launch

class AuthViewModel(private val authService: AuthService) : ViewModel() {
    val accessToken = MutableLiveData("")
    val isAuthenticated = MutableLiveData(false)

    init {
        isAuthenticated.value = false // Not authenticated by default
    }

    fun authenticateIfNeeded(url: String) {
        if (requiresAuthentication(url)) {
            if (accessToken.value.isNullOrEmpty()) {
                signInAutomatically()
            }
        } else {
            isAuthenticated.value = true // No authentication needed for public URLs
        }
    }

    private fun requiresAuthentication(url: String): Boolean {
        return url.contains("/private/") // placeholder condition
    }

    private fun signInAutomatically() {
        viewModelScope.launch {
            val response = authService.signIn("user@example.com", "password").execute()
            if (response.isSuccessful && response.body() != null) {
                accessToken.value = response.body()!!.accessToken
                isAuthenticated.value = true
            } else {
                isAuthenticated.value = false
            }
        }
    }
}

class AuthViewModelFactory(private val authService: AuthService) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AuthViewModel(authService) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}


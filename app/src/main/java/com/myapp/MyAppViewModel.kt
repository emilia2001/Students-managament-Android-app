package com.myapp

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.myapp.core.TAG
import com.myapp.core.data.UserPreferences
import com.myapp.core.data.UserPreferencesRepository
import com.myapp.todo.data.StudentRepository
import kotlinx.coroutines.launch

class MyAppViewModel(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val studentRepository: StudentRepository
) :
    ViewModel() {

    init {
        Log.d(TAG, "init")
    }

    fun logout() {
        viewModelScope.launch {
            studentRepository.deleteAll()
            userPreferencesRepository.save(UserPreferences())
        }
    }

    fun setToken(token: String) {
        studentRepository.setToken(token)
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app =
                    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as MyApplication)
                MyAppViewModel(
                    app.container.userPreferencesRepository,
                    app.container.studentRepository
                )
            }
        }
    }
}


package com.ilazar.myapp.todo.ui.students

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.ilazar.myapp.MyApplication
import com.ilazar.myapp.core.TAG
import com.ilazar.myapp.todo.data.Student
import com.ilazar.myapp.todo.data.StudentRepository
import kotlinx.coroutines.launch

sealed interface StudentsUiState {
    data class Success(val students: List<Student>) : StudentsUiState
    data class Error(val exception: Throwable?) : StudentsUiState
    object Loading : StudentsUiState
}

class StudentsViewModel(private val studentRepository: StudentRepository) : ViewModel() {
    var uiState: StudentsUiState by mutableStateOf(StudentsUiState.Loading)
        private set

    init {
        Log.d(TAG, "init")
        loadStudents()
    }

    fun loadStudents() {
        Log.d(TAG, "loadStudents...")
        viewModelScope.launch {
            uiState = StudentsUiState.Loading
            studentRepository.refresh()
            studentRepository.studentStream.collect {
                Log.d(TAG, "loadStudents collect")
                uiState = StudentsUiState.Success(it)
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app =
                    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as MyApplication)
                StudentsViewModel(app.container.studentRepository)
            }
        }
    }
}

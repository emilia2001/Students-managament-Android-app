package com.ilazar.myapp.todo.ui.student

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
import java.util.*

data class StudentUiState(
    val isLoading: Boolean = false,
    val loadingError: Throwable? = null,
    val studentId: String? = null,
    val student: Student? = null,
    val isSaving: Boolean = false,
    val savingCompleted: Boolean = false,
    val savingError: Throwable? = null,
)

class StudentViewModel(private val studentId: String?, private val studentRepository: StudentRepository) :
    ViewModel() {
    var uiState: StudentUiState by mutableStateOf(StudentUiState(isLoading = true))
        private set

    init {
        Log.d(TAG, "init")
        if (studentId != null) {
            loadStudent()
        } else {
            uiState = uiState.copy(student = Student(), isLoading = false)
        }
    }

    fun loadStudent() {
        viewModelScope.launch {
            studentRepository.studentStream.collect { students ->
                if (!uiState.isLoading) {
                    return@collect
                }
                val student = students.find { it._id == studentId }
                uiState = uiState.copy(student = student, isLoading = false)
            }
        }
    }

    fun saveOrUpdateStudent(firstName: String, lastName: String, birthDate: String, yearOfStudy: Int, scholarship: Boolean) {
        viewModelScope.launch {
            Log.d(TAG, "saveOrUpdateStudent...");
            try {
                uiState = uiState.copy(isSaving = true, savingError = null)
                val student = uiState.student?.copy(firstName = firstName, lastName = lastName, birthDate = birthDate, yearOfStudy = yearOfStudy, scholarship = scholarship)
                if (studentId == null) {
                    studentRepository.save(student!!)
                } else {
                    studentRepository.update(student!!)
                }
                Log.d(TAG, "saveOrUpdateStudent succeeded");
                uiState = uiState.copy(isSaving = false, savingCompleted = true)
            } catch (e: Exception) {
                Log.d(TAG, "saveOrUpdateStudent failed");
                uiState = uiState.copy(isSaving = false, savingError = e)
            }
        }
    }

    companion object {
        fun Factory(studentId: String?): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app =
                    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as MyApplication)
                StudentViewModel(studentId, app.container.studentRepository)
            }
        }
    }
}


package com.ilazar.myapp.todo.ui.students

import android.util.Log
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ilazar.myapp.R

@Composable
fun StudentsScreen(onStudentClick: (id: String?) -> Unit, onAddStudent: () -> Unit, onLogout: () -> Unit) {
    Log.d("StudentsScreen", "recompose")
    val studentsViewModel = viewModel<StudentsViewModel>(factory = StudentsViewModel.Factory)
    val studentsUiState = studentsViewModel.uiState
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.students)) },
                actions = {
                    Button(onClick = onLogout) { Text("Logout") }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    Log.d("StudentsScreen", "add")
                    onAddStudent()
                },
            ) { Icon(Icons.Rounded.Add, "Add") }
        }
    ) {
        when (studentsUiState) {
            is StudentsUiState.Success ->
                StudentList(studentList = studentsUiState.students, onStudentClick = onStudentClick)
            is StudentsUiState.Loading -> CircularProgressIndicator()
            is StudentsUiState.Error -> Text(text = "Failed to load students - $it, ${studentsUiState.exception?.message}")
        }
    }
}

@Preview
@Composable
fun PreviewStudentsScreen() {
    StudentsScreen(onStudentClick = {}, onAddStudent = {}, onLogout = {})
}

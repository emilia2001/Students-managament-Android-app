package com.myapp.todo.ui.students

import android.app.Application
import android.util.Log
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.myapp.core.MyNetworkStatus
import com.myapp.util.showSimpleNotification

@Composable
fun StudentsScreen(
    onStudentClick: (id: String?) -> Unit,
    onAddStudent: () -> Unit,
    onMapClick: () -> Unit,
    onLogout: () -> Unit
) {
    Log.d("StudentsScreen", "recompose")
    val studentsViewModel = viewModel<StudentsViewModel>(factory = StudentsViewModel.Factory)
    val studentsUiState = studentsViewModel.uiState
    val myNetworkStatus = viewModel<MyNetworkStatus>(
        factory = MyNetworkStatus.Factory(
            LocalContext.current.applicationContext as Application
        )
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Students online: " + " " + myNetworkStatus.uiState
                    )
                },

                actions = {
                    Button(onClick = onLogout) { Text("Logout") }
                    Button(onClick = onMapClick) { Text("Show Map") }
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
    StudentsScreen(onStudentClick = {}, onAddStudent = {}, onMapClick = {}, onLogout = {})
}

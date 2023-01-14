package com.ilazar.myapp.todo.ui.students

import android.app.Application
import android.util.Log
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ilazar.myapp.R
import com.ilazar.myapp.todo.ui.MyNetworkStatusViewModel
import com.ilazar.myapp.util.createNotificationChannel
import com.ilazar.myapp.util.showSimpleNotification

@Composable
fun StudentsScreen(
    onStudentClick: (id: String?) -> Unit,
    onAddStudent: () -> Unit,
    onLogout: () -> Unit
) {
    Log.d("StudentsScreen", "recompose")
    val studentsViewModel = viewModel<StudentsViewModel>(factory = StudentsViewModel.Factory)
    val studentsUiState = studentsViewModel.uiState
    val myNetworkStatusViewModel = viewModel<MyNetworkStatusViewModel>(
        factory = MyNetworkStatusViewModel.Factory(
            LocalContext.current.applicationContext as Application
        )
    )
    val channelId = "MyStudentChannel"
    val notificationId = 0
    val context = LocalContext.current

//    LaunchedEffect(Unit) {
//        createNotificationChannel(channelId, context)
//    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Students online: " + " " + myNetworkStatusViewModel.uiState
                    )
                },

                actions = {
                    Button(onClick = onLogout) { Text("Logout") }
                    Button(onClick = {
                        showSimpleNotification(
                            context,
                            channelId,
                            notificationId,
                            "Simple notification",
                            "This is a simple notification with default priority."
                        )
                    }, modifier = Modifier.padding(top = 16.dp)) {
                        Text(text = "Simple Notification")
                    }
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

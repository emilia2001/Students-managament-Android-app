package com.ilazar.myapp.todo.ui

import android.app.DatePickerDialog
import android.util.Log
import android.widget.DatePicker
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ilazar.myapp.R
import com.ilazar.myapp.todo.ui.student.StudentViewModel
import java.text.SimpleDateFormat
import android.content.Context
import androidx.compose.ui.platform.LocalContext
import com.ilazar.myapp.util.createNotificationChannel
import com.ilazar.myapp.util.showSimpleNotification
import java.util.*

@Composable
fun StudentScreen(studentId: String?, onClose: () -> Unit, context: Context) {
    val studentViewModel =
        viewModel<StudentViewModel>(factory = StudentViewModel.Factory(studentId))
    val studentUiState = studentViewModel.uiState
    var firstName by rememberSaveable { mutableStateOf(studentUiState.student?.firstName ?: "") }
    var lastName by rememberSaveable { mutableStateOf(studentUiState.student?.lastName ?: "") }
    var birthDate by rememberSaveable { mutableStateOf(studentUiState.student?.birthDate ?: "") }
    var yearOfStudy by rememberSaveable { mutableStateOf(studentUiState.student?.yearOfStudy ?: 0) }
    var scholarship by rememberSaveable {
        mutableStateOf(
            studentUiState.student?.scholarship ?: false
        )
    }
    Log.d(
        "StudentScreen",
        "recompose, firstName = $firstName, lastName = $lastName, birthDate = $birthDate, yearOfStudy = $yearOfStudy, scholarship = $scholarship"
    )

    LaunchedEffect(studentUiState.savingCompleted) {
        Log.d("StudentScreen", "Saving completed = ${studentUiState.savingCompleted}");
        if (studentUiState.savingCompleted) {
            onClose();
        }
    }

    var textInitialized by remember { mutableStateOf(studentId == null) }
    LaunchedEffect(studentId, studentUiState.isLoading) {
        Log.d("StudentScreen", "Saving completed = ${studentUiState.savingCompleted}");
        if (textInitialized) {
            return@LaunchedEffect
        }
        if (studentUiState.student != null && !studentUiState.isLoading) {
            firstName = studentUiState.student.firstName
            lastName = studentUiState.student.lastName
            birthDate = studentUiState.student.birthDate
            yearOfStudy = studentUiState.student.yearOfStudy
            scholarship = studentUiState.student.scholarship
            textInitialized = true
        }
    }
    val channelId = "MyStudentChannel"
    val notificationId = 0
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "$firstName $lastName") },
                actions = {
                    Button(onClick = {
                        Log.d(
                            "StudentScreen",
                            "save student firstName = $firstName lastName = $lastName"
                        );
                        showSimpleNotification(
                            context,
                            channelId,
                            notificationId,
                            "Save or update student",
                            "Changes were made for $firstName $lastName"
                        );
                        studentViewModel.saveOrUpdateStudent(
                            firstName,
                            lastName,
                            birthDate,
                            yearOfStudy,
                            scholarship
                        )
                    }) { Text("Save") }
                }
            )
        }
    ) {
        if (studentUiState.isLoading) {
            CircularProgressIndicator()
            return@Scaffold
        }
        if (studentUiState.loadingError != null) {
            Text(text = "Failed to load student - ${studentUiState.loadingError.message}")
        }

        val year: Int
        val month: Int
        val day: Int
        val calendar = Calendar.getInstance()
        year = calendar.get(Calendar.YEAR)
        month = calendar.get(Calendar.MONTH)
        day = calendar.get(Calendar.DAY_OF_MONTH)
        calendar.time = Date()
        val datePickerDialog = DatePickerDialog(
            context,
            { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
                birthDate = "$dayOfMonth/$month/$year"
            }, year, month, day
        )

        Column {
            TextField(
                value = firstName,
                onValueChange = { firstName = it }, label = { Text("First Name") },
                modifier = Modifier.wrapContentWidth()
            )
            TextField(
                value = lastName,
                onValueChange = { lastName = it }, label = { Text("Last Name") },
                modifier = Modifier.wrapContentWidth()
            )
            Row {
                TextField(
                    value = birthDate,
                    onValueChange = { birthDate = it }, label = { Text("Birth Date") },
                    modifier = Modifier.wrapContentWidth()
                )
                Button(
                    onClick = { datePickerDialog.show() },
                    shape = RoundedCornerShape(50)
                ) {
                    Icon(
                        Icons.Filled.DateRange,
                        contentDescription = "",
                        modifier = Modifier.size(ButtonDefaults.IconSize)
                    )
                }
            }
            TextField(
                value = yearOfStudy.toString(),
                onValueChange = { yearOfStudy = it.toInt() }, label = { Text("Year of Study") },
                modifier = Modifier.wrapContentWidth()
            )
            Row {
                Text(text = "Scholarship")
                Checkbox(
                    checked = scholarship,
                    onCheckedChange = { scholarship = it },
                    modifier = Modifier.wrapContentWidth()
                )
            }
        }
        if (studentUiState.isSaving) {
            Column(
                Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) { LinearProgressIndicator() }
        }
        if (studentUiState.savingError != null) {
            Text(text = "Failed to save student - ${studentUiState.savingError.message}")
        }
    }
}



package com.ilazar.myapp.todo.ui.students

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ilazar.myapp.todo.data.Student

typealias OnStudentFn = (id: String?) -> Unit

@Composable
fun StudentList(studentList: List<Student>, onStudentClick: OnStudentFn) {
    Log.d("StudentList", "recompose")
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
    ) {
        items(studentList) { student ->
            StudentDetail(student, onStudentClick)
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun StudentDetail(student: Student, onStudentClick: OnStudentFn) {
//    Log.d("StudentDetail", "recompose id = ${student._id}")
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(15.dp)
            .clickable { },
        elevation = 10.dp,
        onClick = { onStudentClick(student._id) }
    ) {
        Column {
            ClickableText(
                text = AnnotatedString("First Name: " + student.firstName),
                style = TextStyle(
                    fontSize = 24.sp,
                ),
                onClick = { onStudentClick(student._id) }
            )
            ClickableText(
                text = AnnotatedString("Last Name: " + student.lastName),
                style = TextStyle(
                    fontSize = 24.sp,
                ),
                onClick = { onStudentClick(student._id) }
            )
            ClickableText(
                text = AnnotatedString("Birth Date: " + student.birthDate.toString()),
                style = TextStyle(
                    fontSize = 24.sp,
                ),
                onClick = { onStudentClick(student._id) }
            )
            ClickableText(
                text = AnnotatedString("Year of study: " + student.yearOfStudy.toString()),
                style = TextStyle(
                    fontSize = 24.sp,
                ),
                onClick = { onStudentClick(student._id) }
            )
            ClickableText(
                text = AnnotatedString("Has scholarship: " + student.scholarship.toString()),
                style = TextStyle(
                    fontSize = 24.sp,
                ),
                onClick = { onStudentClick(student._id) }
            )
        }
    }
}

package com.myapp.todo.data.remote

import com.myapp.todo.data.Student

data class StudentEvent(val type: String, val payload: Student)

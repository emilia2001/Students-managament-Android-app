package com.ilazar.myapp.todo.data.remote

import com.ilazar.myapp.todo.data.Student

data class StudentEvent(val type: String, val payload: Student)

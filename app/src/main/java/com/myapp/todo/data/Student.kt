package com.myapp.todo.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*

@Entity(tableName = "students")
data class Student(
    @PrimaryKey val _id: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val birthDate: String = "",
    val yearOfStudy: Int = 0,
    val scholarship: Boolean = false
)

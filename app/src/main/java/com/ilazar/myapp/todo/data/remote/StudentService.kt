package com.ilazar.myapp.todo.data.remote

import com.ilazar.myapp.todo.data.Student
import retrofit2.http.*

interface StudentService {
    @GET("/api/student")
    suspend fun find(): List<Student>

    @GET("/api/student/{id}")
    suspend fun read(@Path("id") studentId: String?): Student;

    @Headers("Content-Type: application/json")
    @POST("/api/student")
    suspend fun create(@Body student: Student): Student

    @Headers("Content-Type: application/json")
    @PUT("/api/student/{id}")
    suspend fun update(@Path("id") studentId: String?, @Body student: Student): Student
}

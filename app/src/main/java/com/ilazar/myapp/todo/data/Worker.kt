package com.ilazar.myapp.todo.data

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.ilazar.myapp.core.TAG
import com.ilazar.myapp.core.data.remote.Api
import com.ilazar.myapp.todo.data.remote.StudentService

class Worker(
    context: Context,
    workerParams: WorkerParameters,
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {

        Log.d(TAG, "WORKER")

        val operation = inputData.getString("operation")
        val id = inputData.getString("id").orEmpty()
        val firstName = inputData.getString("firstName").orEmpty()
        val lastName = inputData.getString("lastName").orEmpty()
        val birthDate = inputData.getString("birthDate").orEmpty()
        val yearOfStudy = inputData.getInt("yearOfStudy", 1)
        val scholarship = inputData.getBoolean("text", false)

        val student = Student(id, firstName, lastName, birthDate, yearOfStudy, scholarship)

        val StudentService = Api.retrofit.create(StudentService::class.java)

        try {
            if (operation.equals("save")) {
                val createdStudent = StudentService.create(student)
                Log.d(TAG, "SAVE WORKER")
            } else if (operation.equals("update")) {
                val updatedStudent = StudentService.update(student._id, student)
                Log.d(TAG, "UPDATE WORKER")
            }
            return Result.success()
        } catch (e: Exception) {
            return Result.failure()
        }

    }

}

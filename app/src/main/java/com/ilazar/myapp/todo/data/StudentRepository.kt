package com.ilazar.myapp.todo.data

import android.util.Log
import com.ilazar.myapp.core.TAG
import com.ilazar.myapp.todo.data.local.StudentDao
import com.ilazar.myapp.todo.data.remote.StudentEvent
import com.ilazar.myapp.todo.data.remote.StudentService
import com.ilazar.myapp.todo.data.remote.StudentWsClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.withContext

class StudentRepository(
    private val studentService: StudentService,
    private val studentWsClient: StudentWsClient,
    private val studentDao: StudentDao
) {
    val studentStream by lazy { studentDao.getAll() }

    init {
        Log.d(TAG, "init")
    }

    suspend fun refresh() {
        Log.d(TAG, "refresh started")
        try {
            val students = studentService.find()
            studentDao.deleteAll()
            students.forEach { studentDao.insert(it) }
            Log.d(TAG, "refresh succeeded")
        } catch (e: Exception) {
            Log.w(TAG, "refresh failed", e)
        }
    }

    suspend fun openWsClient() {
        Log.d(TAG, "openWsClient")
        withContext(Dispatchers.IO) {
            getStudentEvents().collect {
                Log.d(TAG, "Student event collected $it")
                if (it.isSuccess) {
                    val studentEvent = it.getOrNull();
                    when (studentEvent?.type) {
                        "created" -> handleStudentCreated(studentEvent.payload)
                        "updated" -> handleStudentUpdated(studentEvent.payload)
                        "deleted" -> handleStudentDeleted(studentEvent.payload)
                    }
                }
            }
        }
    }

    suspend fun closeWsClient() {
        Log.d(TAG, "closeWsClient")
        withContext(Dispatchers.IO) {
            studentWsClient.closeSocket()
        }
    }

    suspend fun getStudentEvents(): Flow<Result<StudentEvent>> = callbackFlow {
        Log.d(TAG, "getStudentEvents started")
        studentWsClient.openSocket(
            onEvent = {
                Log.d(TAG, "onEvent $it")
                if (it != null) {
                    trySend(Result.success(it))
                }
            },
            onClosed = { close() },
            onFailure = { close() });
        awaitClose { studentWsClient.closeSocket() }
    }

    suspend fun update(student: Student): Student {
        Log.d(TAG, "update $student...")
        val updatedStudent = studentService.update(student._id, student)
        Log.d(TAG, "update $student succeeded")
        handleStudentUpdated(updatedStudent)
        return updatedStudent
    }

    suspend fun save(student: Student): Student {
        Log.d(TAG, "save $student...")
        val createdStudent = studentService.create(student)
        Log.d(TAG, "save $student succeeded")
        handleStudentCreated(createdStudent)
        return createdStudent
    }

    private suspend fun handleStudentDeleted(student: Student) {
        Log.d(TAG, "handleStudentDeleted - todo $student")
    }

    private suspend fun handleStudentUpdated(student: Student) {
        Log.d(TAG, "handleStudentUpdated...")
        studentDao.update(student)
    }

    private suspend fun handleStudentCreated(student: Student) {
        Log.d(TAG, "handleStudentCreated...")
        studentDao.insert(student)
    }

    suspend fun deleteAll() {
        studentDao.deleteAll()
    }

    fun setToken(token: String) {
        studentWsClient.authorize(token)
    }
}
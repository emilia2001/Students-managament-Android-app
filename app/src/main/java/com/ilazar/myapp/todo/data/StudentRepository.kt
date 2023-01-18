package com.ilazar.myapp.todo.data

import android.util.Log
import androidx.work.*
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
        try {
            Log.d(TAG, "update $student...")
            val updatedStudent = studentService.update(student._id, student)
            Log.d(TAG, "update $student succeeded")
            handleStudentUpdated(updatedStudent)
            return updatedStudent
        } catch (e: Exception) {
            Log.w(TAG, "update - failed", e)
            handleStudentUpdated(student)
            createWorker(student, "update")
            return student
        }
    }

    suspend fun save(student: Student): Student {
        try {
            Log.d(TAG, "save $student...")
            val createdStudent = studentService.create(student)
            Log.d(TAG, "save $student succeeded")
            handleStudentCreated(createdStudent)
            return createdStudent
        } catch (e: Exception) {
            Log.w(TAG, "save - failed", e)
            handleStudentCreated(student)
            createWorker(student, "save")
            return student
        }
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

    fun createWorker(student: Student, operation: String) {
        Log.d(TAG, "CREATE WORKER")

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        Log.d(TAG, "CREATE WORKER")

        val inputData = Data.Builder()
            .putString("operation", operation)
            .putString("id", student._id)
            .putString("firstName", student.firstName)
            .putString("lastName", student.lastName)
            .putString("birthDate", student.birthDate)
            .putInt("yearOfStudy", student.yearOfStudy)
            .putBoolean("scholarship", student.scholarship)
            .build()

        val myWork = OneTimeWorkRequest.Builder(Worker::class.java)
            .setConstraints(constraints)
            .setInputData(inputData)
            .build()

        WorkManager.getInstance().enqueue(myWork);
    }
}